// FPTreeDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CMARApp.h"
#include "FPTreeDlg.h"


// CFPTreeDlg dialog

BOOL CFPTreeDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	cmardlg->buildFPCTree(&m_tree, cmardlg->fptree, TVI_ROOT);

	//UpdateData();
	return true;
}

IMPLEMENT_DYNAMIC(CFPTreeDlg, CDialog)
CFPTreeDlg::CFPTreeDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CFPTreeDlg::IDD, pParent)
{
}

CFPTreeDlg::~CFPTreeDlg()
{
}

void CFPTreeDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_TREE1, m_tree);
}


BEGIN_MESSAGE_MAP(CFPTreeDlg, CDialog)
END_MESSAGE_MAP()


// CFPTreeDlg message handlers
