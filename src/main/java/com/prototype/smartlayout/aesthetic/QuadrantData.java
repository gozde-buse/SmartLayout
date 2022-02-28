package com.prototype.smartlayout.aesthetic;

import lombok.Getter;

@Getter
public class QuadrantData
{
	private QuadrantEnum quadrantPosition;
	private float area;
	
	private float centerX;
	private float centerY;
	
	private float width;
	private float height;
	
	public QuadrantData(QuadrantEnum quadrantPosition, float centerX, float centerY, float width, float height)
	{
		this.quadrantPosition = quadrantPosition;
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		
		area = width * height;
	}
}
