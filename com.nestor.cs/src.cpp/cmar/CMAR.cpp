// CMAR.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "CMAR.h"
#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#include "..\FPTree\BinaryPointersDataset.h"

#include "CMARFPTree.h"
//#include "CMARFPTreeLeaf.h"
//#include "CRTree.h"

#include "..\\JEP-ALG\\IO.h"
#include "..\\JEP-ALG\\Dataset.h"
#include "..\\JEP-ALG\\Itemset.h"

#include ".\cmar.h"

// The one and only application object


using namespace std;

bool debug = false;
bool crossdebug = true;
bool reducingInsertion = true;
bool covPruning = true;
int absMinSupp = 2;

#define CMAR_NOMAIN


#ifndef CMAR_NOMAIN
CWinApp theApp;

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		_tprintf(_T("Fatal Error: MFC initialization failed\n"));
		nRetCode = 1;
	}
	else
	{
		if (argc < 3) {
			printf("Podaj nazwe pliku .dataset jako parametr oraz plik ustawien .cmar.sets\n");
			return 1;
		}
		FILE * plik = fopen(argv[2], "r");
		float f1, f2, f3, f4, f5;
		int i1, i2, i3;
		char tmp[0x100];
		fscanf(plik, "%f%s", &f1, tmp);
		fscanf(plik, "%f%s", &f2, tmp);
		fscanf(plik, "%d%s", &i1, tmp);
		fscanf(plik, "%f%s", &f3, tmp);
		fscanf(plik, "%f%s", &f4, tmp);
		fscanf(plik, "%f%s", &f5, tmp);
		fscanf(plik, "%d%s", &i2, tmp);
		fscanf(plik, "%d%s", &i3, tmp);
		fscanf(plik, "%d%s", &absMinSupp, tmp);
		fclose(plik);

		if (i2 == 1) reducingInsertion = true;
		else reducingInsertion = false;

		if (i3 == 1) covPruning = true;
		else covPruning = false;

		//char * fname = "..\\DATASETS\\primary-tumor.dataset";
		//char * fname = "..\\DATASETS\\test4.dataset";
		char * fname = argv[1];
		int classes;
		int binItems;
		Dataset * datBin = IO::loadDataset(fname, &classes, &binItems);
		classes++;
		BinaryPointersDataset * datPnt = new BinaryPointersDataset(binItems);
		for (int i = 0; i < datBin->GetCount(); i++) {
			Itemset * binItemset = (Itemset*) datBin->GetAt(i);
			datPnt->Add(new BinaryPointersItemset(*binItemset));
		}

		datBin->deleteAll();
		delete datBin;

		//CMAR * cmar = new CMAR(classes, 1, 51, 3, 2.84, 0.01, 0.04);
		CMAR * cmar = new CMAR(classes, f1, f2, i1, f3, f4, f5);
		printf("Rozmiar bazy transakcyjnej: %d\n", datPnt->GetCount());
		printf("Ilosc atrybutow: %d\n", datPnt->binaryAttributes);
		printf("Ilosc klas: %d\n", classes);
		printf("\n");

		int mc, uc;
		double ac;
		cmar->processCrossTest(10, &mc, &uc, &ac, datPnt);

		printf("W sumie:\n");
		printf("Bledy: %d\n", mc);
		printf("Niezaklasyfikowane: %d\n", uc);
		printf("Wynikowa celnosc: %.2f%%\n", ac);


/*		cmar->learn(datPnt);
		for (int i = 0; i < datPnt->GetCount(); i++) {
			BinaryPointersItemset* it = (BinaryPointersItemset*) datPnt->GetAt(i);

			int wynik = cmar->classifyItemset(it);

			printf("Klasa %d, zaklasyfikowano jako %d\n", it->cl, wynik);

		}
*/
		datPnt->deleteAll();
		delete datPnt;

//		cin >> classes;
	}

	return nRetCode;
}
#endif


CMAR::CMAR(int classCount, float supportTH, float confidenceTH, int coverageTH, float chiSquareTH,
		float supportDifferenceTH, float confidenceDifferenceTH, float setConfDiff) :
rules(0)
, m_supportTH(supportTH)
, m_confidenceTH(confidenceTH)
, m_coverageTH(coverageTH)
, m_chiSquareTH(chiSquareTH)
, m_supportDifferenceTH(supportDifferenceTH)
, m_confidenceDifferenceTH(confidenceDifferenceTH)
, m_setConfidenceDifferenceTH(setConfDiff)
, FPList(0)
, classLabels(classCount)
{
	printf("Support TH is %.2f\n", m_supportTH);
	printf("Confidence TH is %.2f\n", m_confidenceTH);
	printf("Coverage TH is %d\n", m_coverageTH);
	printf("Chi2 TH is %.2f\n", m_chiSquareTH);
	printf("Confidence diff. TH is %.2f\n", m_setConfidenceDifferenceTH);
	printf("\n");
}

CMAR::~CMAR(void)
{
	if (FPList) {
		FPList = 0;
	}
	if (rules) {
		rules = 0;
	}
}

void CMAR::learn(BinaryPointersDataset* dataset) {
	if (FPList) {
		delete FPList;
		FPList = 0;
	}
	if (rules) {
		delete rules;
		rules = 0;
	}
	rules = generateRules(dataset, &FPList);
}

CRTree* CMAR::generateRules(BinaryPointersDataset* dataset, BinaryPointersItemset ** pFPList)
{
	int suppTH = int((float)dataset->GetCount() * m_supportTH / 100);
	suppTH = (suppTH < absMinSupp) ? absMinSupp : suppTH;

	//if (crossdebug) printf("Prog wsparcia wynosi: suppTH is %d\n", suppTH);
	CMARFPTree * FPs = new CMARFPTree(dataset, suppTH, classLabels);

	CRTree *unpruned = new CRTree(FPs->classLablelsSupports, FPs->classCount, FPs->datasetSize, FPs->binAttrCnt);
	unpruned->currentChi2TH = m_chiSquareTH;
	unpruned->suppDiffTH = m_supportDifferenceTH;
	unpruned->confDiffTH = m_confidenceDifferenceTH;
	FPs->FillCRTree(m_confidenceTH, unpruned);

	CRTree* res;
	if (covPruning) {
		res = FPs->pruneBasedOnCoverage(unpruned, dataset, m_coverageTH);
		res->currentChi2TH = m_chiSquareTH;
		res->suppDiffTH = m_supportDifferenceTH;
		res->confDiffTH = m_confidenceDifferenceTH;
		delete unpruned;
	} else {
		res = unpruned;
	}

	res->sortHeaderTable(FPs->FPList);

	*pFPList = new BinaryPointersItemset(*FPs->FPList);
	delete FPs;

	return res;
}

int CMAR::classifyItemset(BinaryPointersItemset* itemsetToClassify)
{
	BinaryPointersItemset* itemset = new BinaryPointersItemset(*itemsetToClassify);
	itemset->sortAndPruneAs(FPList);

//	printf(".classify: sorted itemset:"); itemset->debugPrint();

	BinaryPointersDataset ** groups = rules->getRuleGroupsForItemset(itemset, m_coverageTH, m_setConfidenceDifferenceTH);

	// obliczanie chi2 wazonego dla wszystkich grup
	double * powers = new double [classLabels];
	for (int i = 0; i < classLabels; i++) {
		powers[i] = 0;
	}

	for (int gr = 0; gr < classLabels; gr++) {
//		printf(".classify: rules in group %d: %d\n", gr, groups[gr]->GetCount());

		for (int rl = 0; rl < groups[gr]->GetCount(); rl++) {
			BinaryPointersItemset * theRule = (BinaryPointersItemset*) groups[gr]->GetAt(rl);

//			printf("Rule: class:%d conf:%d supp:%d len:%d\n", theRule->cl, theRule->conf, theRule->supp, theRule->getCount());

			float chi2 = CRTree::computeChi2(theRule->supp, theRule->conf, rules->datasetSize, rules->classLablelsSupports[gr]);
			float maxchi2 = CRTree::computeMaxChi2(theRule->supp, rules->classLablelsSupports[gr], rules->datasetSize);
			float power = chi2*chi2/maxchi2;

			powers[gr] += power;
		}
	}

	// wartosci maxChi2 obliczone. wybieramy klase dla ktorej ta suma jest najwieksza
	// w przypadku niejednoznacznosci zwracamy wartosc -1

	int maxPower = 0;
	int selectedClass = -1;
	int classesAtThisPow = 0;

	for (int gr = 0; gr < classLabels; gr++) {
		if (powers[gr] == maxPower) {
			classesAtThisPow++;
		}

		if (powers[gr] > maxPower) {
			maxPower = powers[gr];
			selectedClass = gr;
			classesAtThisPow = 1;
		}
	}

	if (classesAtThisPow > 1) {
		selectedClass = -1;
	}

	for (int i = 0; i < classLabels; i++) {
		groups[i]->deleteAll();
		delete groups[i];
	}
	delete powers;
	delete groups;
	delete itemset;

	return selectedClass;
}

void CMAR::getNthFold(BinaryPointersDataset* d, int folds, int foldNum, 
					  BinaryPointersDataset* train, BinaryPointersDataset* test)
{
	int chunk = d->GetSize()/folds;
	for (int p1 = 0; p1 < d->GetSize(); p1++)
	{
		if ((p1 >= chunk*foldNum) && (p1 < chunk*(foldNum+1)))
			test->Add(d->GetAt(p1));
		else 
			train->Add(d->GetAt(p1));
	}
}

void CMAR::processCrossTest(int folds, int * misclassified, int *unclassified,
							double * accurancy, BinaryPointersDataset* dataset)
{
	int tested_n = 0;

	BinaryPointersDataset * oldTrain = new BinaryPointersDataset(dataset->binaryAttributes);
	oldTrain->Append(*dataset);

	BinaryPointersDataset * train_d = new BinaryPointersDataset(dataset->binaryAttributes);
	BinaryPointersDataset * test_data = new BinaryPointersDataset(dataset->binaryAttributes);
	
	int m_errs = 0;
	int m_unc = 0;
	float m_errs_f = 0;
	float m_unc_f = 0;

	for (int p = 0; p < folds; p++)
	{
		getNthFold(oldTrain, folds, p, train_d, test_data);

		if (crossdebug) printf("Generowanie regul dla %d. przedzialu\n", p);

		// wybrano juz dane:
		// 10% danych jako testowe
		// 90% jako uczace

		learn(train_d);
		int uc = 0, mc = 0;

		for (int t = 0; t < test_data->GetSize(); t++)
		{
			tested_n++;
			BinaryPointersItemset* tested = (BinaryPointersItemset*)test_data->GetAt(t);
			int cl1 = tested->cl;
			int cl2 = classifyItemset(tested);
			if (cl2 == -1)
			{
				m_unc++;
				uc++;
				m_unc_f = (float) m_unc / (float) tested_n * (float)100;
			} else
			if (cl1 != cl2) {
				m_errs++;
				mc++;
				m_errs_f = (float) m_errs / (float) tested_n * (float)100;
			}
		}
//		if (crossdebug) printf("Bledny: %d\n", mc);
//		if (crossdebug) printf("Niezaklasyfikowane: %d\n", uc);

		delete rules;
		rules = 0;
		delete FPList;
		FPList = 0;

		test_data->RemoveAll();
		train_d->RemoveAll();
	} // for p

	delete oldTrain;
	delete train_d;
	delete test_data;

	*unclassified = m_unc;
	*misclassified = m_errs;
	*accurancy = 100 - m_unc_f - m_errs_f;
}
