#ifndef FPTREELEAF
#define FPTREELEAF

#include "stdafx.h"
#include "BinaryPointersItemset.h"
#include "FPTreeHeaderTableElement.h"
#include "DebugPrinter.h"
#include "..\\JEP-ALG\\Dataset.h"

class FPTreeLeaf : public DebugPrinter
{
public:
	FPTreeLeaf(void);
	virtual ~FPTreeLeaf(void);

	int value;
	int count;
	
	CArray <FPTreeLeaf*> subNodes;
	FPTreeLeaf *parent;

	void insertTree(BinaryPointersItemset *itemset, int index = 0,
		CArray <FPTreeHeaderTableElement*> *headers = 0);
	
	int getPathsCount();
	
	virtual BinaryPointersItemset *getItemsetUpward();
	virtual Dataset * getPathSubsets(bool excludeHead = true);

	virtual void debugPrint() {
		FPTreeLeaf::printTree(false, 0);
	}

	virtual void printTree(bool tree = false, int lvl = 0);

};

#endif