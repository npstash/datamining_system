// CRTreeDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CMARApp.h"
#include "CRTreeDlg.h"

#include "CMARAppDlg.h"


// CCRTreeDlg dialog
BOOL CCRTreeDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	cmardlg->buildCRCTree(&m_tree, cmardlg->crtree, TVI_ROOT);

	return true;
}

IMPLEMENT_DYNAMIC(CCRTreeDlg, CDialog)
CCRTreeDlg::CCRTreeDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CCRTreeDlg::IDD, pParent)
{
}

CCRTreeDlg::~CCRTreeDlg()
{
}

void CCRTreeDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_TREE2, m_tree);
}


BEGIN_MESSAGE_MAP(CCRTreeDlg, CDialog)
END_MESSAGE_MAP()


// CCRTreeDlg message handlers
