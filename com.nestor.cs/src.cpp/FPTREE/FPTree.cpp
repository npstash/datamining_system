#include "StdAfx.h"
#include ".\fptree.h"
#include "..\\JEP-ALG\\Dataset.h"

FPTree::FPTree(BinaryPointersDataset * dat, int suppTH, BinaryPointersItemset* con) : 
	FPTreeLeaf()
{
	FPList = dat->getSortedFPList(suppTH);
	if (con)
		conditionalTo = new BinaryPointersItemset(*con);
	else conditionalTo = new BinaryPointersItemset();
	for (int i = 0; i < dat->GetCount(); i++) {
		BinaryPointersItemset *tmp = (BinaryPointersItemset *) dat->GetAt(i);
		BinaryPointersItemset *newone = new BinaryPointersItemset(*tmp);
		newone->sortAndPruneAs(FPList);
		insertTree(newone, 0, &headerTable);
		delete newone;
	}
	sortHeader();
	binAttrCnt = dat->binaryAttributes;
	supportTH = suppTH;
}

FPTree::~FPTree(void)
{
	delete conditionalTo;
	delete FPList;
	for (int i = 0; i < headerTable.GetCount(); i++) {
		delete headerTable.GetAt(i);
	}
}

void FPTree::debugPrint() {
	printTree(true);
	CString s;
	for (int i = 0; i < headerTable.GetCount(); i++) {
		FPTreeHeaderTableElement *elem = (FPTreeHeaderTableElement *) headerTable.GetAt(i);
		s.Format("HT Element %d", i);
		DebugPrinter::print(s);
		for (int j = 0; j < elem->GetCount(); j++) {
			FPTreeLeaf *fptl = (FPTreeLeaf *) elem->GetAt(j);
			fptl->debugPrint();
		}
	}
}

void FPTree::sortHeader() {
	CArray <FPTreeHeaderTableElement*> newTable;
	for (int i = 0; i < FPList->getCount(); i++) {
		int toFind = FPList->getItem(i);
		for (int j = 0; j < headerTable.GetCount(); j++) {
			FPTreeHeaderTableElement *elem = (FPTreeHeaderTableElement *) headerTable.GetAt(j);
			if (toFind == elem->attribute) {
				newTable.Add(elem);
				break;
			}
		}
	}
	headerTable.RemoveAll();
	headerTable.Append(newTable);
}

BinaryPointersDataset* FPTree::FPGrowth(BinaryPointersDataset * res) {
	if (!res) res = new BinaryPointersDataset(binAttrCnt);
	bool isSinglePath = (getPathsCount() == 1);
	if (isSinglePath) {
		Dataset *dat = getPathSubsets(true);
		for (int i = 0; i < dat->GetCount(); i++) {
			BinaryPointersItemset *b = (BinaryPointersItemset*) dat->GetAt(i);
			*b += *conditionalTo;
			res->Add(b);
		}
	} else {
		for (int i = (int)headerTable.GetCount() - 1; i >= 0; i--) {
			BinaryPointersItemset *b = new BinaryPointersItemset (*conditionalTo);
			FPTreeHeaderTableElement* HTelem = (FPTreeHeaderTableElement*)headerTable.GetAt(i);
			b->setItem(HTelem->attribute);
			b->sortAndPruneAs(FPList);
			b->supp = HTelem->computeSupport();
			BinaryPointersDataset * dat = new BinaryPointersDataset(binAttrCnt);
			// construcition of conditional pattern base
			for (int j = 0; j < HTelem->GetCount(); j++) {
				FPTreeLeaf *leaf = (FPTreeLeaf *) HTelem->GetAt(j);
				BinaryPointersItemset *it = leaf->getItemsetUpward();
				dat->Add(it);
			}
			res->Add(b);
			FPTree* tmpTree = new FPTree(dat, supportTH, b);
			if (!tmpTree->subNodes.IsEmpty()) {
				tmpTree->FPGrowth(res);
			}
			dat->deleteAll();
			delete dat;
			delete tmpTree;
		}
	}
	return res;
}
