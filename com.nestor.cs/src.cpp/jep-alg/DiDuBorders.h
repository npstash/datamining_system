// DiDuBorders.h: interface for the DiDuBorders class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_DIDUBORDERS_H__BC558451_233D_40CB_BBB8_6C8171F2EEE5__INCLUDED_)
#define AFX_DIDUBORDERS_H__BC558451_233D_40CB_BBB8_6C8171F2EEE5__INCLUDED_

#include "Borders.h"

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class DiDuBorders : public CObject
{
public:
	DiDuBorders();
	virtual ~DiDuBorders();

	Borders bordersDi;
	Borders bordersDu;
};

#endif // !defined(AFX_DIDUBORDERS_H__BC558451_233D_40CB_BBB8_6C8171F2EEE5__INCLUDED_)
