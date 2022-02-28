package com.prototype.smartlayout.model.enums;

public enum OrientationEnum {
	SINGLE(0),
	HORIZONTAL(1),
	VERTICAL(2),//GRID_LAYOUT
	OTHER(3);

	public final int id;

	OrientationEnum (int id) {
		this.id = id;
	}
}
