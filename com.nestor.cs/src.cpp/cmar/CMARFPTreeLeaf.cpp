#include "StdAfx.h"
#include ".\CMARfptreeleaf.h"

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
CMARFPTreeLeaf::CMARFPTreeLeaf(int classes)
{
	patterns = 0;
	classCount = classes;
    classDistibution = new int[classes];
	for (int i = 0; i < classCount; i++) 
		classDistibution[i] = 0;
	count = 0;
	value = -1;
	parent = 0;
	pathsCount = 1;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
CMARFPTreeLeaf::~CMARFPTreeLeaf(void)
{
	while (!subNodes.IsEmpty()) {
		CMARFPTreeLeaf * toDel = (CMARFPTreeLeaf*) subNodes.GetAt(0);
		delete toDel;
		subNodes.RemoveAt(0);
	}
	delete classDistibution;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
void CMARFPTreeLeaf::insertTree(BinaryPointersItemset *itemset, int index, 
CArray <CMARFPTreeHeaderTableElement*> *headers) {
	count += itemset->supp;
	if (index == itemset->getCount()) {
		classDistibution[itemset->cl] += itemset->supp;
		patterns += itemset->supp;
		return;
	}
	int currentItem = itemset->getItem(index);
	for (int i = 0; i < subNodes.GetCount(); i++) {
		CMARFPTreeLeaf * b = (CMARFPTreeLeaf *) subNodes.GetAt(i);
		if (b->value == currentItem) {
			b->insertTree(itemset, index+1, headers);
			return;
		}
	}
	// nie dopasowano
	CMARFPTreeLeaf *nleaf = new CMARFPTreeLeaf(classCount);
	subNodes.Add(nleaf);
	
	// update pathsCount
	if (subNodes.GetCount() > 1) {
		CMARFPTreeLeaf * tmp1 = this;
		while (tmp1) {
			tmp1->pathsCount++;
			tmp1 = tmp1->parent;
		}
	}

	// dodanie do tablic naglowkow
	if (headers) {
		CMARFPTreeHeaderTableElement *addTo = 0;
		for (int i = 0; i < headers->GetCount(); i++) {
			CMARFPTreeHeaderTableElement *h = (CMARFPTreeHeaderTableElement *) headers->GetAt(i);
			if (h->attribute == currentItem) {
				addTo = h;
				break;
			}
		}
		if (!addTo) {
			addTo = new CMARFPTreeHeaderTableElement();
			addTo->attribute = currentItem;
			headers->Add(addTo);
		}
		addTo->Add(nleaf);
	}
	nleaf->value = currentItem;
	nleaf->parent = this;
	nleaf->insertTree(itemset, index+1, headers);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
void CMARFPTreeLeaf::printTree(bool tree, int lvl) {
	for (int i = 0; i < lvl; i++) {
		printf("\t");
	}
	printf("Val: %d  Count: %d  Patterns: %d Paths: %d", value, count, patterns, pathsCount);
	for (int i = 0; i < classCount; i++) {
		if (classDistibution[i])
			printf(" CL:%d(%d)", i, classDistibution[i]);
	}
	printf("\n");
	if (!tree) return;
	for (int i = 0; i < subNodes.GetCount(); i++) {
		CMARFPTreeLeaf * b = (CMARFPTreeLeaf *) subNodes.GetAt(i);
		b->printTree(true, lvl + 1);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
BinaryPointersDataset *CMARFPTreeLeaf::getItemsetsUpward() {
	BinaryPointersDataset *res = new BinaryPointersDataset(classCount);
	for (int c = 0; c < classCount; c++) {
		if (!classDistibution[c]) continue;
		BinaryPointersItemset *b = getItemsetUpward();
		b->supp = classDistibution[c];
		b->conf = classDistibution[c];
		b->cl = c;
		if (b->getCount()) {
			res->Add(b);
		} else delete b;
	}
	return res;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
int CMARFPTreeLeaf::getMostDominantClass() {
	// naturally, confidence threshold is above 50%
	// hence if two classes are equally preferred
	// none of them is dominant
	int res = -1;
	int max = -1;
	int atMax = 0;
	for (int i = 0; i < classCount; i++) {
		if ((classDistibution[i] > max) && (classDistibution[i] > 0)) {
			max = classDistibution[i];
			atMax = 1;
			res = i;
		} else
		if (classDistibution[i] == max) {
			atMax++;
		}
	}
	return (atMax == 1) ? res : -1;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
Dataset *CMARFPTreeLeaf::getPathLongestSubsets(bool excludeHead) {
	Dataset *res = new Dataset();
	if (!subNodes.IsEmpty()) {
		CMARFPTreeLeaf *head = 0;
		CMARFPTreeLeaf *start = 0;
		CMARFPTreeLeaf *tail = 0;
		if (excludeHead)
			head = (CMARFPTreeLeaf *) subNodes.GetAt(0);
		else head = this; 

		int lastSupp = -1;
		start = head;
		while (!start->subNodes.IsEmpty()) {
			start = (CMARFPTreeLeaf*) start->subNodes.GetAt(0); 
		}
		tail = start;
		while (start != head->parent) {
			if (start->count != lastSupp) {
				lastSupp = start->count;

				int domCl = start->getMostDominantClass();
				if (domCl > -1) {
					BinaryPointersItemset * newb = new BinaryPointersItemset();
					newb->supp = start->count;
					newb->cl = domCl;
					newb->conf = start->classDistibution[domCl];
					// look back downward to compute confidence
                    CMARFPTreeLeaf* downwardLook = tail;
					while (downwardLook != start) {
						if (downwardLook->classDistibution[domCl]) {
							newb->conf += downwardLook->classDistibution[domCl];
						}
						downwardLook = (CMARFPTreeLeaf*) downwardLook->parent;
					}

					res->Add(newb);
				}
			}
			for (int i = 0; i < res->GetCount(); i++) {
				BinaryPointersItemset * tmp = 
					(BinaryPointersItemset *) res->GetAt(i);
				tmp->setItem(start->value);
			}

			start = (CMARFPTreeLeaf*) start->parent;
		}
	}
	return res;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
void CMARFPTreeLeaf::meldIntoParent(void)
{
	if (!parent) return;
	CMARFPTreeLeaf* p = (CMARFPTreeLeaf*)parent;
	for (int i = 0; i < classCount; i++) {
		p->classDistibution[i] += classDistibution[i];
	}
	p->patterns += patterns;
}


//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
BinaryPointersItemset *CMARFPTreeLeaf::getItemsetUpward() {
	BinaryPointersItemset *res = new BinaryPointersItemset();
	res->supp = count;
	CMARFPTreeLeaf *sentry = this->parent;
	while (sentry) {
		if (sentry->parent) {
			res->setItem(sentry->value);
		}
		sentry = sentry->parent;
	}
	return res;
}
