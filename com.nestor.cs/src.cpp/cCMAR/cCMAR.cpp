// cCMAR.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cCMAR.h"

#include "Dataset.h"
#include "IO.h"
#include "BinaryPointersDataset.h"
#include "CMAR.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


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
		_tprintf(_T("Fatal Error: MFC initialization failed\n"));
		nRetCode = 1;
	}
	else
	{
		mainProc(argc, argv);
	}

	return nRetCode;
}

/*
bool debug = false;
bool crossdebug = true;
bool reducingInsertion = true;
bool covPruning = true;
int absMinSupp = 2;
*/

int mainProc(int argc, char* argv[]) {
	// wczytaj dane z argv[0]
	// 1 - wejscie bin

	if (argc < 5) { 
		printf("Argumenty: ccmar.exe <dane_uczace.dataset> <dane_testowe.dataset> <dane_wynikowe.dataset> <plik_ustawien>\n");
		printf("<dane_uczace.dataset> - dane do uzycia w generowaniu wzorcow JEP\n");
		printf("<dane_testowe.dataset> - dane do zaklasyfikowania\n");
		printf("<dane_wynikowe.dataset> - wyniki klasyfikacji\n");
		printf("<plik_ustawien> - ustawienia\n");
		return 1;
	}

	int classes, itemset_len;

	// wczytanie danych ucz¹cych
	Dataset * datTrainBin = IO::loadDataset(argv[1], &classes, &itemset_len);
	if (!datTrainBin)
		return 2;
	printf("Dane uczace wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datTrainBin->GetSize(), itemset_len);
	BinaryPointersDataset * datTrainPnt = new BinaryPointersDataset(itemset_len);
	for (int i = 0; i < datTrainBin->GetCount(); i++) {
		Itemset * binItemset = (Itemset*) datTrainBin->GetAt(i);
		datTrainPnt->Add(new BinaryPointersItemset(*binItemset));
	}
	datTrainBin->deleteAll();
	delete datTrainBin;

	// wczytanie danych testowych
	Dataset * datClasfBin = IO::loadDataset(argv[2], &classes, &itemset_len);
	if (!datClasfBin)
		return 3;
	printf("Dane testowe wczytany: %d klas  %d przykladow  %d binarnych atrybutow\n", classes, datClasfBin->GetSize(), itemset_len);
	BinaryPointersDataset * datClasfPnt = new BinaryPointersDataset(itemset_len);
	for (int i = 0; i < datClasfBin->GetCount(); i++) {
		Itemset * binItemset = (Itemset*) datClasfBin->GetAt(i);
		datClasfPnt->Add(new BinaryPointersItemset(*binItemset));
	}
	datClasfBin->deleteAll();
	delete datClasfBin;

	// wczytanie ustawieñ
	FILE * plik = fopen(argv[4], "r");
	float f1, f2, f3, f4, f5, f6;
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
	fscanf(plik, "%f%s", &f6, tmp);
	fclose(plik);

	if (i2 == 1) reducingInsertion = true;
	else reducingInsertion = false;
	
	if (i3 == 1) covPruning = true;
	else covPruning = false;

	classes++;
	printf("Inicjalizacja klasyfikatora\n");
	CMAR * cmar = new CMAR(classes, f1, f2, i1, f3, f4, f5, f6);

	printf("Generowanie FP-drzewa i CR-drzewa...\n");
	cmar->learn(datTrainPnt);
	for (int i = 0; i < datClasfPnt->GetCount(); i++) {
		BinaryPointersItemset* it = (BinaryPointersItemset*) datClasfPnt->GetAt(i);
		int wynik = cmar->classifyItemset(it);
		printf("Przyklad %d, zaklasyfikowano jako %d\n", i, wynik);
		if (wynik < 0)
			wynik = 0;
		it->cl = wynik;
	}

	Dataset * datClasf = new Dataset();
	for (int i = 0; i < datClasfPnt->GetCount(); i++) {
		BinaryPointersItemset* it = (BinaryPointersItemset*) datClasfPnt->GetAt(i);
		datClasf->Add(it->getItemset(itemset_len));
	}
	IO::writeDataset(argv[3], datClasf, classes - 1, itemset_len);
	datClasf->deleteAll();
	delete datClasf;

	if (datTrainPnt) {
		datTrainPnt->deleteAll();
		delete datTrainPnt;
	}

	if (datClasfPnt) {
		datClasfPnt->deleteAll();
		delete datClasfPnt;
	}

	return 0;
}
