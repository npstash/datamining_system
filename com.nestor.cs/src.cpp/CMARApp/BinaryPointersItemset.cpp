#include "StdAfx.h"
#include ".\binarypointersitemset.h"
#include ".\binarypointersdataset.h"

BinaryPointersItemset::BinaryPointersItemset(void) :
coverage(0),
cl(-1),
supp(0),
conf(0)
{
}

BinaryPointersItemset::BinaryPointersItemset(BinaryPointersItemset &other) {
	for (int i = 0; i < other.data.GetCount(); i++) {
		data.Add(other.data.GetAt(i));
	}
	cl = other.cl;
	supp = other.supp;
	conf = other.conf;
	coverage = other.coverage;
}

BinaryPointersItemset::BinaryPointersItemset(Itemset &binJepItemset) {
	for (int i = 0; i < binJepItemset.itemset_len; i++) {
		if (binJepItemset.getItem(i))
			setItem(i);
	}
	cl = binJepItemset.cl;
	supp = 1; 
	conf = 0;
}

BinaryPointersItemset::~BinaryPointersItemset(void)
{
}

int BinaryPointersItemset::getItem(int attributeNum) {
	return (attributeNum < data.GetCount()) 
		? (attributeNum >= 0) ? data.GetAt(attributeNum) : -1 : -1;
}

bool BinaryPointersItemset::hasItem(int attributeId) {
	for (int i = 0; i < data.GetCount(); i++)
		if (data.GetAt(i) == attributeId) return true;
	return false;
}

void BinaryPointersItemset::setItem(int attributeId, bool clear) {
	for (int i = 0; i < data.GetCount(); i++) {
		if (data.GetAt(i) == attributeId) {
			if (clear) {
				data.RemoveAt(i);
			}
			return;
		}
	}
	data.Add(attributeId);
}

int BinaryPointersItemset::getNext(int attributeId) {
	for (int i = 0; i < data.GetCount()-1; i++)
		if (data.GetAt(i) == attributeId) {
			return data.GetAt(i+1);
		}
	return -1;
}

int BinaryPointersItemset::getPrevious(int attributeId) {
	for (int i = (int)data.GetCount()-1; i > 0; i--)
		if (data.GetAt(i) == attributeId) {
			return data.GetAt(i-1);
		}
	return -1;
}

void BinaryPointersItemset::operator += (BinaryPointersItemset &other) {
	BinaryPointersItemset *ptr = (BinaryPointersItemset*)&other;
	for (int i = 0; i < ptr->data.GetCount(); i++) {
		setItem(ptr->getItem(i));
	}
}

void BinaryPointersItemset::operator -= (BinaryPointersItemset &other) {
	BinaryPointersItemset *ptr = (BinaryPointersItemset*)&other;
	for (int i = 0; i < ptr->data.GetCount(); i++) {
		setItem(ptr->getItem(i), true);
	}
}

bool BinaryPointersItemset::subsetOrEqualTo(BinaryPointersItemset &other) {
	for (int i = 0; i < data.GetCount(); i++) {
		if (!other.hasItem(getItem(i))) return false;
	}
	return true;
}


Dataset *BinaryPointersItemset::getSubsets(int startFrom) {
	Dataset *res = new Dataset();
	BinaryPointersItemset *bpi = new BinaryPointersItemset();
	bpi->setItem(getItem(startFrom));
	if (startFrom < data.GetCount() - 1) {
		Dataset *cab = getSubsets(startFrom+1);
		Dataset *cab2 = new Dataset();
		for (int j = 0; j < cab->GetCount(); j++) {
			BinaryPointersItemset * copyIt = (BinaryPointersItemset*) cab->GetAt(j);
			BinaryPointersItemset * copy = new BinaryPointersItemset(*copyIt);
			*copy += *bpi;
			cab2->Add(copy);
		}
		res->Append(*cab2);
		res->Append(*cab);
		delete cab;
		delete cab2;
	}
	res->Add(bpi);
	return res;
}

void BinaryPointersItemset::sortAndPruneAs(BinaryPointersItemset * order) {
	CUIntArray newData;
	for (int i = 0; i < order->getCount(); i++) {
		int item = order->getItem(i);
		if (hasItem(item))
			newData.Add(item);
	}
	data.RemoveAll();
	data.Append(newData);
}

void BinaryPointersItemset::debugPrint() {
	CString s, r;
	for (int i = 0; i < getCount(); i++) {
		s.Format("%d ", getItem(i));
		r.Append(s);
	}
	s.Format("%s Cl:%d Sp:%d Cf:%d", r, cl, supp, conf);
	print(s);
}
