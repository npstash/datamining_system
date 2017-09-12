// Itemset.h: interface for the Itemset class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ITEMSET_H__7A78C8AB_BA67_49FA_8AFD_6886119A145C__INCLUDED_)
#define AFX_ITEMSET_H__7A78C8AB_BA67_49FA_8AFD_6886119A145C__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class Itemset : public CObject  
{
private:
	int getBlocks();
	void createText();
	// data zawiera reprezentacje zero-jedynkowa tego elementu (abce = 11101)
	unsigned long *data;
	char* text;

public:
	char* maketext();
	void binarize(char *dest);
	void clear();
	long count;
	void read(FILE* file);
	void write(FILE* file);
	char* getInfo();
	void createInfo();
	char* getExtInfo();
	void createExtInfo();
	void deleteText();
	char* getText();

	// klasy sa numerowane od 1!
	int cl;
	float supp;
	int itemset_len;
	Itemset(int);
	Itemset(FILE*);
	Itemset(Itemset&);
	virtual ~Itemset();


	void operator &= (Itemset &other)
	{
		for (int i = 0; i < getBlocks(); i++)
			data[i] &= other.data[i];
	}

	void operator -= (Itemset &other)
	{
		for (int i = 0; i < getBlocks(); i++)
			data[i] &= ~other.data[i];
	}

	void operator += (Itemset &other)
	{
		for (int i = 0; i < getBlocks(); i++)
			data[i] |= other.data[i];
	}

	bool operator == (Itemset &other)
	{
		bool ok = true;
		for (int i = 0; i < getBlocks(); i++)
			if (data[i] != other.data[i]) { ok = false; break; }
		return ok;
	}

	bool hasItems()
	{
		bool ok = false;
		for (int i = 0; i < getBlocks(); i++)
			if (data[i] != 0)
				{ok = true; break;}
		return ok;
	}

	bool superSetOf(Itemset &other)
	{
		bool ok = true;
		for (int i = 0; i < getBlocks(); i++)
			if (! (((data[i] & other.data[i]) == other.data[i]) && (data[i] != other.data[i])) )
				{ok = false; break;}
		return ok;
	}

	bool superOrEqTo(Itemset &other)
	{
		bool ok = true;
		for (int i = 0; i < getBlocks(); i++)
			if (! ((data[i] & other.data[i]) == other.data[i]) )
				{ok = false; break; }
		return ok;
	}

	bool subSetOf(Itemset &other)
	{
		bool ok = true;
		for (int i = 0; i < getBlocks(); i++)
			if (! (((data[i] & other.data[i]) == data[i]) && (data[i] != other.data[i])) )
				{ok = false; break; }
		return ok;
	}

	bool subOrEqTo(Itemset &other)
	{
		bool ok = true;
		for (int i = 0; i < getBlocks(); i++)
			if (! ((data[i] & other.data[i]) == data[i]) )
				{ok = false; break; }
		return ok;
	}

	void setItem(int elem_n, int val)
	{
		// elem_n od zera!
		int block_n = elem_n >> 5; // /32
		elem_n = elem_n & 31; // %32
		if (val)
			data[block_n] = data[block_n] | (1 << elem_n);
		else 
			data[block_n] &= (unsigned long)(~(1 << elem_n));
		if (text) delete text; 
		text = NULL;
	}

	bool getItem(int elem_n)
	{
		// elem_n od zera!
		return ((data[elem_n >> 5] & (1 << (elem_n & 31))) != 0);
	}

};

#endif // !defined(AFX_ITEMSET_H__7A78C8AB_BA67_49FA_8AFD_6886119A145C__INCLUDED_)
