#pragma once

#include "BinaryPointersItemset.h"
#include "DebugPrinter.h"

#include "..\\JEP-ALG\\Dataset.h"

class BinaryPointersDataset :
	public Dataset, public DebugPrinter
{
public:
	BinaryPointersDataset(int binAttrs);
	~BinaryPointersDataset(void);

	int binaryAttributes;
	BinaryPointersItemset * getSortedFPList(int suppTH);

	void debugPrint();
	void getFPListAndClassDistribution(BinaryPointersItemset** pFPList, int suppTH,
		int** pclassDistribution = 0, int classCount = 0);

	virtual void deepCopyItemsetFrom(Dataset * from);
};

