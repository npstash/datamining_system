#pragma once

#include "stdafx.h"

#include "..\FPTree\BinaryPointersDataset.h"

#include "crtreenode.h"

class CRTree : public CRTreeNode
{
public:
	CRTree(void);
	CRTree(int* labels, int clCount, int datSize, int binAts);
	virtual ~CRTree(void);

//////////////////////////////////////////////////////////////////////////////////////////////////////
// dane
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// tablica naglowkow. potrzebna do wyszukiwania regul w drzewie
	CArray <CArray<CRTreeNode*>*> headerTable;

	// rozklad czestosci wystapien poszczegolnych etykiet klasy
	int* classLablelsSupports;

	// rozmiar danych uczacych, z ktorego wygenerowano reguly, potrzebne do Chi2
	int datasetSize;

	// ilosc etykiet klasy
	int classCount;

	// ilosc atrybutow binarnych (transakcji)
	int binAttrs;

	// prog statystyki Chi2
	float currentChi2TH;

	// dopuszczalna roznica zaufania do zachowania jednakowej rangi
	float confDiffTH;

	// dopuszczalna roznica wsparcia do zachowania jednakowej rangi
	float suppDiffTH;


//////////////////////////////////////////////////////////////////////////////////////////////////////
// metody
//////////////////////////////////////////////////////////////////////////////////////////////////////

	// wstawienie reguly wraz z redukcja rankingowa oraz chi2
	void insertRuleWithPruning(BinaryPointersItemset* rule, bool *rulesAdded);

	// okreslenie czy nowa regula jest lepsza niz dany wezel
	int isNodeHigherRankThanRule(CRTreeNode* node, BinaryPointersItemset* rule);
	
	// porownanie rang
	inline int isHigherRank(int cf1, int sp1, int cf2, int sp2, int N, bool useTHs);
	
	// redukcja drzewa przy nowej regule
	bool pruneChildren(CRTreeNode* startFrom, int cf, int cl, int sp);

	// obliczenie wartosci chi2
	static float computeChi2(int ruleSupp, int ruleConf_n, int n, int cSupp);
	
	// obliczenie wartosci maxchi2
	static double computeMaxChi2(int ruleSupp, int cSupp, int T);

	// czy dana regula jest pozytywnie skorelowana
	inline bool passesChi2(BinaryPointersItemset* rule);

	// podglad
	virtual void debugPrint();

	// sortowanie naglowka
	void sortHeaderTable(BinaryPointersItemset* FPList);

	// generowanie grup regul klasyfikujacych dany obiekt na rzecz poszczegolnych wartosci klasy
	BinaryPointersDataset ** getRuleGroupsForItemset(BinaryPointersItemset* itemset);

	void removeFromHeadersTable(CRTreeNode* node);

	int compareValuesByTH(float v1, float v2, float TH);
};
