// IO.h: interface for the IO class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IO_H__0EEF67A8_8397_412F_897D_6E594015AFEA__INCLUDED_)
#define AFX_IO_H__0EEF67A8_8397_412F_897D_6E594015AFEA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Dataset.h"

class IO  
{
public:
	static void writeDataset(char *fname, Dataset * d, int classes, int itemset_len);
	static Dataset * loadDataset(char * fname, int * classes, int * itemset_len);
	IO();
	virtual ~IO();

};

#endif // !defined(AFX_IO_H__0EEF67A8_8397_412F_897D_6E594015AFEA__INCLUDED_)
