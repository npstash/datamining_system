#pragma once

#include "stdafx.h"
#include "..\FPTree\DebugPrinter.h"

extern class CMARFPTreeLeaf;

class CMARFPTreeHeaderTableElement :
	public CArray <CMARFPTreeLeaf*>, public DebugPrinter
{
public:
	CMARFPTreeHeaderTableElement(void);
	~CMARFPTreeHeaderTableElement(void);

	int attribute;
	int computeSupport();

	void computeMostDominantClass(int *cl, int *conf);
	void meldNodesIntoParents(void);

	virtual void debugPrint();
};
