#include "StdAfx.h"

#include ".\CMARFPTree.h"
#include "..\JEP-ALG\Dataset.h"

extern bool debug;
extern bool crossdebug;
extern bool reducingInsertion;

//////////////////////////////////////////////////////////////////////////////////////////////////////
// konstruktor
// jako parametry bierze zestaw danych, prog wsparcia, ilosc wystepujacych klas oraz
// (ewnetualnie) prefix drzewa, jesli ma to byc drzewo dla danych warunkowych.
// znajduje elementy czeste i buduje drzewo, wraz z tabela naglowkowa (HeaderTable)
//////////////////////////////////////////////////////////////////////////////////////////////////////
CMARFPTree::CMARFPTree(BinaryPointersDataset * dat, int suppTH, int cl, BinaryPointersItemset* con) : 
CMARFPTreeLeaf(cl)
, classLablelsSupports(NULL)
, datasetSize(0)
{
	if (con) {
		conditionalTo = new BinaryPointersItemset(*con);
	} else {
		conditionalTo = new BinaryPointersItemset();
	}

	// nalezy uzyskac posortowana liste czestych transakcji
	// oraz czestosc wystepowania poszczegolnych klas
	dat->getFPListAndClassDistribution(&FPList, suppTH, &classLablelsSupports, cl);
	datasetSize = dat->GetCount();

	// jesli lista FPList (czeste przedmioty) jest pusta, to drzewo tez bedzie puste
	if (!FPList->getCount()) return;

	// wpisywanie do drzewa kolejnych obiektow ze zbioru danych
	for (int i = 0; i < dat->GetCount(); i++) {
		BinaryPointersItemset *tmp = (BinaryPointersItemset *) dat->GetAt(i);
		BinaryPointersItemset *newone = new BinaryPointersItemset(*tmp);

		// nalezy usunac 
		newone->sortAndPruneAs(FPList);
		insertTree(newone, 0, &headerTable);
		delete newone;
	}

	// posortowanie elementow 
	sortHeader();

	binAttrCnt = dat->binaryAttributes;
	supportTH = suppTH;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// destruktor
//////////////////////////////////////////////////////////////////////////////////////////////////////
CMARFPTree::~CMARFPTree(void)
{
	delete classLablelsSupports;
	delete conditionalTo;
	delete FPList;
	
	// usuniecie naglowkow
	for (int i = 0; i < headerTable.GetCount(); i++) {
		delete headerTable.GetAt(i);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// funkcja podlagladowa. wyswietla informacje o drzewie, wyswietla tez cale drzewo
//////////////////////////////////////////////////////////////////////////////////////////////////////
void CMARFPTree::debugPrint() {
	printTree(true);
	CString s;
	for (int i = 0; i < headerTable.GetCount(); i++) {
		CMARFPTreeHeaderTableElement *elem = (CMARFPTreeHeaderTableElement *) headerTable.GetAt(i);
		s.Format("HT Element %d", i);
		DebugPrinter::print(s);
		for (int j = 0; j < elem->GetCount(); j++) {
			CMARFPTreeLeaf *fptl = (CMARFPTreeLeaf *) elem->GetAt(j);
			fptl->debugPrint();
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// sortowanie elementow w tablicy naglowkow
//////////////////////////////////////////////////////////////////////////////////////////////////////
void CMARFPTree::sortHeader() {
	// tworze nowa kopie tablicy naglowkow, gdzie bede umieszczal elementy starej tablicy
	// wg. okreslonego w FPList porzadku
	CArray <CMARFPTreeHeaderTableElement*> newTable;

	for (int i = 0; i < FPList->getCount(); i++) {

		// pobieram kolejny element z FPList i wyszkuje odpowiadajacego elementu
		// w tablicy naglowkow
		int toFind = FPList->getItem(i);
		for (int j = 0; j < headerTable.GetCount(); j++) {
			CMARFPTreeHeaderTableElement *elem = (CMARFPTreeHeaderTableElement *) headerTable.GetAt(j);
			if (toFind == elem->attribute) {

				// znaleziony element wpisuje do nowej tablicy
				newTable.Add(elem);
				break;
			}
		}
	}
	headerTable.RemoveAll();
	headerTable.Append(  newTable);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// wydobywanie regul i wypelnienie nimi drzewa CR
// pobiera argumenty: prog zaufania (w %), ewnetualnie drzewo wynikowe, ostatni argument
// jest zarezerwowany
//////////////////////////////////////////////////////////////////////////////////////////////////////
CRTree* CMARFPTree::FillCRTree(float confTH, CRTree* res, bool *rulesAdded) {
	// utworzenie kontenera zwrotnego
	if (!res) {
		res = new CRTree(classLablelsSupports, classCount, datasetSize, binAttrCnt);
	}

	if (rulesAdded) {
		*rulesAdded = false;
	}

	// jesli drzewo zawiera jedna sciezke to drzewa warukowe nie beda juz budowane
	// nalezy wybrac z niej wszystkie mozliwe reguly
	if (pathsCount == 1) {

		// wybranie ze sciezki mozliwie najdluzszych regul
		Dataset *dat = getPathLongestSubsets(true);

		// sprawdzamy kazda z nich pod wzgledem zaufania
		for (int i = 0; i < dat->GetCount(); i++) {
			BinaryPointersItemset *b = (BinaryPointersItemset*) dat->GetAt(i);	
			if (computeConf(b->conf, b->supp) > confTH) {

				// poszerzam uzyskane reguly o przefix aktualnego drzewa
				*b += *conditionalTo;
				bool isAdded = false;

				// warunek spelniony, wstawienie do CR-drzewa
				if (reducingInsertion) {
					res->insertRuleWithPruning(b, &isAdded);
				} else {
					res->insertRule(b, 0, &res->headerTable); 
					isAdded = true;
				}
				if (isAdded) {
					if (rulesAdded) {
						*rulesAdded = true;
					}
				}
			}
		}
		dat->deleteAll();
		delete dat;
	} else {
		// drzewo zawiera kilka sciezek, konieczne jest budowanie drzew warunkowych
		// operacja ta wykonywana jest dla kazdego z elementow tablicy naglowkow
		// (pobierane sa one od konca listy)
		for (int i = (int)headerTable.GetCount() - 1; i >= 0; i--) {

			// wykonanie kopii prefiksu obecnego drzewa
			BinaryPointersItemset *b = new BinaryPointersItemset (*conditionalTo);
			CMARFPTreeHeaderTableElement* HTelem = (CMARFPTreeHeaderTableElement*)headerTable.GetAt(i);

			// do uzyskanej kopii dodaje sie element odpowiadajacy obecnemu elementowi
			// z tablicy naglowkow
			b->setItem(HTelem->attribute);

			// uporzadkowanie wg FPList
			b->sortAndPruneAs(FPList);

			// wsparcie prefiksu dla nowego drzewa jest obliczane jako
			// suma wsparc wszystkich wystapien elementu, odpowiadajacego danemu elementowi
			// z tablicy naglowkow, wystepujacych w obecnym drzewie
			b->supp = HTelem->computeSupport();

			// jego klasa ustawiana jako ta najbardziej dominujaca sposrod tych elementow
			// nalezy zwrocic uwage ze sensowne sa tylko reguly z zaufaniem > 50%
			// z czego wynika ze nie moze byc dwoch klas dominujacych jednakowo.
			// ustalenie klasy jest konieczne - dzieki temu bedzie wiadomo jaka klase
			// maja miec reguly wygenerowane pozniej z tego drzewa warunkowego
			HTelem->computeMostDominantClass(&b->cl, &b->conf);

			BinaryPointersDataset * dat = new BinaryPointersDataset(binAttrCnt);
			
			// budowa drzewa warukowego
			// dla kazdego elementu na danej pozycji tablicy naglowkow
			// 'patrzy sie w gore' aby uzystac elementy do nowej bazy
			for (int j = 0; j < HTelem->GetCount(); j++) {
				CMARFPTreeLeaf *leaf = (CMARFPTreeLeaf *) HTelem->GetAt(j);
				
				// pobranie elementow od tego wezla w gore
				// moze byc ich kilka, o ile po drodze w gore zmieniaja sie wsparcia
				// w kolejnych wezlach oraz etykiety klas
				BinaryPointersDataset *d1 = leaf->getItemsetsUpward();
				dat->Append(*d1);
				delete d1;
			}

			// baza jest juz utworzona, mozna budowac drzewo warunkowe
			CMARFPTree* tmpTree = new CMARFPTree(dat, supportTH, classCount, b);

			// czy nowe drzewo wynegerowalo jakies reguly?
			bool added = false;

			// jesli drzewo jest niepuste, wydobadz z niego reguly
			if (!tmpTree->subNodes.IsEmpty()) {
				tmpTree->FillCRTree(confTH, res, &added);
			} 

			if (!added) {
				// nie dodano nowych regul z drzewa warukowego
				// mozna spronbowac dodac sam prefiks jako regule
				if (b->cl > -1) 
					if (computeConf(b->conf, b->supp) > confTH) {
						bool b1 = false;
						if (reducingInsertion) {
							res->insertRuleWithPruning(b, &b1);
						} else {
							res->insertRule(b, 0, &res->headerTable); 
							b1 = true;
						}
						if (b1) {
							if (rulesAdded) {
								*rulesAdded = true;
							}
						}
					}
			} else {
				// zaznaczam ze jakas regula zostala dodana
				if (rulesAdded) {
					*rulesAdded = true;
				}
			}

			// usuniecie kopii prefiksu, warunkowej bazy danych i warunkowego drzewa
			delete b;
			dat->deleteAll();
			delete dat;
			delete tmpTree;

			// zlaczenie lisci drzewa odpowiadajacych aktualnej pozycji w tablicy naglowkow
			// z wezlami nadrzednymi
			HTelem->meldNodesIntoParents();
		}
	}
	return res;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// obliczenie zaufania procentowego
//////////////////////////////////////////////////////////////////////////////////////////////////////
float CMARFPTree::computeConf(int p_conf, int p_supp)
{
	return ((float)p_conf) / ((float) p_supp) * 100;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// dokonanie redukcji opartej na pokryciu bazy
//////////////////////////////////////////////////////////////////////////////////////////////////////
CRTree* CMARFPTree::pruneBasedOnCoverage(CRTree * tree, BinaryPointersDataset* base, int coverageTH, int* rc1, int* rc2)
{
	// uzyskanie listy regul
	BinaryPointersDataset* rules = new BinaryPointersDataset(binAttrCnt);
	tree->generateRuleDataset(rules);
	if (rc1) {
		*rc1 = rules->GetCount();
	}
	if (crossdebug) printf("\tWygenerowano %d regul przed ostatnia redukcja\n", rules->GetCount());
	// reguly musza zostac posortowane wg. rangi. przez wybieranie
	BinaryPointersDataset* sortedRules = new BinaryPointersDataset(binAttrCnt);
	int insertedRules = 0;
	int rulesToInsert = rules->GetCount();

	// powtarzanie az wsyzstkie reguly zostana przeniesione
	while (insertedRules < rulesToInsert) {
		int numOfRule = 0;
		BinaryPointersItemset * theRule1 = (BinaryPointersItemset *) rules->GetAt(numOfRule);

		for (int j = 1; j < rules->GetCount(); j++) {
			BinaryPointersItemset * theRule2 = (BinaryPointersItemset *) rules->GetAt(j);
			
			// ustalam czy R2 dominuje R1. jest to mozliwe, poniewaz pomijam tu etykiety klas
			bool higher2than1 = false;
			bool equal = false;
			int cmpRes = tree->isHigherRank(theRule2->conf, theRule2->supp, 
				theRule1->conf, theRule1->supp, 0, false);
			if (cmpRes > 0) {
				higher2than1 = true;
			} else if (cmpRes == 0) {
				if (theRule2->getCount() < theRule1->getCount()) {
					higher2than1 = true;
				} else if (theRule2->getCount() == theRule1->getCount()) {
					equal = true;
				}
			}

			if (equal) continue;

			if (higher2than1) {
				theRule1 = theRule2;
				numOfRule = j;
				continue;
			}
		}

		// wybrano maksymalna regule z pozostalego zbioru regul nieposortowanych
		// wstawiam ja do nowej listy

		rules->RemoveAt(numOfRule);
		insertedRules++;
		sortedRules->Add(theRule1);
	}

	// stary kontener juz niepotrzebny
	delete rules;

	// kopia bazy
	BinaryPointersDataset * tmpBase = new BinaryPointersDataset(binAttrCnt);
	tmpBase->deepCopyItemsetFrom(base);

	BinaryPointersDataset* nonPrunedRules = new BinaryPointersDataset(binAttrCnt);

	while (!sortedRules->IsEmpty() && !tmpBase->IsEmpty()) {
		BinaryPointersItemset * theRule = (BinaryPointersItemset *) sortedRules->GetAt(0);

		// dla kazdej reguly sprawdzam pokrycie
		bool covered = false;
		for (int j = 0; j < tmpBase->GetCount(); j++) {
			BinaryPointersItemset* item = (BinaryPointersItemset*) tmpBase->GetAt(j);
			if (theRule->cl == item->cl) {
				if (theRule->subsetOrEqualTo(*item)) {
					covered = true;
					item->coverage++;
					if (item->coverage >= coverageTH) {
						tmpBase->RemoveAt(j);
						delete item;
						j--;
						continue;
					}
				} // if subset
			} //if cl
		} // for

		sortedRules->RemoveAt(0);
		if (covered) {
			nonPrunedRules->Add(theRule);
		} else {
			delete theRule;
		}
	} // while
	if (rc2) {
		*rc2 = nonPrunedRules->GetCount();
	}

	tmpBase->deleteAll();
	delete tmpBase;
	sortedRules->deleteAll();
	delete sortedRules;

	if (crossdebug) printf("\tOstatecznie uzyskano %d regul\n", nonPrunedRules->GetCount());

	// mam juz liste nie zredukowanych regul
	// nalezy z nich teraz utworzyc drzewo
	CRTree* res = new CRTree(tree->classLablelsSupports, tree->classCount, tree->datasetSize, tree->binAttrs);

	for (int i = 0; i < nonPrunedRules->GetCount(); i++) {
		BinaryPointersItemset * theRule = (BinaryPointersItemset *) nonPrunedRules->GetAt(i);

		// TODO:
		// jeszcze trzeba tylko uporzadkowac. reguly po wyciagnieciu z CRdrzewa sa odwrocone
		theRule->sortAndPruneAs(FPList);
		res->insertRule(theRule, 0, &res->headerTable);
	}

	nonPrunedRules->deleteAll();
	delete nonPrunedRules;

	return res;
}
