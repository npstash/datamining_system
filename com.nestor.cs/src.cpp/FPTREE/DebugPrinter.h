#pragma once

//#include "stdafx.h"

class DebugPrinter
{
public:
	DebugPrinter(void);
	~DebugPrinter(void);

protected:
	virtual void print(CString s) {
		printf("%s\n", s);
	}

	virtual void print(char* s) {
		printf("%s\n", s);
	}

public:
	virtual void debugPrint() {
		print("No information available");
	}
};
