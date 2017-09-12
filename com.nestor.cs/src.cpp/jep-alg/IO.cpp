// IO.cpp: implementation of the IO class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "IO.h"
#include "Dataset.h"
#include "Itemset.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

IO::IO()
{

}

IO::~IO()
{

}

Dataset * IO::loadDataset(char *fname, int * classes, int * itemset_len)
{
	FILE * file = fopen(fname, "r");

	if (!file) return NULL;

	int size;

	Dataset * d = new Dataset();

	fscanf(file, "%d\n", classes);
	fscanf(file, "%d\n", itemset_len);

	fscanf(file, "%d\n", &size);
	for (int i = 0; i < size; i++)
	{
		Itemset * newone = new Itemset(file);
		d->Add(newone);
	}
	fclose(file);

	return d;
}

void IO::writeDataset(char *fname, Dataset *d, int classes, int itemset_len)
{
	FILE * file = fopen(fname, "w");

	fprintf(file, "%d\n", classes);
	fprintf(file, "%d\n", itemset_len);

	fprintf(file, "%d\n", d->GetSize());
	for (int i = 0; i < d->GetSize(); i++)
		((Itemset*)(d->GetAt(i)))->write(file);

	fclose(file);
}
