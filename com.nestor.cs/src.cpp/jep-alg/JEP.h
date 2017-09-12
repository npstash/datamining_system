// JEP.h: interface for the JEP class.
//
//////////////////////////////////////////////////////////////////////

#include "ProgressStep.h"
#include "ConvexSpace.h"
#include "Dataset.h"
#include "Itemset.h"
#include "Borders.h"
#include "DiDuBorders.h"
#include "CObArrayExt.h"

#if !defined(AFX_JEP_H__921FAB1A_4B19_47BB_A081_5D3D1EA2D3D0__INCLUDED_)
#define AFX_JEP_H__921FAB1A_4B19_47BB_A081_5D3D1EA2D3D0__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class JEP
{
public:
	static void getNthFold(Dataset* d, int folds, int foldNum, Dataset** train, Dataset** test);
	void calculateAccurancyOnTestData(Dataset * test, float * mistakes, float * unclassified, ProgressStep * prog);
	void calcuteNcrossFordValidationAccurancy(int folds, float * mistakes, float * unclassified, ProgressStep * prog);
	void cleanJEPSpace();
	void cleanBorders();
	void initBorders();
	void initJEPSpace();
	int class_n;

	Dataset** getMEJEPs(Itemset* item);
	Dataset* getMEJEPs2(Itemset* item) ;
	float* voteForItemset(Itemset* item, int ** nv, FILE* writeto);
	int classifyItemset(Itemset *item, FILE* writeto);
	void classifyDataset(Dataset *clasf, FILE* writeto);
	int classifyVotes(float* votes);
	bool b_horiz;
	bool b_jeps;
	void calculateJEPsupport();
	static Dataset* getMinItemsets(Dataset *d);
	static Dataset* getDiffItemsets(Dataset* d);
	static Dataset* cartesian(Dataset* D1, Dataset* D2);
	static Dataset* allSubItemsOf(Itemset*);
	static Borders* BorderDiff(Itemset* U, Dataset* S);
	int findJEPs(ProgressStep *prog);
	static Dataset* getMaxItemsets(Dataset *d);
	
	// zawiera przestrzenie JEP dla kazdej z klas
	// lista obiektow ConvexSpace. Obiekt ConvexSpace jest lista i zawiera obiekty Borders
	CObArrayExt JEPSpace;

	// granice horyzontalne dla kazdej z klas

	int generateHorizBorders(ProgressStep * prog);

	// tu sa granice poszczegolnych klas wraz z granicami danych nie nalezachych do tej klasy
	// lista obiektow DiDuBorders. kazdy z nich zawiera dwa obiekty Borders
	CObArrayExt horizBorders;

	Dataset* train_d;
	JEP(int, Dataset*);
	virtual ~JEP();

};

#endif // !defined(AFX_JEP_H__921FAB1A_4B19_47BB_A081_5D3D1EA2D3D0__INCLUDED_)
