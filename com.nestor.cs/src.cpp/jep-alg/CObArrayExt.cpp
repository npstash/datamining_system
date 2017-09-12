// CObArrayExt.cpp: implementation of the CObArrayExt class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "CObArrayExt.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

void CObArrayExt::DeleteAll()
{
	for (int pos = 0; pos < GetSize(); pos++)
		delete GetAt(pos);
	RemoveAll();
}
