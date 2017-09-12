// ProgressStep.h: interface for the ProgressStep class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_PROGRESSSTEP_H__6249D1D2_61EB_4C91_89F2_2B2E35CD758E__INCLUDED_)
#define AFX_PROGRESSSTEP_H__6249D1D2_61EB_4C91_89F2_2B2E35CD758E__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class ProgressStep  
{
public:
	virtual int StepIt();
	ProgressStep();
	virtual ~ProgressStep();

};

#endif // !defined(AFX_PROGRESSSTEP_H__6249D1D2_61EB_4C91_89F2_2B2E35CD758E__INCLUDED_)
