#pragma once
#include "afxcmn.h"

#include "Cmarappdlg.h"

// CFPTreeDlg dialog

class CFPTreeDlg : public CDialog
{
	DECLARE_DYNAMIC(CFPTreeDlg)

public:
	CFPTreeDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CFPTreeDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG2 };

protected:
	virtual BOOL OnInitDialog();

	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CTreeCtrl m_tree;
};
