#include "StdAfx.h"
#include ".\cmarfptreeheadertableelement.h"
#include "cmarfptreeleaf.h"

CMARFPTreeHeaderTableElement::CMARFPTreeHeaderTableElement(void)
{
}

CMARFPTreeHeaderTableElement::~CMARFPTreeHeaderTableElement(void)
{
}

void CMARFPTreeHeaderTableElement::computeMostDominantClass(int *cl, int *conf) 
{
	*cl = -1;
	int res = -1;
	if (IsEmpty()) return;
	int clcnt = ((CMARFPTreeLeaf *) GetAt(0))->classCount;
	int *cls = new int[clcnt];
	for (int c = 0; c < clcnt; c++) {
		cls[c] = 0;
	}
	for (int i = 0; i < GetCount(); i++) {
		CMARFPTreeLeaf *l = (CMARFPTreeLeaf *) GetAt(i);
		for (int c = 0; c < clcnt; c++) {
			cls[c] += l->classDistibution[c];
		}
	}
	int max = -1;
	int atMax = 0;
	for (int i = 0; i < clcnt; i++) {
		if ((cls[i] > max) && (cls[i] > 0)) {
			max = cls[i];
			atMax = 1;
			res = i;
		} else
		if (cls[i] == max) {
			atMax++;
		}
	}
	*cl = (atMax == 1) ? res : -1;
	*conf = (atMax == 1) ? cls[*cl] : -1;
	delete cls;
}

void CMARFPTreeHeaderTableElement::meldNodesIntoParents(void)
{
	for (int i = 0; i < GetCount(); i++) {
		CMARFPTreeLeaf *l = (CMARFPTreeLeaf *) GetAt(i);
		if (l->parent) {
			l->meldIntoParent();
			for (int j = 0; j < l->parent->subNodes.GetCount(); j++) {
				CMARFPTreeLeaf *chk = (CMARFPTreeLeaf*) l->parent->subNodes.GetAt(j);
				if (chk == l) {
					l->parent->subNodes.RemoveAt(j);
					if (l->parent->pathsCount > 1) {
						// update pathsCount
						CMARFPTreeLeaf * tmp1 = l->parent;
						while (tmp1) {
							tmp1->pathsCount--;
							tmp1 = tmp1->parent;
						}
					} // pathsCount
					break;
				}
			}
			delete l;
		}
	}
}

int CMARFPTreeHeaderTableElement::computeSupport() {
	int res = 0;
	for (int i = 0; i < GetCount(); i++) {
		CMARFPTreeLeaf *b = (CMARFPTreeLeaf*) GetAt(i);
		res += b->count;
	}
	return res;
}

void CMARFPTreeHeaderTableElement::debugPrint() {
	CString s;
	s.Format("%d");
	DebugPrinter::print(s);
	for (int i = 0; i < GetCount(); i++) {
		CMARFPTreeLeaf *b = (CMARFPTreeLeaf*) GetAt(i);
		b->debugPrint();
	}
}

