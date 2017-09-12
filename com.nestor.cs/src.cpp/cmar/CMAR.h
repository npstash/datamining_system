#pragma once

#include "resource.h"

#include "..\FPTree\BinaryPointersDataset.h"

#include "CRTree.h"


extern bool debug;
extern bool crossdebug;
extern bool reducingInsertion;
extern bool covPruning;
extern int absMinSupp;
extern absMinSupp;


class CMAR
{
public:
	CMAR(int classCount, float supportTH = 5, float confidenceTH = 50, int coverageTH = 3, float chiSquareTH = 3.84f,
		float supportDifferenceTH = 0.01, float confidenceDifferenceTH = 0.01, float setConfDiff = 1);
	~CMAR(void);

private:
	CRTree* generateRules(BinaryPointersDataset* dataset, BinaryPointersItemset ** pFPList);

public:
	float m_supportTH; 
	float m_confidenceTH;
	int m_coverageTH; 
	float m_chiSquareTH;
	float m_supportDifferenceTH;
	float m_confidenceDifferenceTH;
	float m_setConfidenceDifferenceTH;

	CRTree *rules;
	BinaryPointersItemset* FPList;
	int classLabels;

	void learn(BinaryPointersDataset* dataset);

	int classifyItemset(BinaryPointersItemset* itemset);

	void processCrossTest(int folds, int * misclassified, int * unclassified, 
		double * accurancy, BinaryPointersDataset* train_d);

	void getNthFold(BinaryPointersDataset* d, int folds, int foldNum, 
		BinaryPointersDataset* train, BinaryPointersDataset* test);
};
