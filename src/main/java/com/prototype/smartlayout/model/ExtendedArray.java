package com.prototype.smartlayout.model;

import java.lang.reflect.Array;

public class ExtendedArray<T>
{
	public long length;

    private final Class<T> classType;
	private final T[][] array;
	
	@SuppressWarnings("unchecked")
	public ExtendedArray(Class <T> t, long length)
	{
		classType = t;
		this.length = length;
		
		if(length <= Integer.MAX_VALUE)
			array = (T[][]) Array.newInstance(classType, 1, (int) length);
		else
		{
			int division = (int) Math.floor(length / Integer.MAX_VALUE);
			int remainder = (int) length % Integer.MAX_VALUE;
			
			if(remainder != 0)
				division++;
			
			array = (T[][]) Array.newInstance(classType, division, Integer.MAX_VALUE);
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
		
		return array[row][col];
	}
	
	public void set(T element, long index)
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
		
		array[row][col] = element;
	}
	
	@SuppressWarnings("unchecked")
	public ExtendedArray<T> Merge( ExtendedArray<T>... arrays )
	{
		Class <T> classType = this.classType;
		long length = 0;
		
		for(int i = 0; i < arrays.length; i++)
		{
			length += arrays[i].length;
			
			if(classType != arrays[i].classType)
				return null;
		}
		
		ExtendedArray<T> mergedArray = new ExtendedArray<T>(classType, length);
		
		long elementIndex = 0;
		int arrayIndex = 0;
		
		for(long i = 0; i < length; i++)
		{
			mergedArray.set(arrays[arrayIndex].get(elementIndex), i);
			
			elementIndex++;
			
			if(elementIndex >= arrays[arrayIndex].length)
			{
				arrayIndex++;
				elementIndex = 0;
			}
		}
		
		return mergedArray;
	}
	
	public void makeArrayOf(ExtendedArrayList<T> arrayList)
	{
		for(long i = 0; i < arrayList.length; i++)
			set(arrayList.get(i), i);
	}
}
