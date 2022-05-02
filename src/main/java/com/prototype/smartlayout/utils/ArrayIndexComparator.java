package com.prototype.smartlayout.utils;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer>
{
	private final long[] array;

	public ArrayIndexComparator (long[] maxIndices)
	{
		this.array = maxIndices;
	}

	public ArrayIndexComparator (int[] maxIndices)
	{
		long[] array = new long[maxIndices.length];
		
		for(int i = 0; i < maxIndices.length; i++)
			array[i] = maxIndices[i];
		
		this.array = array;
	}

	public Integer[] createIndexArray()
	{
		Integer[] indexes = new Integer[array.length];
		
		for (int i = 0; i < array.length; i++)
		{
			indexes[i] = i;
		}
		
		return indexes;
	}

	@Override
	public int compare(Integer index1, Integer index2)
	{
		return Long.compare(array[index1], array[index2]);
	}
}
