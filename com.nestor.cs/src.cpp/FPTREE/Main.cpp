// FPTREE.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "FPTREE.h"
#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#include "BinaryPointersDataset.h"

#include "..\\JEP-ALG\\IO.h"
#include "..\\JEP-ALG\\Dataset.h"
#include "..\\JEP-ALG\\Itemset.h"


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
		if (argc < 3) {
			printf("Podaj nazwe pliku .dataset jako parametr oraz %prog wsparcia\n");
			return 1;
		}
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

		int suppTH = datPnt->GetCount() * atof(argv[2]) / 100;
		if (suppTH < 2) suppTH = 2;

		printf("Zbior danych: %s\n", argv[1]); 
		printf("Dlugosc rekordow: %d\n", datPnt->binaryAttributes); 
		printf("Ilosc rekordow: %d\n", datPnt->GetCount()); 
		printf("Prog wsparcia wynosi %d (%.2f%%)\n", suppTH, atof(argv[2]));


		long czas1 = time(0);

		FPTree * tree = new FPTree(datPnt, suppTH);
		BinaryPointersDataset* res = tree->FPGrowth();

		long czas2 = time(0);
		long czas = czas2 - czas1;

		printf("Uzyskano %d zbiorow w czasie %ds\n", res->GetCount(), czas);

		delete res;
		delete tree;

		datBin->deleteAll();
		delete datBin;

		datPnt->deleteAll();
		delete datPnt;
	}

	return nRetCode;
}
