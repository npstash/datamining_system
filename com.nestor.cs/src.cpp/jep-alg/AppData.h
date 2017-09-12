// AppData.h: interface for the AppData class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_APPDATA_H__4BEB9F11_1FC1_4169_946B_533460965E63__INCLUDED_)
#define AFX_APPDATA_H__4BEB9F11_1FC1_4169_946B_533460965E63__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Dataset.h"
#include "JEP.h"

class AppData  
{
public:
	AppData();
	virtual ~AppData();

	Dataset train_data;
	Dataset test_data;

	int class_n;
	int itemset_len;

	JEP *jep;
	bool datal;
};

#endif // !defined(AFX_APPDATA_H__4BEB9F11_1FC1_4169_946B_533460965E63__INCLUDED_)
