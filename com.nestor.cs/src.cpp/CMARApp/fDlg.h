#pragma once
#include "afxcmn.h"

#include "resource.h"
// CfDlg dialog
#include "CMARappdlg.h"


class CfDlg : public CDialog
{
	DECLARE_DYNAMIC(CfDlg)

public:
	CfDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CfDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG5 };

protected:
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CTreeCtrl m_tree;
	CString m_itemset;
	int m_binAts;
	afx_msg void OnBnClickedButton1();
	CString m_fak;
	CString m_klasa;
	CString getItText(BinaryPointersItemset * it);
	afx_msg void OnBnClickedOk();
	CString m_fp;
};
