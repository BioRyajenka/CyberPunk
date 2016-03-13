package com.jackson.myengine;

public interface ChangeableRectangle extends Rectangle {
	public void setWidth(float width);
	public void setHeight(float height);
	
	public default void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}
}
