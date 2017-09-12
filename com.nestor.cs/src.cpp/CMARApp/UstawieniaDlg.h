#pragma once


// CUstawieniaDlg dialog

class CUstawieniaDlg : public CDialog
{
	DECLARE_DYNCREATE(CUstawieniaDlg)

public:
	CUstawieniaDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CUstawieniaDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG1, IDH = IDR_HTML_USTAWIENIADLG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	virtual BOOL OnInitDialog();

	DECLARE_MESSAGE_MAP()
public:
	int ust_minSupp;
	float ust_confTH;
	int ust_covTH;
	float ust_suppTH;
	float ust_chi2TH;

	afx_msg void OnButtonOK();
	afx_msg void OnButtonCancel();
};
