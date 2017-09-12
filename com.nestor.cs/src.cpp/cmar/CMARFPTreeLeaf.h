#pragma once

#include "stdafx.h"

#include "..\FPTree\binarypointersdataset.h"
#include "..\FPTree\debugPrinter.h"

#include "CMARFPTreeHeaderTableElement.h"

class CMARFPTreeLeaf : public DebugPrinter
{
public:
	CMARFPTreeLeaf() {};
	CMARFPTreeLeaf(int classes);
	virtual ~CMARFPTreeLeaf(void);

//////////////////////////////////////////////////////////////////////////////////////////////////////
// dane
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// lista wezlow potomnych
	CArray <CMARFPTreeLeaf*> subNodes;

	// wskaznik do wezla nadrzednego
	CMARFPTreeLeaf *parent;

	// wartosc w sensie numeru transakcji, ktora ten wezel reprezentuje
	int value;

	// wsparcie wzorcow konczacych sie w tym wezle
	int count;

	// rozklad czestosci etykiet klas
	int *classDistibution;

	// liczba etykiet klas w sumie
	int classCount;

	// ilosc wzorcow ktore zakonczyly sie w tym wezle
	int patterns;

	// ilosc sciezek od tego miejsca
	int pathsCount;


//////////////////////////////////////////////////////////////////////////////////////////////////////
// metody
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// wstawianie obiektu do drzewa, funkcja rekurencyjna
	void insertTree(BinaryPointersItemset *itemset, int index = 0,
		CArray <CMARFPTreeHeaderTableElement*> *headers = 0);

	// pozyskanie obiektow wzwyz, z pominiecie tego wezla i korzenia
	BinaryPointersDataset *getItemsetsUpward();

	// pozyskanie najdluzszych spojnych elementow zawartych w sciezce
	Dataset * getPathLongestSubsets(bool excludeHead = true);

	// uzyskanie etykiety klasy najcesciej sie powtarzajcej w tym wezle
	int getMostDominantClass();

	// podglad
	void printTree(bool tree = false, int lvl = 0);

	// wtopienie do wezla nadrzednego - przepisane zostaja : 'patterns' oraz rozklas etykie klas
	void meldIntoParent(void);

	// pozsykanie jednego obiektu wzwyz
	BinaryPointersItemset *getItemsetUpward();

	// podlgad
	virtual void debugPrint() {
		printTree(false, 0);
	}

};
