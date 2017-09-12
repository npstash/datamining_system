#pragma once

#include "stdafx.h"

#include "..\FPTree\BinaryPointersItemset.h"
#include "..\FPTree\BinaryPointersDataset.h"
#include "..\FPTree\DebugPrinter.h"

class CRTreeNode : public DebugPrinter
{
public:
	CRTreeNode(void);
	virtual ~CRTreeNode(void);

	CArray <CRTreeNode*> childNodes;
	int conf;
	int supp;
	int cl;
	int value;
	CRTreeNode *parent;

	void insertRule(BinaryPointersItemset* rule, int index = 0, CArray <CArray<CRTreeNode*>*> *headers = 0);

	virtual void debugPrint();
	void printNode(bool tree = false, int lvl = 0);

	BinaryPointersDataset* generateRuleDataset(BinaryPointersDataset* addTo);
};
