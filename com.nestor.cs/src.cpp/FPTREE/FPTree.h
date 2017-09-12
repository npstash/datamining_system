#ifndef FPTREE
#define FPTREE

#include "stdafx.h"
#include "FPTreeHeaderTableElement.h"
#include "BinaryPointersDataset.h"
#include "FPTreeLeaf.h"

class FPTree : public FPTreeLeaf
{
public:
	FPTree(BinaryPointersDataset * dat, int suppTH, BinaryPointersItemset *con = 0);
	~FPTree(void);

	CArray <FPTreeHeaderTableElement*> headerTable; 
	BinaryPointersItemset * conditionalTo;
	BinaryPointersItemset * FPList;
	int supportTH;
	int binAttrCnt;

	void sortHeader();
	BinaryPointersDataset* FPGrowth(BinaryPointersDataset *res = 0);

	virtual void debugPrint();
};

#endif
