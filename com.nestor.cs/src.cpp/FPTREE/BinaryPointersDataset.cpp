#include "StdAfx.h"
#include ".\binarypointersdataset.h"

BinaryPointersDataset::BinaryPointersDataset(int ba) : 
	binaryAttributes(ba)
{
}

BinaryPointersDataset::~BinaryPointersDataset(void)
{
}

BinaryPointersItemset * BinaryPointersDataset::getSortedFPList(int suppTH) {
	BinaryPointersItemset * tmp = 0;
	getFPListAndClassDistribution(&tmp, suppTH);
	return tmp;
}

void BinaryPointersDataset::debugPrint() {
	for (int i = 0; i < GetCount(); i++) {
		BinaryPointersItemset * bpi = (BinaryPointersItemset*) GetAt(i);
//		printf("%d ", i);
		bpi->debugPrint();
	}
}

void BinaryPointersDataset::getFPListAndClassDistribution(BinaryPointersItemset** pFPList, int suppTH, 
														  int** pclassDistribution, int classCount)
{
	if (classCount) {
		*pclassDistribution = new int [classCount];
		for (int i = 0; i < classCount; i++) {
			(*pclassDistribution)[i] = 0;
		}
	}
	int *items = new int [binaryAttributes];
	for (int i = 0; i < binaryAttributes; i++) {
		items[i] = 0;
	}
	for (int i = 0; i < GetCount(); i++) {
		BinaryPointersItemset * bpi = (BinaryPointersItemset*) GetAt(i);
		for (int j = 0; j < bpi->getCount(); j++) {
			items[bpi->getItem(j)] += bpi->supp;
		}
		if (classCount) {
			(*pclassDistribution)[bpi->cl] += bpi->supp;
		}
	}
	// sortowanie
	BinaryPointersItemset * res = new BinaryPointersItemset();
	do {
		int maxVal = -1;
		int maxI = -1;
		for (int i = 0; i < binaryAttributes; i++) {
			if (items[i] >= suppTH) { 
				if (items[i] > maxVal) {
					maxVal = items[i];
					maxI = i;
				}
			}
		}//for
		if (maxI+1) {
			items[maxI] = -1;
			res->setItem(maxI);
		} else break;
	}  while (true);
	delete items;
	*pFPList = res;
}

void BinaryPointersDataset::deepCopyItemsetFrom(Dataset * from) {
	RemoveAll();
	for (int i = 0; i < from->GetSize(); i++) {
		BinaryPointersItemset * it = (BinaryPointersItemset*) from->GetAt(i);
		BinaryPointersItemset * newOne = new BinaryPointersItemset(*it);
		Add(newOne);
	}
}
