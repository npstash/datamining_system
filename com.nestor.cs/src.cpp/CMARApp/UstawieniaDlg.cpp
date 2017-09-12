// UstawieniaDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CMARApp.h"
#include "UstawieniaDlg.h"
#include ".\ustawieniadlg.h"


// CUstawieniaDlg dialog

IMPLEMENT_DYNCREATE(CUstawieniaDlg, CDHtmlDialog)

CUstawieniaDlg::CUstawieniaDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CUstawieniaDlg::IDD, pParent)
	, ust_minSupp(2)
	, ust_confTH(51)
	, ust_covTH(4)
	, ust_chi2TH(3.84)
	, ust_suppTH(4)
{
}

CUstawieniaDlg::~CUstawieniaDlg()
{
}

void CUstawieniaDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Text(pDX, IDC_EDIT1, ust_suppTH);
	DDV_MinMaxFloat(pDX, ust_suppTH, 0, 100);
	DDX_Text(pDX, IDC_EDIT2, ust_minSupp);
	DDV_MinMaxInt(pDX, ust_minSupp, 1, 10000);
	DDX_Text(pDX, IDC_EDIT3, ust_confTH);
	DDV_MinMaxFloat(pDX, ust_confTH, 50, 100);
	DDX_Text(pDX, IDC_EDIT4, ust_covTH);
	DDV_MinMaxInt(pDX, ust_covTH, 1, 10000);
	DDX_Text(pDX, IDC_EDIT5, ust_chi2TH);
	DDV_MinMaxFloat(pDX, ust_chi2TH, 1, 10000);
}

BOOL CUstawieniaDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	return TRUE;  // return TRUE  unless you set the focus to a control
}

BEGIN_MESSAGE_MAP(CUstawieniaDlg, CDialog)
	ON_BN_CLICKED(IDOK, OnButtonOK)
	ON_BN_CLICKED(IDCANCEL, OnButtonCancel)
END_MESSAGE_MAP()

void CUstawieniaDlg::OnButtonOK()
{
	if (UpdateData(true)) {
		OnOK();
	}
}

void CUstawieniaDlg::OnButtonCancel()
{
	OnCancel();
}
