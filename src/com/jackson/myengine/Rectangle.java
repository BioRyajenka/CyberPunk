package com.jackson.myengine;

public interface Rectangle {
	public float getGlobalX();
	public float getGlobalY();
	
	public float getWidth();
	public float getHeight();
	
	public default boolean isSelected(float x, float y) {
		return Utils.inBounds(x, getGlobalX(), getGlobalX() + getWidth() - 1) && Utils
				.inBounds(y, getGlobalY(), getGlobalY() + getHeight() - 1);
	}
}
