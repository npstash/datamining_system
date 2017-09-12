#pragma once

//#include "stdafx.h"

#include "..\\JEP-ALG\\Itemset.h"
#include "..\\JEP-ALG\\Dataset.h"

#include "DebugPrinter.h"

class BinaryPointersItemset : public DebugPrinter, public CObject
{
public:
	BinaryPointersItemset(void);
	BinaryPointersItemset(BinaryPointersItemset &other);
	BinaryPointersItemset(Itemset &binJepItemset);
	~BinaryPointersItemset(void);

private:
	CUIntArray data;

public:

	int cl;
	int supp;
	int conf;
	int coverage;

	int getCount()
	{ return (int)data.GetCount(); }


	int getItem(int attributeNum);	
	bool hasItem(int attributeId);	
	void setItem(int attributeId, bool clear = false);

	int getNext(int attributeId);
	int getPrevious(int attributeId);

	void operator += (BinaryPointersItemset &other);
	void operator -= (BinaryPointersItemset &other);
	bool subsetOrEqualTo(BinaryPointersItemset &other);

	Dataset *getSubsets(int startFrom = 0);
	void sortAndPruneAs(BinaryPointersItemset * order);

	void debugPrint();
};
