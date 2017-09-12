// fDlg.cpp : implementation file
//

#include "stdafx.h"
#include "fDlg.h"
#include ".\fdlg.h"

// CfDlg dialog

BOOL CfDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	m_fp.Format("%s", getItText(cmardlg->FPList));

	UpdateData(false);

	return true;
}

IMPLEMENT_DYNAMIC(CfDlg, CDialog)
CfDlg::CfDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CfDlg::IDD, pParent)
	, m_itemset(_T(""))
	, m_binAts(0)
	, m_fak(_T(""))
	, m_klasa(_T(""))
	, m_fp(_T(""))
{
}

CfDlg::~CfDlg()
{
}

void CfDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_TREE4, m_tree);
	DDX_Text(pDX, IDC_EDIT1, m_itemset);
	DDX_Text(pDX, IDC_MAX, m_binAts);
	DDX_Text(pDX, IDC_fak, m_fak);
	DDX_Text(pDX, IDC_C, m_klasa);
	DDX_Text(pDX, IDC_FPLIST, m_fp);
}


BEGIN_MESSAGE_MAP(CfDlg, CDialog)
	ON_BN_CLICKED(IDC_BUTTON1, OnBnClickedButton1)
	ON_BN_CLICKED(IDOK, OnBnClickedOk)
END_MESSAGE_MAP()


// CfDlg message handlers

void CfDlg::OnBnClickedButton1()
{
	UpdateData();

	CString str( m_itemset.GetBuffer() );
	CString resToken;
	int curPos= 0;

	BinaryPointersItemset* toc = new BinaryPointersItemset();

	resToken = str.Tokenize(", ",curPos);
	while (resToken != "")
	{
		if (!strcmp(resToken.GetBuffer(), ",")) continue;
		if (!strcmp(resToken.GetBuffer(), " ")) continue;
		if (!strcmp(resToken.GetBuffer(), " ,")) continue;
		if (!strcmp(resToken.GetBuffer(), ", ")) continue;

		int it = atoi(resToken);

		toc->setItem(it);
				
		resToken= str.Tokenize("% #",curPos);
	};
		

	BinaryPointersItemset* toc2 = new BinaryPointersItemset(*toc);
	toc2->sortAndPruneAs(cmardlg->FPList);
	m_fak.Format("%s", getItText(toc2).GetBuffer());
	UpdateData(false);

	BinaryPointersDataset ** groups = cmardlg->crtree->getRuleGroupsForItemset(toc2);


	// obliczanie chi2 wazonego dla wszystkich grup
	double * powers = new double [cmardlg->crtree->classCount];
	for (int i = 0; i < cmardlg->crtree->classCount; i++) {
		powers[i] = 0;
	}

	for (int gr = 0; gr < cmardlg->crtree->classCount; gr++) {
		for (int rl = 0; rl < groups[gr]->GetCount(); rl++) {
			BinaryPointersItemset * theRule = (BinaryPointersItemset*) groups[gr]->GetAt(rl);

			float chi2 = CRTree::computeChi2(theRule->supp, theRule->conf, 
				cmardlg->crtree->datasetSize, cmardlg->crtree->classLablelsSupports[gr]);
			float maxchi2 = CRTree::computeMaxChi2(theRule->supp, 
				cmardlg->crtree->classLablelsSupports[gr], 
				cmardlg->crtree->datasetSize);
			float power = chi2*chi2/maxchi2;

			powers[gr] += power;
		}

		for (int rl = 0; rl < groups[gr]->GetCount(); rl++) {
			//m_tree
		}
	}


	// wartosci maxChi2 obliczone. wybieramy klase dla ktorej ta suma jest najwieksza
	// w przypadku niejednoznacznosci zwracamy wartosc -1

	int maxPower = 0;
	int selectedClass = -1;
	int classesAtThisPow = 0;

	for (int gr = 0; gr < cmardlg->crtree->classCount; gr++) {
		if (powers[gr] == maxPower) {
			classesAtThisPow++;
		}

		if (powers[gr] > maxPower) {
			maxPower = powers[gr];
			selectedClass = gr;
			classesAtThisPow = 1;
		}
	}

	if (classesAtThisPow > 1) {
		selectedClass = -1;
	}

	m_klasa.Format("%d", selectedClass);

	for (int i = 0; i < cmardlg->crtree->classCount; i++) {
		groups[i]->deleteAll();
		delete groups[i];
	}

	delete powers;
	delete groups;
	delete toc2;

}

CString CfDlg::getItText(BinaryPointersItemset * it)
{
	CString s("");
	for (int j = 0; j < ((BinaryPointersItemset*)it)->getCount(); j++) {
		CString t;
		t.Format("%d, ", ((BinaryPointersItemset*)it)->getItem(j));
		s.Append(t);
	}
	return s;
}

void CfDlg::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here
	OnOK();
}
