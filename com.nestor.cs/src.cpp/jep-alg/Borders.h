// Borders.h: interface for the Borders class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_BORDERS_H__C2FEE550_3A3A_4C54_B366_711055E6C62B__INCLUDED_)
#define AFX_BORDERS_H__C2FEE550_3A3A_4C54_B366_711055E6C62B__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Dataset.h"

class Borders : public CObject  
{
public:
	Borders();
	virtual ~Borders();

	Dataset lBorder;
	Dataset rBorder;
};

#endif // !defined(AFX_BORDERS_H__C2FEE550_3A3A_4C54_B366_711055E6C62B__INCLUDED_)
