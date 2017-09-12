// JEP.cpp: implementation of the JEP class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "JEP.h"

#include "Itemset.h"
#include "Dataset.h"
#include "ProgressStep.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////


	// dla kazdej klasy i:
	// 1. utworz zbior danych Di z danymi z tej klady
	// 2. utworz zbior danych Du = U(Dj)|j!=i
	// 3. utworz granice horyzontalne dla Di oraz Du
	// 4. odejmij granice (MDB-LLBorder) Di-Du
	// 5. powstale granice zbierz w przestrzen
	// 6. do klasyfikacji uzywamy dolnych granic z wygenerowanej przestrzeni
 


//////////////////////////////////////////////////////////////////////
// dane przechowywane w obiekcie
// 
// horizBorders	:	CObArrayExt of Borders [ilosc_klas]
//					Borders		:	lBorder : Dataset of Itemset
//									rBorder : Dataset of Itemset
// 
// JEPSpace		:	CObArrayExt of ConvexSpace [ilosc_klas]
//					ConvexSpace	: CObArrayExt of Borders
// 
// train_d, test_d:	Dataset
// 
//////////////////////////////////////////////////////////////////////


JEP::JEP(int nclass, Dataset *dat)
{
	train_d = dat;
	class_n = nclass;
	initBorders();	
	initJEPSpace();	
	b_horiz = false;
	b_jeps = false;
}

JEP::~JEP()
{
	cleanBorders();
	cleanJEPSpace();
}


//////////////////////////////////////////////////////////////////////
// konieczna do wywolania przed obliczeniem przestrzeni JEP
// wynik pojawi sie w zmiennej czlonkowskiej horizBorders
//////////////////////////////////////////////////////////////////////
int JEP::generateHorizBorders(ProgressStep *prog)
{
	cleanBorders();
	initBorders();

	// iteruje wszystkie klasy
	for (int cl = 0; cl < class_n; cl++)
	{
		// do Di kopiuje wszyskie z danej klasy
		Dataset *Di = new Dataset();
		// do Du wszystkie pozostale
		Dataset *Du = new Dataset();

		for (int m = 0; m < train_d->GetSize(); m++)
		{
			int icl = ((Itemset*)(train_d->GetAt(m)))->cl - 1;

			if (icl == cl)
				Di->Add((Itemset*)(train_d->GetAt(m)));
			else 
				Du->Add((Itemset*)(train_d->GetAt(m)));
		}

		if (prog) prog->StepIt();

		Dataset *maxDi = getMaxItemsets(Di);
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDi.rBorder.RemoveAll();
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDi.rBorder.Append(*maxDi);
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDi.lBorder.RemoveAll();
		delete maxDi;

		if (prog) prog->StepIt();

		Dataset *maxDu = getMaxItemsets(Du);
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDu.rBorder.RemoveAll();
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDu.rBorder.Append(*maxDu);
		((DiDuBorders*)horizBorders.GetAt(cl))->bordersDu.lBorder.RemoveAll();
		delete maxDu;

		if (prog) prog->StepIt();

		delete Di;
		delete Du;
	}
	
	b_horiz = true;
	return 0;
}

//////////////////////////////////////////////////////////////////////
// wybiera elementy maksymalne ze zbioru
// wynikiem jest nowo zaalokowany zbior danych
//////////////////////////////////////////////////////////////////////
Dataset* JEP::getMaxItemsets(Dataset *d)
{
	// wyszukuje maksymalne elementy w danych
	int n = d->GetSize();

	// w tej tablicy beda oznaczane elementy - 1 jest maksymalny, 0 - nie jest
	char * ismax = new char [n];
	// inicjalnie wszystkie przyjmuje jako max
	memset(ismax, 1, sizeof(char) * n);

	Itemset* i_is, *j_is;
	// eliminujemy elementy nie maksymalne, niestety n2 :(
	for (int i = 0; i < n; i++)
		if (ismax[i])
			for (int j = 0; j < n; j++)
				if (j != i) if (ismax[j])
				{
					i_is = (Itemset*)d->GetAt(i);
					j_is = (Itemset*)d->GetAt(j);
					if ( i_is->superOrEqTo(*j_is) )
						// element i jest super do j lub sa rowne - j odpada
						ismax[j] = 0;
				}
	
	// dodaje do utworzonej listy zaznaczone elementy
	// NIE tworzy duplikatow elemetow
	Dataset* itlist = new Dataset();
	for (int k = 0; k < n; k++)
		if (ismax[k])
			itlist->Add(d->GetAt(k));
	
	delete ismax;
	return itlist;	
}

//////////////////////////////////////////////////////////////////////
// inicjalizacjia granic horyzontalnych
// dla kazdej klasy dodaje sie granice horyz. poztywne i negatywne
//////////////////////////////////////////////////////////////////////
void JEP::initBorders()
{
	for (int i = 0; i < class_n; i++) 
		horizBorders.Add(new DiDuBorders());
}

//////////////////////////////////////////////////////////////////////
// czyszczenie zawartosci granic horyz.
//////////////////////////////////////////////////////////////////////
void JEP::cleanBorders()
{
	horizBorders.DeleteAll();
}

void JEP::initJEPSpace()
{
	for (int i = 0; i < class_n; i++) 
		JEPSpace.Add(new ConvexSpace());
}

//////////////////////////////////////////////////////////////////////
// czyszczenie zawartosci przestrzeni JEP
//////////////////////////////////////////////////////////////////////
void JEP::cleanJEPSpace()
{
	for (int i = 0; i < class_n; i++)
	{
		ConvexSpace* cs = (ConvexSpace*)JEPSpace.GetAt(i);
		for (int b = 0; b < cs->GetSize(); b++)
		{
			Borders* bs = (Borders*)cs->GetAt(b);
			bs->lBorder.deleteAll();
			bs->rBorder.deleteAll();
		}
		cs->DeleteAll();
	}
	JEPSpace.DeleteAll();
}

//////////////////////////////////////////////////////////////////////
// implementacja MDB-LLBORDER
// obliczanie przestrzeni JEP
// wynik w zmiennej czlonkowskiej JEPSpace
//////////////////////////////////////////////////////////////////////
int JEP::findJEPs(ProgressStep *prog)
{
	if (!b_horiz) return 1;

	cleanJEPSpace();
	initJEPSpace();
	
	// algorytm MDB_LLBORDER
	// dla kazdej z klas
	for (int cl = 0; cl < class_n; cl++)
	{
		// jako paramerty brane sa granice horyzontalne klasy cl (D2) oraz pozostalych danych (D1)
		Borders *D2 = &((DiDuBorders*)horizBorders.GetAt(cl))->bordersDi;
		Borders *D1 = &((DiDuBorders*)horizBorders.GetAt(cl))->bordersDu;

		// EPBORDERS : JEPSpace.GetAt(cl)

		// znajdujemy JEP z D1 do D2 (te ktore maja wsparce 0 w D1, i != 0 w D2)
		// D1 = <{0}, {C0, ..., Cm}>
		// D2 = <{0}, {D0, ..., Dn}>
		for (int j = 0; j < D2->rBorder.GetSize(); j++)
		{
			Itemset* Dj = (Itemset*)(D2->rBorder.GetAt(j));

			// jesli ktorys z Ci jest supersetm Dj to => continue
			bool CiSuperOfDj = true;
			for (int cx = 0; cx < D1->rBorder.GetSize(); cx++)
			{
				Itemset* Ci = (Itemset*)D1->rBorder.GetAt(cx);
				
				if (Ci->superOrEqTo(*Dj))
				{
					CiSuperOfDj = true;
				} else 
					CiSuperOfDj = false;

				if (CiSuperOfDj)
					break;
			}
			if (prog) prog->StepIt();
			if (CiSuperOfDj) {
				if (prog) prog->StepIt();
				if (prog) prog->StepIt();
				if (prog) prog->StepIt();
				continue;
			}

			// {C'1, ..., C'm} <- {C1 x Dj, ..., Cm x Dj}
			Dataset* C_ = new Dataset();		
			for (int cp = 0; cp < D1->rBorder.GetSize(); cp++)
			{
				Itemset* Ci = (Itemset*)D1->rBorder.GetAt(cp);
				Itemset* Cp = new Itemset(*Ci);
				*Cp &= *Dj;
				C_->Add(Cp);
			}
			if (prog) prog->StepIt();

			// RIGHTBOUND <- all maximal itemsets in {C'1, ..., C'm}
			Dataset * maximals = getMaxItemsets(C_);
			if (prog) prog->StepIt();

			Dataset * rightBound = new Dataset();
			// tworzone sa kopie elementow maksymalnych, po czym rezultaty poprzedniej operacji sa usuwane
			for (int imax = 0; imax < maximals->GetSize(); imax++)
				rightBound->Add(new Itemset( *((Itemset*)(maximals->GetAt(imax))) ));
			C_->deleteAll();
			delete C_;
			delete maximals;

			// add BORDER-DIFF (<{0}, Dj>, <{0}, RIGHTBOUND> into EPBORDERS

			((ConvexSpace*)(JEPSpace.GetAt(cl)))->Add(BorderDiff(Dj, rightBound));
			rightBound->deleteAll();
			delete rightBound;
			
			if (prog) prog->StepIt();

		}
	}

	calculateJEPsupport();
	b_jeps = true;
	return 0;
}

//////////////////////////////////////////////////////////////////////
// implemenctacja BORDER-DIFF
// obliczanie roznicy granic
// wynikiem jest nowo zaalokowany obiekt granicy - dolne i gorne ogr.
//////////////////////////////////////////////////////////////////////
Borders* JEP::BorderDiff(Itemset *U, Dataset *S)
{
	// dane sa B1 = <{0}, {U}> oraz B2 = <{0}, {S1, S2, ..., Sk}>
	// rezultat : B1 - B2

	Borders * b = new Borders();
	// granica L : b->lBorder
	b->rBorder.Add(new Itemset(*U));

	// initalize L to {{x} | x E U - S1};
	Itemset* S1 = ((Itemset*)(S->GetAt(0)));

	// nasz U - S1
	Itemset* U_S1 = new Itemset(*U);
	*U_S1 -= *S1;
	Dataset *items = allSubItemsOf(U_S1);
	delete U_S1;
	b->lBorder.Append(*items);
	delete items;

	for (int j = 1; j < S->GetSize(); j++)
	{
		Itemset* Sj = ((Itemset*)(S->GetAt(j)));
		Itemset * U_Sj = new Itemset(*U);
		*U_Sj -= *Sj;
		items = allSubItemsOf(U_Sj);
		delete U_Sj;
		Dataset *cartes = cartesian(&b->lBorder, items);
		items->deleteAll();
		delete items;
		b->lBorder.deleteAll();
		Dataset *minis = getMinItemsets(cartes);
		// nie mozna uzyc append do wpisania elementow minimalnych do lBorder, poniewaz nalezy skopiowac te
		// elementy, a nie tylko ich referencje
		for (int a = 0; a < minis->GetSize(); a++)
			b->lBorder.Add( new Itemset( *((Itemset*)minis->GetAt(a)) ) );
		cartes->deleteAll();
		delete cartes;
		delete minis;
	}

	return b;
}

//////////////////////////////////////////////////////////////////////
// zwraca wszystkie elementy jednostkowe danego przykladu
// alokouje obiekt Dataset
//////////////////////////////////////////////////////////////////////
Dataset* JEP::allSubItemsOf(Itemset* U)
{
	Dataset* subitems = new Dataset();
	for (int i = 0; i < U->itemset_len; i++)
		if (U->getItem(i))
		{
			Itemset * newone = new Itemset(U->itemset_len);
			newone->clear();
			newone->setItem(i, 1);
			//int temp = subitems->GetSize();
			subitems->Add(newone);
			//ASSERT(subitems->GetSize() == temp + 1);
		}
	return subitems;
}

Dataset* JEP::cartesian(Dataset *D1, Dataset *D2)
{
	Dataset* result = new Dataset();

	for (int i = 0; i < D1->GetSize(); i++)
		for (int j = 0; j < D2->GetSize(); j++)
		{
			Itemset * I1 = (Itemset*)D1->GetAt(i);
			Itemset * I2 = (Itemset*)D2->GetAt(j);
			Itemset * I1I2 = new Itemset(*I1);
			*I1I2 += *I2;
			result->Add(I1I2);
		}
	return result;
}

//////////////////////////////////////////////////////////////////////
// zwraca elementy minimalne w danym zbiorze
// alokuje nowey obiekt dataset
//////////////////////////////////////////////////////////////////////
Dataset* JEP::getMinItemsets(Dataset* d)
{
	// wyszukuje minimalne elementy w danych
	int n = d->GetSize();

	// w tej tablicy beda oznaczane elementy - 1 jest minimalny, 0 - nie jest
	char * ismin = new char [n];
	// inicjalnie wszystkie przyjmuje jako max
	memset(ismin, 1, sizeof(char) * n);

	Itemset* i_is, *j_is;
	// eliminujemy elementy nie minimalne, niestety n2 :(
	for (int i = 0; i < n; i++)
		if (ismin[i])
			for (int j = 0; j < n; j++)
				if (j != i) if (ismin[j])
				{
					i_is = (Itemset*)d->GetAt(i);
					j_is = (Itemset*)d->GetAt(j);
					if ( i_is->subOrEqTo(*j_is) )
						// element i jest sub do j lub sa rowne - j odpada
						ismin[j] = 0;
				}

	// dodaje do utworzonej listy zaznaczone elementy
	// NIE tworzy duplikatow elemetow
	Dataset* itlist = new Dataset();
	for (int k = 0; k < n; k++)
		if (ismin[k])
			itlist->Add(d->GetAt(k));

	delete ismin;
	return itlist;	
}


//////////////////////////////////////////////////////////////////////
// potrzebna do eliminacji powtarzajacych sie MEJEP
// zwraca nowy obiekt dataset wypelniony jedynie unikalnymi przykladami
//////////////////////////////////////////////////////////////////////
Dataset* JEP::getDiffItemsets(Dataset* d)
{
	// wyszukuje minimalne elementy w danych
	int n = d->GetSize();

	// w tej tablicy beda oznaczane elementy - 1 jest unikalny, 0 - nie jest
	char * iseq = new char [n];
	// inicjalnie wszystkie przyjmuje jako unikalne
	memset(iseq, 1, sizeof(char) * n);

	Itemset* i_is, *j_is;
	// eliminujemy elementy nie unikalne, niestety n2 :(
	for (int i = 0; i < n; i++)
		if (iseq[i])
			for (int j = 0; j < n; j++)
				if (j != i) if (iseq[j])
				{
					i_is = (Itemset*)d->GetAt(i);
					j_is = (Itemset*)d->GetAt(j);
					if (*i_is == *j_is)
						// element i jest sub do j lub sa rowne - j odpada
						iseq[j] = 0;
				}

	// dodaje do utworzonej listy zaznaczone elementy
	// NIE tworzy duplikatow elemetow
	Dataset* itlist = new Dataset();
	for (int k = 0; k < n; k++)
		if (iseq[k])
			itlist->Add(d->GetAt(k));

	delete iseq;
	return itlist;	
}


//////////////////////////////////////////////////////////////////////
// przejrzenie zioru uczacego i obliczenie wsparc dla elementow 
// w JEPSpace
//////////////////////////////////////////////////////////////////////
void JEP::calculateJEPsupport()
{
	int train_n;

	for (int cl = 0; cl < JEPSpace.GetSize(); cl++)
	{
		ConvexSpace* cs = (ConvexSpace*) JEPSpace.GetAt(cl);
		for (int j = 0; j < cs->GetSize(); j++)
		{
			Borders *b = (Borders*) cs->GetAt(j);
			for (int l = 0; l < b->lBorder.GetSize(); l++)
			{
				Itemset* check = (Itemset*)b->lBorder.GetAt(l);
				check->count = 0;
				check->cl = cl + 1;
				train_n = 0;
				for (int tl = 0; tl < train_d->GetSize(); tl++)
					if ( ((Itemset*) (train_d->GetAt(tl)))->cl == cl + 1)
					{
						if (check->subOrEqTo( *((Itemset*)(train_d->GetAt(tl))) ))
							check->count++;
						train_n++;
					}
				ASSERT(train_n != 0);
				check->supp = (float)check->count / (float)train_n;
			}

			for (int r = 0; r < b->rBorder.GetSize(); r++)
			{
				Itemset* check = (Itemset*)b->rBorder.GetAt(r);
				check->count = 0;
				check->cl = cl + 1;
				train_n = 0;
				for (int tr = 0; tr < train_d->GetSize(); tr++)
					if ( ((Itemset*) (train_d->GetAt(tr)))->cl == cl + 1)
					{
						if (check->subOrEqTo( *((Itemset*)(train_d->GetAt(tr))) ))
							check->count++;
						train_n++;
					}
				ASSERT(train_n != 0);
				check->supp = (float)check->count / (float)train_n;
			}
		}
	}
}


//////////////////////////////////////////////////////////////////////
// wybiera klase na podstawie wektora glosow
// zwraca: 
// -1 blad
// 0 nie zakwalifikowany
// >0 wybrana klasa
//////////////////////////////////////////////////////////////////////
int JEP::classifyVotes(float* votes)
{	
	if (!b_jeps) return 0;
	float max_vote = 0;
	int class_selected = -1;

	for (int v = 0; v < class_n; v++)
		if (votes[v] > max_vote)
		{
			max_vote = votes[v];
			class_selected = v + 1;
		} else if (votes[v] == max_vote)
			// nie mozna dokonac jednoznacznej klasyfikacji
			class_selected = 0;
	delete votes;
	return class_selected;
}

//////////////////////////////////////////////////////////////////////
// klasyfikuje pojedynczy przyklad
// zwraca jego klase od 1 do class_n
// zwraca:
// -1 blad
// 0 nie zakwalifikowany lub blad danych
// >0 wybrana klasa
//////////////////////////////////////////////////////////////////////
int JEP::classifyItemset(Itemset *item, FILE* writeto)
{
	if (!b_jeps) return 0;
	float *votes = voteForItemset(item, NULL, writeto);
	return classifyVotes(votes);
}

void JEP::classifyDataset(Dataset *test_data, FILE *writeto) {
	for (int t = 0; t < test_data->GetSize(); t++)
	{
		Itemset* tested = (Itemset*)test_data->GetAt(t);
		int cl = classifyItemset(tested, NULL);
		if (cl < 0) 
			cl = 0;
		tested->cl = cl;
	}
}

//////////////////////////////////////////////////////////////////////
// zwraca tabele obiektow dataset. kazdy z nich zawiera MEJEPy ktore
// sa zawarte w przykladzie item
// jesli itemset == NULL, to wyszukuje szystkie MEJEP
//////////////////////////////////////////////////////////////////////
Dataset** JEP::getMEJEPs(Itemset* item) 
{
	// wybieranie MEJEP

	Dataset ** uniq = new Dataset*[class_n];
	for (int cl = 0; cl < class_n; cl++)
	{
		uniq[cl] = new Dataset();
		ConvexSpace* cs = (ConvexSpace*) JEPSpace.GetAt(cl);
		for (int j = 0; j < cs->GetSize(); j++)
		{
			Borders *b = (Borders*) cs->GetAt(j);
			for (int l = 0; l < b->lBorder.GetSize(); l++)
			{
				Itemset* voter = (Itemset*)b->lBorder.GetAt(l);
				if (item != NULL) {
					if (voter->subOrEqTo(*item))
						uniq[cl]->Add(voter);
				} else uniq[cl]->Add(voter);
			}
		}
	}

	// poniewaz w przestrzeniach JEP moga sie pojawic te same klasyfikatory, nalezy
	// je przefiltrowac i wybrac te rozne
	Dataset ** temp = new Dataset*[class_n];
	for (cl = 0; cl < class_n; cl++)
	{
		temp[cl] = getDiffItemsets(uniq[cl]);
		uniq[cl]->RemoveAll();
		delete uniq[cl];
		uniq[cl] = temp[cl];
	}
	delete temp;
	return uniq;
}

//////////////////////////////////////////////////////////////////////
// zwraca dataset zawierjacy MEJEPy ktore
// sa zawarte w przykladzie item
// jesli itemset == NULL, to wyszukuje szystkie MEJEP
//////////////////////////////////////////////////////////////////////
Dataset* JEP::getMEJEPs2(Itemset* item) 
{
	// wybieranie MEJEP

	Dataset ** uniq = new Dataset*[class_n];
	for (int cl = 0; cl < class_n; cl++)
	{
		uniq[cl] = new Dataset();
		ConvexSpace* cs = (ConvexSpace*) JEPSpace.GetAt(cl);
		for (int j = 0; j < cs->GetSize(); j++)
		{
			Borders *b = (Borders*) cs->GetAt(j);
			for (int l = 0; l < b->lBorder.GetSize(); l++)
			{
				Itemset* voter = (Itemset*)b->lBorder.GetAt(l);
				if (item != NULL) {
					if (voter->subOrEqTo(*item))
						uniq[cl]->Add(voter);
				} else uniq[cl]->Add(voter);
			}
		}
	}

	// poniewaz w przestrzeniach JEP moga sie pojawic te same klasyfikatory, nalezy
	// je przefiltrowac i wybrac te rozne
	Dataset ** temp = new Dataset*[class_n];
	for (cl = 0; cl < class_n; cl++)
	{
		temp[cl] = getDiffItemsets(uniq[cl]);
		uniq[cl]->RemoveAll();
		delete uniq[cl];
		uniq[cl] = temp[cl];
	}
	delete temp;

	Dataset * temp2 = new Dataset();
	for (cl = 0; cl < class_n; cl++)
	{
		temp2->Append(*uniq[cl]);
		uniq[cl]->RemoveAll();
		delete uniq[cl];
	}
	delete uniq;

	return temp2;
}

//////////////////////////////////////////////////////////////////////
// oblicza sumy glosow dla poszczegolnych klas dla danego przykladu
// zwraca wektor sum glosow
//////////////////////////////////////////////////////////////////////
float* JEP::voteForItemset(Itemset *item, int** nv, FILE* writeto)
{
	if (!b_jeps) return NULL;
	char buf[1024];

	// zbiera glosy wszystkich JEP dla kazdej z klas
	float * votes = new float [class_n];
	int * nvotes = new int [class_n];

	Dataset ** uniq = getMEJEPs(item);

	for (int cl = 0; cl < class_n; cl++)
	{
		votes[cl] = 0;
		nvotes[cl] = 0;
		for (int j = 0; j < uniq[cl]->GetSize(); j++)
		{
			Itemset* voter = (Itemset*)uniq[cl]->GetAt(j);
			votes[cl] += voter->supp;
			nvotes[cl] ++;
			if (writeto != NULL)
			{
				voter->binarize(buf);
				if (writeto != NULL) 
					fprintf(writeto, "\tCount %d Support %.2f%%\t%s\n", voter->count, voter->supp*(float)100, buf);
			}
		}
	}

	for (cl = 0; cl < class_n; cl++)
	{
		uniq[cl]->RemoveAll();
		delete uniq[cl];
	}
	delete uniq;

	if (nv != NULL) { 
		*nv = nvotes;
	} else delete nvotes;

	return votes;
}

void JEP::getNthFold(Dataset* d, int folds, int foldNum, Dataset** train, Dataset** test)
{
	int chunk = d->GetSize()/folds;
	for (int p1 = 0; p1 < d->GetSize(); p1++)
	{
		if ((p1 >= chunk*foldNum) && (p1 < chunk*(foldNum+1)))
			(*test)->Add(d->GetAt(p1));
		else 
			(*train)->Add(d->GetAt(p1));
	}
}


void JEP::calcuteNcrossFordValidationAccurancy(int folds, float *mistakes, float *unclassified, ProgressStep * prog)
{
	int tested_n = 0;

	Dataset * oldTrain = new Dataset();
	oldTrain->Append(*train_d);
	train_d->RemoveAll();

	Dataset * test_data = new Dataset();
	int maxrange = train_d->GetSize()*10;

	int m_errs = 0;
	int m_unc = 0;
	float m_errs_f = 0;
	float m_unc_f = 0;

	for (int p = 0; p < folds; p++)
	{
		getNthFold(oldTrain, folds, p, &train_d, &test_data);

		// wybrano juz dane:
		// 10% danych jako testowe
		// 90% jako uczace

		generateHorizBorders(NULL);
		findJEPs(NULL);

		for (int t = 0; t < test_data->GetSize(); t++)
		{
			tested_n++;
			Itemset* tested = (Itemset*)test_data->GetAt(t);
			int cl1 = tested->cl;
			int cl2 = classifyItemset(tested, NULL);
			if (cl2 == 0)
			{
				m_unc++;
				m_unc_f = (float) m_unc / (float) tested_n * (float)100;
			} else
			if (cl1 != cl2) {
				m_errs++;
				m_errs_f = (float) m_errs / (float) tested_n * (float)100;
			}
			if (prog) prog->StepIt();
		}
		test_data->RemoveAll();
		train_d->RemoveAll();
	} // for p

	train_d->Append(*oldTrain);
	delete oldTrain;

	*unclassified = m_unc_f;
	*mistakes = m_errs_f;
}

void JEP::calculateAccurancyOnTestData(Dataset* test, float *mistakes, float *unclassified, ProgressStep * prog)
{
	int m_errs = 0;
	int m_unc = 0;
	float m_errs_f = 0;
	float m_unc_f = 0;
	for (int t = 0; t < test->GetSize(); t++)
	{
		Itemset* tested = (Itemset*)test->GetAt(t);
		int cl1 = tested->cl;
		int cl2 = classifyItemset(tested, NULL);
		if (cl2 == 0)
		{
			m_unc++;
			m_unc_f = (float) m_unc / (float) t * (float)100;
		} else
		if (cl1 != cl2) 
		{
			m_errs++;
			m_errs_f = (float) m_errs / (float) t * (float)100;
		}
		if (prog) prog->StepIt();
	}

	*mistakes = m_errs_f;
	*unclassified = m_unc_f;	
}
