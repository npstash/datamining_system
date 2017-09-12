// CMARAppDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CMARApp.h"
#include "CMARAppDlg.h"
#include "UstawieniaDlg.h"
#include "FPTreeDlg.h"
#include "CRTreeDlg.h"
#include "DaneDlg.h"
#include "fDlg.h"

#include "cmarappdlg.h"
#include ".\cmarappdlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


CCMARAppDlg * cmardlg;

// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
END_MESSAGE_MAP()


// CCMARAppDlg dialog



CCMARAppDlg::CCMARAppDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CCMARAppDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CCMARAppDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_BUTTON3, btn_genFptree);
	DDX_Control(pDX, IDC_BUTTON4, btnShowFP);
	DDX_Control(pDX, IDC_BUTTON6, btnCRtree);
	DDX_Control(pDX, IDC_BUTTON7, btnShowCR);
	DDX_Control(pDX, IDC_BUTTON8, bthClassif);
	DDX_Control(pDX, IDC_BUTTON5, btnDane);
}

BEGIN_MESSAGE_MAP(CCMARAppDlg, CDialog)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
	ON_BN_CLICKED(IDC_BUTTON2, OnBnClickedButton2)
	ON_BN_CLICKED(IDC_BUTTON1, OnBnClickedButton1)
	ON_BN_CLICKED(IDC_BUTTON3, OnBnClickedButton3)
	ON_BN_CLICKED(IDC_BUTTON4, OnBnClickedButton4)
	ON_BN_CLICKED(IDC_BUTTON6, OnBnClickedButton6)
	ON_BN_CLICKED(IDC_BUTTON7, OnBnClickedButton7)
	ON_BN_CLICKED(IDC_BUTTON8, OnBnClickedButton8)
	ON_BN_CLICKED(IDC_BUTTON5, OnBnClickedButton5)
END_MESSAGE_MAP()


// CCMARAppDlg message handlers

BOOL CCMARAppDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	a_suppTH = 4;
	a_confTH = 51;
	a_covTH = 4;
	a_chi2TH = 3.84;

	datPnt = 0;
	fptree = 0;
	crtree = 0;
	FPList = 0;
		
	cmardlg = this;

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CCMARAppDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CCMARAppDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CCMARAppDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

void CCMARAppDlg::OnBnClickedButton2()
{
		CUstawieniaDlg dlg1;
		if (dlg1.DoModal() == IDOK) {
			a_suppTH = dlg1.ust_suppTH;
			a_confTH = dlg1.ust_confTH;
			a_covTH = dlg1.ust_covTH;
			absMinSupp = dlg1.ust_minSupp;
			a_chi2TH = dlg1.ust_chi2TH;
		}
}

void CCMARAppDlg::OnBnClickedButton1()
{
    static char *ext = "Dataset file (*.dataset)|*.dataset||";
	CFileDialog dlg(true, "dataset", NULL, OFN_FILEMUSTEXIST, ext);

	if (dlg.DoModal() == IDOK) {

		Dataset * datBin = IO::loadDataset(dlg.GetFileName().GetBuffer(), &classes, &binItems);
		classes++;

		if (datPnt) delete datPnt;
		datPnt = new BinaryPointersDataset(binItems);
		for (int i = 0; i < datBin->GetCount(); i++) {
			Itemset * binItemset = (Itemset*) datBin->GetAt(i);
			datPnt->Add(new BinaryPointersItemset(*binItemset));
		}

		datBin->deleteAll();
		delete datBin;

		btnDane.EnableWindow();
		btn_genFptree.EnableWindow();
		btnShowFP.EnableWindow(false);
		btnCRtree.EnableWindow(false);
		btnShowCR.EnableWindow(false);
		bthClassif.EnableWindow(false);		

		if (fptree) delete fptree;
		if (crtree) delete crtree;
		if (FPList) delete FPList;
	}

}

void CCMARAppDlg::OnBnClickedButton3()
{
	int suppTH = int((float)datPnt->GetCount() * a_suppTH / 100);
	suppTH = (suppTH < absMinSupp) ? absMinSupp : suppTH;
	fptree = new CMARFPTree(datPnt, suppTH, classes);

	btnShowFP.EnableWindow(true);
	btnCRtree.EnableWindow(true);
}



void CCMARAppDlg::OnBnClickedButton4()
{
	CFPTreeDlg dlg;
	dlg.DoModal();
}

void CCMARAppDlg::buildFPCTree(CTreeCtrl* tree, CMARFPTreeLeaf* node, HTREEITEM parent)
{
	CString s;
	if (node->value != -1) {
		s.Format("%d, Liczba:%d", node->value, node->count);
		
		if (node->patterns) {
			s.Append(", Klasy: ");
			for (int j = 0; j < node->classCount; j++) {
				if (node->classDistibution[j]) {
					CString tmp;
					tmp.Format("%d (Liczba:%d),", j, node->classDistibution[j]);
					s.Append(tmp);
				}
			}
		}
	} else {
		s.Format("ROOT");
	}

	HTREEITEM par = tree->InsertItem(s.GetBuffer(), parent);

	for (int i = 0; i < node->subNodes.GetCount(); i++) {
		CMARFPTreeLeaf * sub = (CMARFPTreeLeaf*) node->subNodes.GetAt(i);
		buildFPCTree(tree, sub, par);
	}
	tree->Expand(par, TVE_EXPAND);
}

void CCMARAppDlg::buildCRCTree(CTreeCtrl* tree, CRTreeNode* node, HTREEITEM parent)
{
	CString s;
	if (node->value != -1) {
		s.Format("%d", node->value);

		if (node->cl > -1) {
			CString t;
			t.Format("  REGULA: Wsparcie:%d  Zaufanie:%d", node->cl, node->supp, node->conf);
			s.Append(t);
		}
	} else {
		s.Format("ROOT");
	}

	HTREEITEM par = tree->InsertItem(s.GetBuffer(), parent);

	for (int i = 0; i < node->childNodes.GetCount(); i++) {
		CRTreeNode * sub = (CRTreeNode*) node->childNodes.GetAt(i);
		buildCRCTree(tree, sub, par);
	}
	tree->Expand(par, TVE_EXPAND);
}

void CCMARAppDlg::OnBnClickedButton6()
{
	CRTree *unpruned = new CRTree(fptree->classLablelsSupports, fptree->classCount, fptree->datasetSize, fptree->binAttrCnt);
	unpruned->currentChi2TH = a_chi2TH;
	unpruned->suppDiffTH = 0;
	unpruned->confDiffTH = 0;
	fptree->FillCRTree(a_confTH, unpruned);

	CRTree* res;
	res = fptree->pruneBasedOnCoverage(unpruned, datPnt, a_covTH);
	res->currentChi2TH = a_chi2TH;
	res->suppDiffTH = 0;
	res->confDiffTH = 0;
	delete unpruned;

	res->sortHeaderTable(fptree->FPList);

	FPList = new BinaryPointersItemset(*fptree->FPList);

	crtree = res;

	btnShowCR.EnableWindow();
	bthClassif.EnableWindow();
}



void CCMARAppDlg::OnBnClickedButton7()
{
	CCRTreeDlg dlg;
	dlg.DoModal();
}

void CCMARAppDlg::OnBnClickedButton8()
{
	CfDlg dlg;
	dlg.m_binAts = datPnt->binaryAttributes-1;
	dlg.DoModal();
}

void CCMARAppDlg::OnBnClickedButton5()
{
	DaneDlg dlg;
	dlg.DoModal();
}


