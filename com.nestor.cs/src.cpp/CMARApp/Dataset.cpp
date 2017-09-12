// Dataset.cpp: implementation of the Dataset class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "Dataset.h"
#include "math.h"

#include "Itemset.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

Dataset::Dataset()
{

}

Dataset::~Dataset()
{
	RemoveAll();
}

void Dataset::deleteAll()
{
	for (int i = 0; i < GetSize(); i++) delete ((Itemset*)GetAt(i));
	RemoveAll();
}

void Dataset::deepCopyItemsetFrom(Dataset * from) {
	RemoveAll();
	for (int i = 0; i < from->GetSize(); i++) {
		Itemset * it = (Itemset*) from->GetAt(i);
		Itemset * newOne = new Itemset(*it);
		Add(newOne);
	}
}