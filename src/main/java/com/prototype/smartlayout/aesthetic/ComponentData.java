package com.prototype.smartlayout.aesthetic;

import java.util.Vector;

import com.prototype.smartlayout.model.LayoutComponent;

import lombok.Getter;

@Getter
public class ComponentData
{
	private LayoutComponent component;
	
	private float totalArea;
	private float leftArea;
	private float rightArea;
	private float topArea;
	private float bottomArea;
	
	private float distance;
	private float horizontalDistance;
	private float verticalDistance;
	private float leftDistance;
	private float rightDistance;
	private float topDistance;
	private float bottomDistance;
	
	private float centerX;
	private float centerY;
	
	private Vector<QuadrantData> quadrants;
	
	public ComponentData(float totalArea, float leftArea, float rightArea, float topArea, float bottomArea, float distance, float horizontalDistance,
			float verticalDistance, float leftDistance, float rightDistance, float topDistance, float bottomDistance, float centerX, float centerY)
	{
		this.totalArea = totalArea;
		this.leftArea = leftArea;
		this.rightArea = rightArea;
		this.topArea = topArea;
		this.bottomArea = bottomArea;

		this.distance = distance;
		this.horizontalDistance = horizontalDistance;
		this.verticalDistance = verticalDistance;
		this.leftDistance = leftDistance;
		this.rightDistance = rightDistance;
		this.topDistance = topDistance;
		this.bottomDistance = bottomDistance;

		this.centerX = centerX;
		this.centerY = centerY;
		
		//Print();
	}
	
	public void CalculateQuadrants(float frameCenterX, float frameCenterY)
	{
		quadrants = new Vector<QuadrantData>();
		
		if(leftArea != 0 && rightArea != 0 && topArea != 0 && bottomArea != 0)
		{
			float widthLeft, widthRight, heightTop, heightBottom;
			float centerXLeft, centerXRight, centerYTop, centerYBottom;
			
			widthLeft = frameCenterX - component.getAssignedX();
			widthRight = component.getAssignedX() + component.getAssignedWidth() - frameCenterX;
			heightTop = frameCenterY - component.getAssignedY();
			heightBottom = component.getAssignedY() + component.getAssignedHeight() - frameCenterY;
			
			centerXLeft = component.getAssignedX() + widthLeft / 2;
			centerXRight = frameCenterX + widthRight / 2;
			centerYTop = component.getAssignedY() + heightTop / 2;
			centerYBottom = frameCenterY + heightBottom / 2;
			
			QuadrantData ulData = new QuadrantData(QuadrantEnum.UL, centerXLeft, centerYTop, widthLeft, heightTop);
			quadrants.add(ulData);

			QuadrantData urData = new QuadrantData(QuadrantEnum.UR, centerXRight, centerYTop, widthRight, heightTop);
			quadrants.add(urData);

			QuadrantData llData = new QuadrantData(QuadrantEnum.LL, centerXLeft, centerYBottom, widthLeft, heightBottom);
			quadrants.add(llData);
			
			QuadrantData lrData = new QuadrantData(QuadrantEnum.LR, centerXRight, centerYBottom, widthRight, heightBottom);
			quadrants.add(lrData);
		}
		else if(leftArea != 0 && rightArea != 0)
		{
			float widthLeft, widthRight, height;
			float centerXLeft, centerXRight, centerY;
			
			widthLeft = frameCenterX - component.getAssignedX();
			widthRight = component.getAssignedX() + component.getAssignedWidth() - frameCenterX;
			height = component.getAssignedHeight();
			
			centerXLeft = component.getAssignedX() + widthLeft / 2;
			centerXRight = frameCenterX + widthRight / 2;
			centerY = component.getAssignedY() + height / 2;
			
			if(topArea != 0)
			{
				QuadrantData ulData = new QuadrantData(QuadrantEnum.UL, centerXLeft, centerY, widthLeft, height);
				quadrants.add(ulData);

				QuadrantData urData = new QuadrantData(QuadrantEnum.UR, centerXRight, centerY, widthRight, height);
				quadrants.add(urData);
			}
			else
			{
				QuadrantData llData = new QuadrantData(QuadrantEnum.LL, centerXLeft, centerY, widthLeft, height);
				quadrants.add(llData);
				
				QuadrantData lrData = new QuadrantData(QuadrantEnum.LR, centerXRight, centerY, widthRight, height);
				quadrants.add(lrData);
			}
		}
		else if(topArea != 0 && bottomArea != 0)
		{
			float width, heightTop, heightBottom;
			float centerX, centerYTop, centerYBottom;
			
			width = component.getAssignedWidth();
			heightTop = frameCenterY - component.getAssignedY();
			heightBottom = component.getAssignedY() + component.getAssignedHeight() - frameCenterY;
			
			centerX = component.getAssignedX() + width / 2;
			centerYTop = component.getAssignedY() + heightTop / 2;
			centerYBottom = frameCenterY + heightBottom / 2;
			
			if(leftArea != 0)
			{
				QuadrantData ulData = new QuadrantData(QuadrantEnum.UL, centerX, centerYTop, width, heightTop);
				quadrants.add(ulData);

				QuadrantData llData = new QuadrantData(QuadrantEnum.LL, centerX, centerYBottom, width, heightBottom);
				quadrants.add(llData);
			}
			else
			{
				QuadrantData urData = new QuadrantData(QuadrantEnum.UR, centerX, centerYTop, width, heightTop);
				quadrants.add(urData);
				
				QuadrantData lrData = new QuadrantData(QuadrantEnum.LR, centerX, centerYBottom, width, heightBottom);
				quadrants.add(lrData);
			}
		}
		else
		{
			float width, height;
			float centerX, centerY;
			
			width = component.getAssignedWidth();
			height = component.getAssignedHeight();
			
			centerX = component.getAssignedX() + width / 2;
			centerY = component.getAssignedY() + height / 2;
			
			if(topArea != 0)
			{
				if(leftArea != 0)
				{
					QuadrantData ulData = new QuadrantData(QuadrantEnum.UL, centerX, centerY, width, height);
					quadrants.add(ulData);
				}
				else
				{
					QuadrantData urData = new QuadrantData(QuadrantEnum.UR, centerX, centerY, width, height);
					quadrants.add(urData);
				}
			}
			else
			{
				if(leftArea != 0)
				{
					QuadrantData llData = new QuadrantData(QuadrantEnum.LL, centerX, centerY, width, height);
					quadrants.add(llData);
				}
				else
				{
					QuadrantData lrData = new QuadrantData(QuadrantEnum.LR, centerX, centerY, width, height);
					quadrants.add(lrData);
				}
			}
		}
	}
	
	public void SetComponent(LayoutComponent component)
	{
		this.component = component;
	}
	
	/*private void Print()
	{
		System.out.println("\tTotal Area: " + totalArea);
		System.out.println("\tLeft Area: " + leftArea);
		System.out.println("\tRight Area: " + rightArea);
		System.out.println("\tTop Area: " + topArea);
		System.out.println("\tBottom Area: " + bottomArea);
		System.out.println("\tDistance: " + distance);
		System.out.println("\tHorizontal Distance: " + horizontalDistance);
		System.out.println("\tVertical Distance: " + verticalDistance);
		System.out.println("\tLeft Distance: " + leftDistance);
		System.out.println("\tRight Distance: " + rightDistance);
		System.out.println("\tTop Distance: " + topDistance);
		System.out.println("\tBottom Distance: " + bottomDistance);
		System.out.println("\tCenterX: " + centerX);
		System.out.println("\tCenterY: " + centerY);
		System.out.println();
	}*/
}
