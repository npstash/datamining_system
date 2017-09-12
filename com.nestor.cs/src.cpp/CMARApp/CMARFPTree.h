#pragma once

#include "stdafx.h"

#include "CMARFPTreeLeaf.h"
#include "CMARFPTreeHeaderTableElement.h"
#include "CRTree.h"

#include "..\FPTree\BinaryPointersDataset.h"

class CMARFPTree : public CMARFPTreeLeaf
{
public:
	CMARFPTree(BinaryPointersDataset * dat, int suppTH, int cl, BinaryPointersItemset *con = 0);
	virtual ~CMARFPTree(void);

//////////////////////////////////////////////////////////////////////////////////////////////////////
// dane
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// tablica naglowkow
	CArray <CMARFPTreeHeaderTableElement*> headerTable; 

	// prefiks drzewa, jesli warunkowe
	BinaryPointersItemset * conditionalTo;

	// FPList-a uzyskana przed budowa drzewa
	BinaryPointersItemset * FPList;

	// wymagany prog wsparcia
	int supportTH;

	// maksymalna dlugosc rekordu. ilosc mozliwych transakcji
	int binAttrCnt;

	// rozklad czestosci etykiet klasy
	int* classLablelsSupports;

	// rozmiar bazy (warunkowej)
	int datasetSize;

//////////////////////////////////////////////////////////////////////////////////////////////////////
// metody
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// sortowanie tablicy naglowkow
	void sortHeader();

	// wyciaganie regul asocjacyjnych
	CRTree * FillCRTree(float confTH, CRTree *res = 0, bool *rulesAdded = 0);

	// podglad obiektu
	virtual void debugPrint();

	// obliczanie wsparcia w %
	inline float computeConf(int p_conf, int p_supp);

	// redukcja oparta na pokryciu bazy
	CRTree* pruneBasedOnCoverage(CRTree * tree, BinaryPointersDataset* dat, int coverageTH, int* rc1 = 0, int* rc2 = 0);
};
