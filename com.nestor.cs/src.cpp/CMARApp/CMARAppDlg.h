// CMARAppDlg.h : header file
//

#pragma once
#include "afxwin.h"

#include "CMAR.h"
#include "CMARFPTree.h"
#include "CMARFPTreeLeaf.h"

//#include "BinaryPointersItemset.h"

#include "CRTree.h"
#include "CRTreeNode.h"

#include "IO.h"
#include "Dataset.h"
#include "Itemset.h"

// CCMARAppDlg dialog
class CCMARAppDlg : public CDialog
{
// Construction
public:
	CCMARAppDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_CMARAPP_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnBnClickedButton2();
	afx_msg void OnBnClickedButton1();
	afx_msg void OnBnClickedButton3();

	CButton btn_genFptree;
	CButton btnShowFP;
	CButton btnCRtree;
	CButton btnShowCR;
	CButton bthClassif;

	afx_msg void OnBnClickedButton4();
	void buildFPCTree(CTreeCtrl* tree, CMARFPTreeLeaf* node, HTREEITEM parent);

	void buildCRCTree(CTreeCtrl* tree, CRTreeNode* node, HTREEITEM parent);

	float a_suppTH;
	float a_confTH;
	int a_covTH;
	float a_chi2TH;

	BinaryPointersDataset * datPnt;
	CMARFPTree * fptree;
	CRTree * crtree;

	BinaryPointersItemset* FPList;

	int classes;
	int binItems;

	afx_msg void OnBnClickedButton6();
	afx_msg void OnBnClickedButton7();
	afx_msg void OnBnClickedButton8();
	afx_msg void OnBnClickedButton5();
	CButton btnDane;
};

extern CCMARAppDlg * cmardlg;
