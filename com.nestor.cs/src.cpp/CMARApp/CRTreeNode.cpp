#include "StdAfx.h"
#include ".\crtreenode.h"

CRTreeNode::CRTreeNode(void) :
	conf(0),
	supp(0),
	parent(0),
	cl(-1),
	value(-1)
{
}

CRTreeNode::~CRTreeNode(void)
{
	for (int i = 0; i < childNodes.GetCount(); i++) {
		delete childNodes.GetAt(i);
	}
}

void CRTreeNode::insertRule(BinaryPointersItemset* rule, int index, CArray <CArray<CRTreeNode*>*> *headers) {
	if (index == rule->getCount()) {
		supp = rule->supp;
		conf = rule->conf;
		cl = rule->cl;
		return;
	}
	for (int i = 0; i < childNodes.GetCount(); i++) {
		CRTreeNode* tmp = (CRTreeNode*) childNodes.GetAt(i);
		if (tmp->value == rule->getItem(index)) {
			tmp->insertRule(rule, index + 1, headers);
			return;
		}
	}
	CRTreeNode *newone = new CRTreeNode();
	newone->parent = this;
	newone->value = rule->getItem(index);
	childNodes.Add(newone);

	// dodanie do tablic naglowkow
	if (headers) {
		CArray<CRTreeNode*> *addTo = 0;
		for (int i = 0; i < headers->GetCount(); i++) {
			CArray<CRTreeNode*> *h = (CArray<CRTreeNode*> *) headers->GetAt(i);
			CRTreeNode * any = (CRTreeNode *) h->GetAt(0);
			if (any->value == newone->value) {
				addTo = h;
				break;
			}
		}
		if (!addTo) {
			addTo = new CArray<CRTreeNode*>();
			headers->Add(addTo);
		}
		addTo->Add(newone);
	}

	// rekurencyjnie dodaje kolejny wezel
	newone->insertRule(rule, index + 1);
}

void CRTreeNode::debugPrint() {
	printNode(true);
}

void CRTreeNode::printNode(bool tree, int lvl)
{
	for (int i = 0; i < lvl; i++) {
		printf("\t");
	}
	printf("Val:%d Supp:%d Conf:%d Cl:%d\n", value, supp, conf, cl);
	if (tree) {
		for (int i = 0; i < childNodes.GetCount(); i++) {
			CRTreeNode* tmp = (CRTreeNode*) childNodes.GetAt(i);
			tmp->printNode(tree, lvl+1);
		}
	}
}

BinaryPointersDataset* CRTreeNode::generateRuleDataset(BinaryPointersDataset* addTo)
{
	if (cl > -1) {
		// rule creation
		BinaryPointersItemset * bpi = new BinaryPointersItemset();
		bpi->supp = supp;
		bpi->cl = cl;
		bpi->conf = conf;
		CRTreeNode* enumerator = this;
		while (enumerator->parent) {
			bpi->setItem(enumerator->value);
			enumerator = enumerator->parent;
		}
		addTo->Add(bpi);
	}
	for (int i = 0; i < childNodes.GetCount(); i++) {
		CRTreeNode* child = (CRTreeNode*) childNodes.GetAt(i);
		child->generateRuleDataset(addTo);
	}
	return addTo;
}

