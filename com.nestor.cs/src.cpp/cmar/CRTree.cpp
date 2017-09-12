#include "StdAfx.h"
#include ".\crtree.h"
#include ".\cmarfptree.h"

extern bool debug;

CRTree::CRTree() : 
CRTreeNode()
, classLablelsSupports(NULL)
, datasetSize(0)
, classCount(0)
, binAttrs(0)
{
	confDiffTH = 0.04f;
	suppDiffTH = 0.01f;
	currentChi2TH = 3.84f;
}

CRTree::CRTree(int* labels, int clCount, int datSize, int binAts) : 
CRTreeNode()
, classLablelsSupports(NULL)
, datasetSize(datSize)
, classCount(clCount)
, binAttrs(binAts)
{
	classLablelsSupports = new int [clCount];
	for (int i = 0; i < clCount; i++) {
		classLablelsSupports[i] = labels[i];
	}
}

CRTree::~CRTree(void)
{
	delete classLablelsSupports;

	// usuniecie naglowkow
	for (int i = 0; i < headerTable.GetCount(); i++) {
		delete headerTable.GetAt(i);
	}
}

void CRTree::debugPrint() {
	CRTreeNode::debugPrint();
}

void CRTree::insertRuleWithPruning(BinaryPointersItemset* rule, bool *rulesAdded) {
	if (rulesAdded) {
		*rulesAdded = false;
	}

if (debug)	printf("INSERTING RULE : "); 
if (debug)	rule->debugPrint();

	// first pruning : chi-square based
	if (classCount) {
		//if (debug)
		//float chi2 = computeChi2(rule->supp, rule->conf, datasetSize, classLablelsSupports[rule->cl]);
		//printf("CHI2 is %.5f\n", chi2);
		//printf("by vals: %d %d %d %d\n", rule->supp, rule->conf, datasetSize, classLablelsSupports[rule->cl]);
		if (!passesChi2(rule)) {
			//if (debug)
			//printf("RULE PRUNED BY CHI2\n");
			return;
		}
	}

	// rank prune
	// rank: R1 is higher than R2:
	// 1. R1.conf > R2.conf
	// 2. R1.conf == R2.conf && R1.supp > R2.supp
	// 3. R1.conf == R2.conf && R1.supp == R2.supp && R1.len < R2.len

	// is pruned?
	CRTreeNode * current = this;
	for (int atr = 0 ; atr < rule->getCount(); atr++) {
		int valToFind = rule->getItem(atr);
		CRTreeNode * node = 0;
		bool nodeFound = false;
		for (int n = 0; n < current->childNodes.GetCount(); n++) {
			node = (CRTreeNode *) current->childNodes.GetAt(n);
			if (node->value == valToFind) {
				nodeFound = true;
				break;
			}
		}
		if (nodeFound) {
			// rule found?
			if ((node->cl > -1) && (node->cl == rule->cl)) {
				// check if this rule prunes the new one
				if (isNodeHigherRankThanRule(node, rule) >= 0) {
if (debug)					printf("PRUNING RULE: "); 
if (debug)					rule->debugPrint();
					return;
				} else {
					// TODO:
					// moze sprunowac??
if (debug)					printf("PRUNING NODE\n"); 
					node->cl = -1;
					node->supp = 0;
					node->conf = 0;
					if (node->childNodes.IsEmpty() && (atr == rule->getCount()-1)) {
						node->cl = rule->cl;
						node->supp = rule->supp;
						node->conf = rule->conf;
						return;
					}
				}
			} // if same class
		} else {
			// 1. no pruning more general rules found
			// 2. node is longer than others in this path - it will be added
			*rulesAdded = true;
			insertRule(rule, 0, &headerTable);
			return;
		}
		current = node;
	}

	// rule was not pruned by more general rules.
	// now it may prune other rules
	// 'current' now points to the node, where the rule would end after insertion

	// check all subpaths:
	// for each node with class label same as rule.label
	//    if that node is pruned by rule
	//       1. its label is cleared
	//       2. if it has no child elements, it is deleted

	bool pruneRule= pruneChildren(current, rule->conf, rule->cl, rule->supp);

	if (!pruneRule) {
		insertRule(rule, 0, &headerTable);
		*rulesAdded = true;
	}
}

bool CRTree::pruneChildren(CRTreeNode* startFrom, int cf, int cl, int sp) {
	for (int c = 0; c < startFrom->childNodes.GetCount(); c++) {
		CRTreeNode* node = (CRTreeNode*) startFrom->childNodes.GetAt(c);
		if (node->cl == cl) {
			if (isHigherRank(cf, sp, node->conf, node->supp, datasetSize/*, true*/) >= 0) {
				// this node must be pruned
if (debug)				printf("NODE PRUNED cl:%d sp:%d cf:%d\n", node->cl, node->supp, node->conf);
				node->cl = -1;
				node->supp = 0;
				node->conf = 0;
				if (node->childNodes.IsEmpty()) {
if (debug)					printf("NODE DELETION\n");
					startFrom->childNodes.RemoveAt(c);
					removeFromHeadersTable(node);
					delete node;
					c--;
					continue;
				} else {
				} // if isempty
			} // if rank...
			else {
				// ?????? TODO:
				return true;
			}
		} // if cl
		if (!node->childNodes.IsEmpty()) {
			bool res = pruneChildren(node, cf, cl, sp);
			// ?????? TODO:
			if (res) return true;
			if ((node->childNodes.IsEmpty()) && (node->cl == -1)) {
if (debug)				printf("NODE DELETION\n");
				startFrom->childNodes.RemoveAt(c);
				removeFromHeadersTable(node);
				delete node;
				c--;
				continue;
			}
		}
	} // for
}

int CRTree::isHigherRank(int cf1, int sp1, int cf2, int sp2, int N/*, bool useTH*/) {
//	if (useTH) {
		double cf1f = double(((double)cf1) / ((double)sp1));
		double cf2f = double(((double)cf2) / ((double)sp2));
		/*double sp1f = double(((double)sp1) / ((double)N));
		double sp2f = double(((double)sp2) / ((double)N));*/
		int res1 = compareValuesByTH(cf1f, cf2f, 0/*confDiffTH*/);
		if (res1 != 0) return res1;
		//int res2 = compareValuesByTH(sp1f, sp2f, 0/*suppDiffTH*/);
		//if (res2 != 0) return res2;
		if (sp1 > sp2) return 1;
		if (sp1 < sp2) return -1;
/*	} else {
		if (cf1 > cf2) return 1;
		if (cf1 < cf2) return -1;
		if (sp1 > sp2) return 1;
		if (sp1 < sp2) return -1;
	}*/
	return 0;
}

int CRTree::isNodeHigherRankThanRule(CRTreeNode* node, BinaryPointersItemset* rule)
{
	int cmpRes = isHigherRank(node->conf, node->supp, rule->conf, rule->supp, datasetSize/*, true*/);

	if (cmpRes == 1) return 1;
	if (cmpRes == -1) return -1;

	// compute noded rule length
	int len = 0;
	CRTreeNode * n = node;
	while (n->parent) {
		len++;
		n = n->parent;
	}
	if (len < rule->getCount()) return 1;
	if (len > rule->getCount()) return -1;
	return 0;
}

float CRTree::computeChi2(int ruleSupp, int ruleConf_n, int n, int cSupp)
{
	float fruleSupp = (float) ruleSupp;
	float fn = (float) n;
	float fcSupp = (float) (cSupp);
	float fruleConf_n = (float) ruleConf_n;

	float Frc = float( fruleConf_n );
	float Fr_c = float( fruleSupp - fruleConf_n );
	float F_rc = float( fcSupp - fruleConf_n );
	float F_r_c = float( fn - Frc - F_rc - Fr_c );
	float Erc = float( fruleSupp * fcSupp / fn );
	float Er_c = float( fruleSupp * (fn - fcSupp) / fn );
	float E_rc = float( (fn - fruleSupp) * fcSupp / fn );
	float E_r_c = float( (fn - ruleSupp) * (fn - fcSupp) / fn );

	float chi2 = (Frc - Erc)*(Frc - Erc)/Erc;
	chi2 += (F_rc - E_rc)*(F_rc - E_rc)/E_rc;
	chi2 += (Fr_c - Er_c)*(Fr_c - Er_c)/Er_c;
	chi2 += (F_r_c - E_r_c)*(F_r_c - E_r_c)/E_r_c;

	return chi2;
}

double CRTree::computeMaxChi2(int ruleSupp, int cSupp, int T)
{
	double maxchi2 = (ruleSupp < cSupp) ? ruleSupp : cSupp;
	maxchi2 -= (double) ruleSupp * (double) cSupp / (double) T;
	maxchi2 = maxchi2 * maxchi2 * (double) T;
	double e = 1 / (double) cSupp / (double) ruleSupp;
	e += 1 / (double) cSupp / (double) (T - ruleSupp);
	e += 1 / (double) (T - cSupp) / (double) ruleSupp;
	e += 1 / (double) (T - cSupp) / (double) (T - ruleSupp);
	maxchi2 *= e;
	return maxchi2;
}

bool CRTree::passesChi2(BinaryPointersItemset* rule)
{
	// na ogol przyjmuje sie poziom prawdopodobienstwa zaleznosci 5%
	// z tablic matematycznych dla stopni swobody (1) wartosc progu wynosi 3.84
	float chi2 = computeChi2(rule->supp, rule->conf, datasetSize, classLablelsSupports[rule->cl]);
	return chi2 >= currentChi2TH;
}

void CRTree::sortHeaderTable(BinaryPointersItemset* FPList)
{
	// tworze nowa kopie tablicy naglowkow, gdzie bede umieszczal elementy starej tablicy
	// wg. okreslonego w FPList porzadku
	CArray <CArray<CRTreeNode*>*> newTable;

	for (int i = 0; i < FPList->getCount(); i++) {

		// pobieram kolejny element z FPList i wyszkuje odpowiadajacego elementu
		// w tablicy naglowkow
		int toFind = FPList->getItem(i);
		for (int j = 0; j < headerTable.GetCount(); j++) {
			CArray<CRTreeNode*> *elem = (CArray<CRTreeNode*>*) headerTable.GetAt(j);
			CRTreeNode * any = (CRTreeNode *) elem->GetAt(0);
			if (toFind == any->value) {

				// znaleziony element wpisuje do nowej tablicy
				newTable.Add(elem);
				break;
			}
		}
	}
	headerTable.RemoveAll();
	headerTable.Append(newTable);

}

BinaryPointersDataset ** CRTree::getRuleGroupsForItemset(BinaryPointersItemset* itemset, int covTH, float confTH__)
{
/*
	BinaryPointersDataset ** res = new BinaryPointersDataset* [classCount];

	for (int i = 0; i < classCount; i++) {
		res[i] = new BinaryPointersDataset(binAttrs);
	}
*/
	BinaryPointersDataset * allRules = new BinaryPointersDataset(binAttrs);

	// wyciaganie regul z drzewa odbywa sie nastepujaco:
	// klasyfikowany element powinien byc posortowany wg FPListy
	// bierzemy ostatni z elementow i patrzymy na odpowiadajacy mu element w tablicy naglowkow
	// dla kazdego z elementow listy w tym miejscu tablicy naglowkow patrzymy 'wzwyz'
	// aby uzyskac reguly

	for (int i = itemset->getCount()-1; i >= 0; i--) {
		int val = itemset->getItem(i);

		// przeszukanie tablicy naglowkow
		CArray<CRTreeNode*>* found = 0;

		for (int j = 0; j < headerTable.GetCount(); j++) {
			found = (CArray<CRTreeNode*>*) headerTable.GetAt(j);
			CRTreeNode * first = (CRTreeNode *) found->GetAt(0);
			if (first->value == val) {

				// znaleziono odpowiednia liste w tablicy naglowkow
				for (int k = 0; k < found->GetCount(); k++) {
					CRTreeNode * theNode = (CRTreeNode *) found->GetAt(k);
					if (theNode->cl == -1) {
						continue;
					}
					// generowanie reguly. ('patrzenie w gore')
					// sprawdza sie przy kazdym odwiedzanym wezle,
					// czy jego wartosc zawiera sie w klasyfikowanym przykladzie
					BinaryPointersItemset * newRule = new BinaryPointersItemset();
					newRule->cl = theNode->cl;
					newRule->supp = theNode->supp;
					newRule->conf = theNode->conf;
					bool matched = true;
                    CRTreeNode * start = theNode;
					while (start->parent) {
						if (itemset->hasItem(start->value)) {
							newRule->setItem(start->value);
							start = start->parent;
						} else {
							matched = false;
							break;
						}
					}
					if (matched) {
						// regula zbudowana, dodanie do zbioru
						//res[newRule->cl]->Add(newRule);
						allRules->Add(newRule);
					} else {
						// nie dopasowano reguly
						delete newRule;
					}
				}
			} else {
				continue;
			} // if val
		} // for header elements
	} // for items

	// trzeba posortowac reguly
	BinaryPointersDataset * sortedRules = CMARFPTree::sortRules(binAttrs, this, allRules);
	delete allRules;

	BinaryPointersDataset ** res = new BinaryPointersDataset* [classCount];

	for (int i = 0; i < classCount; i++) {
		res[i] = new BinaryPointersDataset(binAttrs);
	}

	int added = 0;
	float cfF;
	BinaryPointersItemset * firstRule = NULL;
	for (int i = 0; i < sortedRules->GetCount(); i++) {
		if (added >= covTH)
			break;
		BinaryPointersItemset * rule = (BinaryPointersItemset *) sortedRules->GetAt(i);
		if (firstRule == NULL) {
			firstRule = rule;	
			cfF = (float) ( ((float) firstRule->conf) / ((float) firstRule->supp));
		}
		if (rule != firstRule) {
			float cfR = (float) ( ((float) rule->conf) / ((float) rule->supp));
			if (cfF - cfR > confTH__) {
				break;
			}
		}
		if (rule->cl < 1)
			continue;
		res[rule->cl]->Add(rule);
		added++;
		sortedRules->RemoveAt(i);
		i--;
	}
	sortedRules->deleteAll();
	delete sortedRules;

	return res;
}

void CRTree::removeFromHeadersTable(CRTreeNode* node)
{
	CArray<CRTreeNode*>* found = 0;

	for (int j = 0; j < headerTable.GetCount(); j++) {
		found = (CArray<CRTreeNode*>*) headerTable.GetAt(j);

		for (int k = 0; k < found->GetCount(); k++) {
			CRTreeNode * n = (CRTreeNode *) found->GetAt(k);
			if (n == node) {
				found->RemoveAt(k);
				if (found->IsEmpty()) {
					headerTable.RemoveAt(j);
					delete found;
				}
				return;
			}
		}
	}
}

int CRTree::compareValuesByTH(double v1, double v2, double TH)
{
	if ((v1 <= v2 + TH) && (v1 >= v2 - TH)) return 0;
	if (v1 > v2 + TH) return 1;
	return -1;
}
