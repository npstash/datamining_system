// cDeEP.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cDeEP.h"
#include "Dataset.h"
#include "JEP.h"
#include "Itemset.h"
#include "ConvexSpace.h"
#include "Borders.h"
#include "DiDuBorders.h"
#include "IO.h"

#include <direct.h>
#include <stdlib.h>

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
		mainProc(argc, argv);
	}

	return nRetCode;
}



int mainProc(int argc, char* argv[]) {
	// wczytaj dane z argv[0]
	// 1 - wejscie bin

	if (argc < 4) { 
		printf("Argumenty: deep.exe <dane_uczace.dataset> <dane_testowe.dataset> <dane_wynikowe.dataset> \n");
		printf("<dane_uczace.dataset> - dane do uzycia w generowaniu wzorcow JEP\n");
		printf("<dane_testowe.dataset> - dane do zaklasyfikowania\n");
		printf("<dane_wynikowe.dataset> - wyniki klasyfikacji\n");
		return 1;
	}

	int classes, itemset_len;

	// wczytanie danych
	Dataset * datTrain = IO::loadDataset(argv[1], &classes, &itemset_len);
	if (!datTrain)
		return 2;
	printf("Dane uczace wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datTrain->GetSize(), itemset_len);

	Dataset * datClasf = IO::loadDataset(argv[2], &classes, &itemset_len);
	if (!datClasf)
		return 3;
	printf("Dane testowe wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datClasf->GetSize(), itemset_len);
	
	
	// obliczyc dla kazdego przykladu z 'test' czesc wspolna z elementami
	// 'train'
	for (int ts = 0; ts < datClasf->GetSize(); ts++) {
		Dataset * usedTrain = new Dataset();
		usedTrain->deepCopyItemsetFrom(datTrain);

		// wymnozenie
		Itemset * tested = (Itemset*) datClasf->GetAt(ts);

		for (int tr = 0; tr < usedTrain->GetSize(); tr++) {
			Itemset * it = (Itemset*) usedTrain->GetAt(tr);
			*it &= *tested;			
		}
		// usunac wszystkie puste? po co? :) horiz border je wyeliminuje:)

		// mamy czesc wspolna
		// obliczyc granice i JEPy - normalny algorytm JEP dla tego przyk³adu
		JEP * j = new JEP(classes, usedTrain);
		j->generateHorizBorders(NULL);

		int bsum = 0;
		for (int b = 0; b < j->class_n; b++) {
			DiDuBorders * ddbr = (DiDuBorders*) j->horizBorders.GetAt(b);
			bsum += ddbr->bordersDi.rBorder.GetSize();
		}
		printf("Wielkosc uzyskanej bazy trenujacej dla przykladu %d\n", bsum, ts);

		j->findJEPs(NULL);
		Dataset** MEJEPs = j->getMEJEPs(NULL);

		int msum = 0;
		for (int m = 0; m < j->class_n; m++) 
			msum+= MEJEPs[m]->GetSize();
			
		printf("W sumie uzyskano %d wzorcow MEJEP dla przykladu %d\n", msum, ts);

		int res = j->classifyItemset(tested, NULL);

		tested->cl = res;

		usedTrain->deleteAll();
		delete usedTrain;
		delete j;
	}

	IO::writeDataset(argv[3], datClasf, classes, itemset_len);

	if (datTrain) {
		datTrain->deleteAll();
		delete datTrain;
	}

	if (datClasf) {
		datClasf->deleteAll();
		delete datClasf;
	}

	return 0;
}
