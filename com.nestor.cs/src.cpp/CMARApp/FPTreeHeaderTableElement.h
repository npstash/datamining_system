#pragma once

#include "stdafx.h"
#include "DebugPrinter.h"
extern class FPTreeLeaf;

class FPTreeHeaderTableElement :
	public CArray <FPTreeLeaf*>, public DebugPrinter
{
public:
	FPTreeHeaderTableElement(void);
	virtual ~FPTreeHeaderTableElement(void);

	int attribute;
	int computeSupport();

	virtual void debugPrint();
};
