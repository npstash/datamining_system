// ConvexSpace.cpp: implementation of the ConvexSpace class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "ConvexSpace.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

ConvexSpace::~ConvexSpace()
{
	RemoveAll();
}

/*void ConvexSpace::deleteAll()
{
	for (POSITION pos = GetHeadPosition(); pos != NULL; )
		delete GetNext(pos);
	RemoveAll();
}*/
