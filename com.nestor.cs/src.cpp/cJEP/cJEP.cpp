// cJEP.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cJEP.h"

#include "JEP.h"
#include "Dataset.h"
#include "IO.h"
#include "DiDuBorders.h"
#include "Borders.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// The one and only application object

CWinApp theApp;

using namespace std;

int _tmain(int argc, TCHAR* argv[], TCHAR* envp[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{
		// TODO: change error code to suit your needs
		cerr << _T("Fatal Error: MFC initialization failed") << endl;
		nRetCode = 1;
	}
	else
	{
		return mainProc(argc, argv);
	}

	return nRetCode;
}


int mainProc(int argc, char* argv[]) {
	// wczytaj dane z argv[0]
	// 1 - wejscie bin

	if (argc < 2) { 
		printf("Argumenty: cjep.exe <dane_uczace.dataset> [<plik_MEJEP.dataset> [<dane_testowe.dataset> <dane_wynikowe.dataset>]] \n");
		printf("<dane_uczace.dataset> - dane do uzycia w generowaniu wzorcow JEP\n");
		printf("<plik_MEJEP.dataset> - tutaj bêd¹ zapisane wzorce MEJEP\n");
		printf("<dane_testowe.dataset> - dane do zaklasyfikowania\n");
		printf("<dane_wynikowe.dataset> - wyniki klasyfikacji\n");
		return 1;
	}

	bool saveMEJEPs = false;
	bool doClasf = false;

	if (argc == 3) { 
		saveMEJEPs = true;
	} else 
	if (argc == 5) { 
		saveMEJEPs = true;
		doClasf = true;
	} else 
		return 4;

	int classes, itemset_len;
	JEP * j = NULL;
	Dataset * datTrain = NULL;
	Dataset * datClasf = NULL;
	Dataset * MEJEPs = NULL;

	datTrain = IO::loadDataset(argv[1], &classes, &itemset_len);
	if (!datTrain) 
		return 2;
	printf("Zbior uczacy wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datTrain->GetSize(), itemset_len);

	if (doClasf) {
		datClasf = IO::loadDataset(argv[3], &classes, &itemset_len);
		if (!datClasf) 
			return 3;
		printf("Zbior testowy wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datClasf->GetSize(), itemset_len);
	}

	j = new JEP(classes, datTrain);

	printf("Generowanie granic horyzontalnych\n");
	j->generateHorizBorders(NULL);
	
	for (int cl = 0; cl < j->class_n; cl++) {
		Borders * ddb = &((DiDuBorders*)j->horizBorders.GetAt(cl))->bordersDi;
		printf("Rozmiar gornego ograniczenia horyzonatlnego dla klasy %d: %d\n", cl+1, ddb->rBorder.GetSize());
	}

	j->findJEPs(NULL);

	for (cl = 0; cl < j->class_n; cl++) {
		ConvexSpace * cs = (ConvexSpace*) j->JEPSpace.GetAt(cl);
		
		int sum = 0;
		for (int c = 0; c < cs->GetSize(); c++) {
			Borders * b = (Borders *) cs->GetAt(c);
			sum += b->lBorder.GetSize();
		}
		printf("Wygenerowano w sumie %d JEP w lewych granicach przestrzeni JEP dla klasy %d\n", sum, cl+1);
	}

	MEJEPs = j->getMEJEPs2(NULL);
	printf("W sumie %d MEJEP\n", MEJEPs->GetSize());

	if (saveMEJEPs) {
		printf("Zapisuje MEJEPs do pliku %s\n", argv[2]); 
		IO::writeDataset(argv[2], MEJEPs, j->class_n, itemset_len);
	}

	if (MEJEPs) {
		delete MEJEPs;
	}

	if (datClasf) {
		printf("Wykonywanie klasyfikacji danych testowych");
		j->classifyDataset(datClasf, stdout);
		IO::writeDataset(argv[4], datClasf, j->class_n, itemset_len);
	}

	if (datTrain) {
		datTrain->deleteAll();
		delete datTrain;
	}

	if (datClasf) {
		datClasf->deleteAll();
		delete datClasf;
	}

	if (j) delete j;

	return 0;
}
