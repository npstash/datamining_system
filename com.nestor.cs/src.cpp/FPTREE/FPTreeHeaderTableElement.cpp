#include "StdAfx.h"
#include ".\fptreeheadertableelement.h"
#include "FPTreeLeaf.h"

FPTreeHeaderTableElement::FPTreeHeaderTableElement(void)
{
}

FPTreeHeaderTableElement::~FPTreeHeaderTableElement(void)
{
}

int FPTreeHeaderTableElement::computeSupport() {
	int res = 0;
	for (int i = 0; i < GetCount(); i++) {
		FPTreeLeaf *b = (FPTreeLeaf*) GetAt(i);
		res += b->count;
	}
	return res;
}

void FPTreeHeaderTableElement::debugPrint() {
	CString s;
	s.Format("%d");
	DebugPrinter::print(s);
	for (int i = 0; i < GetCount(); i++) {
		FPTreeLeaf *b = (FPTreeLeaf*) GetAt(i);
		b->debugPrint();
	}
}
