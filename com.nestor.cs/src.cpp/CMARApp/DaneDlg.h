#pragma once
#include "afxcmn.h"


// DaneDlg dialog

class DaneDlg : public CDialog
{
	DECLARE_DYNAMIC(DaneDlg)

public:
	DaneDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~DaneDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG4 };

protected:
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CTreeCtrl m_tree;
};
