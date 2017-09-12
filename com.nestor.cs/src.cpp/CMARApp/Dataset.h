// Dataset.h: interface for the Dataset class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_DATASET_H__0164830C_BB25_4F91_82A2_0393CC1B4089__INCLUDED_)
#define AFX_DATASET_H__0164830C_BB25_4F91_82A2_0393CC1B4089__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class Dataset : public CObArray
{
public:
	void deleteAll();
	Dataset();
	virtual ~Dataset();
	virtual void deepCopyItemsetFrom(Dataset * from);
};

#endif // !defined(AFX_DATASET_H__0164830C_BB25_4F91_82A2_0393CC1B4089__INCLUDED_)
