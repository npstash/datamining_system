// Borders.cpp: implementation of the Borders class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "Borders.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

Borders::Borders()
{
}

Borders::~Borders()
{
	lBorder.RemoveAll();
	rBorder.RemoveAll();
}
