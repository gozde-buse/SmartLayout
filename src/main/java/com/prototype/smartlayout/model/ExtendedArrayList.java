package com.prototype.smartlayout.model;

import java.util.ArrayList;

public class ExtendedArrayList<T>
{
	public long length = 0;

    private final Class<T> classType;
	private final ArrayList<ArrayList<T>> arrayList;
	
	private int lastIndex_inner = 0;
	private int lastIndex_outer = 0;

	public ExtendedArrayList(Class <T> t)
	{
		classType = t;
		arrayList = new ArrayList<ArrayList<T>>();
	}
	
	public void add(T element)
	{
		if(arrayList.size() <= lastIndex_outer)
			arrayList.add(new ArrayList<T>());
		
		arrayList.get(lastIndex_outer).add(element);
		length++;
		
		if(lastIndex_inner == Integer.MAX_VALUE)
		{
			lastIndex_inner = 0;
			lastIndex_outer++;
			
			arrayList.add(new ArrayList<T>());
		}
		else
			lastIndex_inner++;
	}
	
	public void addAll(ExtendedArrayList<T> elements)
	{
		for(long i = 0; i < elements.length; i++)
		{
			add(elements.get(i));
		}
	}
	
	public T get(long index)
	{
		int row, col;
		
		if(index < Integer.MAX_VALUE)
		{
			row = 0;
			col = (int) index;
		}
		else
		{
			row = (int) Math.floor(index / Integer.MAX_VALUE);
			col = (int) index % Integer.MAX_VALUE;
			
			if(col != 0)
				row++;
			
			row--;
			
			if(col == 0)
				col = Integer.MAX_VALUE;
			else
				col--;
		}
		
		return arrayList.get(row).get(col);
	}
	
	public void clear()
	{
		arrayList.clear();
		length = 0;
		lastIndex_inner = 0;
		lastIndex_outer = 0;
	}
	
	@SuppressWarnings("unchecked")
	public ExtendedArrayList<T> Merge( ExtendedArrayList<T>... arrayLists )
	{
		Class <T> classType = this.classType;
		long length = 0;
		
		for(int i = 0; i < arrayLists.length; i++)
		{
			length += arrayLists[i].length;
			
			if(classType != arrayLists[i].classType)
				return null;
		}
		
		ExtendedArrayList<T> mergedArrayList = new ExtendedArrayList<T>(classType);
		
		long elementIndex = 0;
		int arrayIndex = 0;
		
		for(long i = 0; i < length; i++)
		{
			mergedArrayList.add(arrayLists[arrayIndex].get(elementIndex));
			
			elementIndex++;
			
			if(elementIndex >= arrayLists[arrayIndex].length)
			{
				arrayIndex++;
				elementIndex = 0;
			}
		}
		
		return mergedArrayList;
	}
}
