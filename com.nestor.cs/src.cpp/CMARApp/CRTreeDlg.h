#pragma once
#include "afxcmn.h"


// CCRTreeDlg dialog

class CCRTreeDlg : public CDialog
{
	DECLARE_DYNAMIC(CCRTreeDlg)

public:
	CCRTreeDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CCRTreeDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG3 };

protected:
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CTreeCtrl m_tree;
};
