#include "StdAfx.h"
#include ".\fptreeleaf.h"

FPTreeLeaf::FPTreeLeaf(void)
{
	count = 0;
	value = -1;
	parent = 0;
}

FPTreeLeaf::~FPTreeLeaf(void)
{
	for (int i = 0; i < subNodes.GetCount(); i++) {
		delete subNodes.GetAt(i);
	}
}

void FPTreeLeaf::insertTree(BinaryPointersItemset *itemset, int index, 
CArray <FPTreeHeaderTableElement*> *headers) {
	count += itemset->supp;
	if (index == itemset->getCount()) {
		return;
	}
	int currentItem = itemset->getItem(index);
	for (int i = 0; i < subNodes.GetCount(); i++) {
		FPTreeLeaf * b = subNodes.GetAt(i);
		if (b->value == currentItem) {
			b->insertTree(itemset, index+1, headers);
			return;
		}
	}
	// nie dopasowano
	FPTreeLeaf *nleaf = new FPTreeLeaf();
	subNodes.Add(nleaf);
	// dodanie do tablic naglowkow
	if (headers) {
		FPTreeHeaderTableElement *addTo = 0;
		for (int i = 0; i < headers->GetCount(); i++) {
			FPTreeHeaderTableElement *h = (FPTreeHeaderTableElement *) headers->GetAt(i);
			if (h->attribute == currentItem) {
				addTo = h;
				break;
			}
		}
		if (!addTo) {
			addTo = new FPTreeHeaderTableElement();
			addTo->attribute = currentItem;
			headers->Add(addTo);
		}
		addTo->Add(nleaf);
	}
	nleaf->value = currentItem;
	nleaf->parent = this;
	nleaf->insertTree(itemset, index+1, headers);
}

int FPTreeLeaf::getPathsCount() {
	if (subNodes.IsEmpty())
		return 1;
	int m = 0;
	for (int i = 0; i < subNodes.GetCount(); i++) {
		FPTreeLeaf * f = (FPTreeLeaf*) subNodes.GetAt(i);
		m += f->getPathsCount();
	}
	return m;
}


void FPTreeLeaf::printTree(bool tree, int lvl) {
	for (int i = 0; i < lvl; i++) {
		printf("\t");
	}
	printf("Val: %d  Count: %d\n", value, count);
	if (!tree) return;
	for (int i = 0; i < subNodes.GetCount(); i++) {
		FPTreeLeaf * b = subNodes.GetAt(i);
		b->printTree(true, lvl + 1);
	}
}

BinaryPointersItemset *FPTreeLeaf::getItemsetUpward() {
	BinaryPointersItemset *res = new BinaryPointersItemset();
	res->supp = count;
	FPTreeLeaf *sentry = this->parent;
	while (sentry) {
		if (sentry->parent) {
			res->setItem(sentry->value);
		}
		sentry = sentry->parent;
	}
	return res;
}

Dataset *FPTreeLeaf::getPathSubsets(bool excludeHead) {
	Dataset *res = new Dataset();
	BinaryPointersItemset *bpi = new BinaryPointersItemset();
	if (!excludeHead) {
		bpi->setItem(value);
		bpi->supp = count;
	} else excludeHead = false;
	if (!subNodes.IsEmpty()) {
		Dataset *cab = ((FPTreeLeaf*)subNodes.GetAt(0))->getPathSubsets(excludeHead);
		if (bpi->getCount() > 0) {
			Dataset *cab2 = new Dataset();
			for (int j = 0; j < cab->GetCount(); j++) {
				BinaryPointersItemset * copyIt = (BinaryPointersItemset*) cab->GetAt(j);
				BinaryPointersItemset * copy = new BinaryPointersItemset(*copyIt);
				*copy += *bpi;
				if (copy->supp < bpi->supp) bpi->supp = copy->supp;
				cab2->Add(copy);
			}
			res->Append(*cab2);
			delete cab2;
		}
		res->Append(*cab);
		delete cab;
	}
	if (bpi->getCount() > 0) {
		res->Add(bpi);
	}
	return res;
}
