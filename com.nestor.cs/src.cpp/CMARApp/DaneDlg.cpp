// DaneDlg.cpp : implementation file
//

#include "stdafx.h"
#include "CMARApp.h"
#include "DaneDlg.h"

#include "cmarappdlg.h"

// DaneDlg dialog

BOOL DaneDlg::OnInitDialog()
{
	CDialog::OnInitDialog();


	for (int i = 0; i < cmardlg->datPnt->GetCount(); i++) {
		BinaryPointersItemset* bin = (BinaryPointersItemset*) cmardlg->datPnt->GetAt(i);
		
		CString s;


		for (int j = 0; j < bin->getCount(); j++) {
			CString t;
			t.Format("%d, ", bin->getItem(j));
			s.Append(t);
		}

		m_tree.InsertItem(s.GetBuffer());
	}

	return true;
}

IMPLEMENT_DYNAMIC(DaneDlg, CDialog)
DaneDlg::DaneDlg(CWnd* pParent /*=NULL*/)
	: CDialog(DaneDlg::IDD, pParent)
{
}

DaneDlg::~DaneDlg()
{
}

void DaneDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_TREE3, m_tree);
}


BEGIN_MESSAGE_MAP(DaneDlg, CDialog)
END_MESSAGE_MAP()


// DaneDlg message handlers
