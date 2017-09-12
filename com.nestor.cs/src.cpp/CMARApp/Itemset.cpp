// Itemset.cpp: implementation of the Itemset class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "Itemset.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

Itemset::Itemset(int _itemset_len)
{
	itemset_len = _itemset_len;
	data = new unsigned long [getBlocks()];
	for (int i = 0; i < getBlocks(); i++)
		data[i] = 0;
	cl = 0;
	supp = 0;
	count = 0;
	text = NULL;
}

Itemset::Itemset(Itemset& item)
{
	itemset_len = item.itemset_len;
	data = new unsigned long [getBlocks()];
	for (int i = 0; i < getBlocks(); i++)
		data[i] = item.data[i];
	cl = item.cl;
	supp = item.supp;
	count = item.count;
	text = NULL;
}

Itemset::Itemset(FILE* file)
{
	text = NULL;
	read(file);
}

Itemset::~Itemset()
{
	delete data;
	if (text) delete text;
}

char* Itemset::getText()
{
	if (!text)
		createText();
	return text;
}

void Itemset::deleteText()
{
	if (text) delete text;
}

void Itemset::createText()
{
	deleteText();
	
	if (itemset_len < 26) text = maketext();
	else {
		text = new char [itemset_len + 1];
		binarize(text);
	}

}

void Itemset::createInfo()
{
	deleteText();
	char * temp;
	if (itemset_len < 26) temp = maketext();
	else {
		temp = new char [itemset_len + 1];
		binarize(temp);
	}
	text = new char[itemset_len + 1 + 20];
	sprintf(text, "Class:%d Data:%s", cl, temp);
	delete temp;
}

void Itemset::createExtInfo()
{
	deleteText();
	char * temp;
	if (itemset_len < 26) temp = maketext();
	else {
		temp = new char [itemset_len + 1];
		binarize(temp);
	}
	text = new char[itemset_len + 1 + 35];
	sprintf(text, "Class:%d Data:%s Count:%ld (%d%%)", cl, temp, count, (int)(supp*100));
	delete temp;
}

char* Itemset::getInfo()
{
	if (!text)
		createInfo();
	return text;
}

char* Itemset::getExtInfo()
{
	if (!text)
		createExtInfo();
	return text;
}

void Itemset::write(FILE *file)
{
	char *binary = new char [itemset_len * 32];
	for (int i = 0; i < itemset_len; i++)
		binary[i] = (getItem(i))? '1': '0';
	binary[i] = 0;
	fprintf(file, "%d %d %ld %f %s\n", cl, itemset_len, count, supp, binary);
	delete binary;
}

void Itemset::read(FILE *file)
{
	char binary[0x200];
	fscanf(file, "%d%d%ld%f%s\n", &cl, &itemset_len, &count, &supp, binary);
	data = new unsigned long [getBlocks()];
	clear();
	for (int b = 0; b < getBlocks(); b++)
		data[b] = 0;
	for (int i = 0; i < itemset_len; i++)
		setItem(i, (binary[i] == '1')? 1: 0);
	deleteText();
}

int Itemset::getBlocks()
{
	return (itemset_len >> 5) + ((itemset_len & 31)? 1: 0);
}

void Itemset::clear()
{
	for (int b = 0; b < getBlocks(); b++)
		data[b] = 0;
}

void Itemset::binarize(char *dest)
{
	for (int b = 0; b < itemset_len; b++)
		dest[b] = (getItem(b))? '1' : '0';
	dest[b] = 0;
}


char* Itemset::maketext()
{
	int bits = 0, bit = 0;
	for (int b = 0; b < itemset_len; b++)
		if (getItem(b)) bits++;

	char * temp = new char[bits + 1];
	for (int i = 0; i < itemset_len; i++)
		if (getItem(i))
			temp[bit++] = 'A' + i;
	temp[bit] = 0;
	return temp;
}
