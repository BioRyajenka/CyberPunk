package com.jackson.myengine;

public class TrimmedSprite extends Sprite {
	private float x1, y1, x2, y2;
	
	public TrimmedSprite(float pX, float pY, String path) {
		super(pX, pY, path);
	}

	public void setStartDrawPosition(float pX1, float pY1) {
		x1 = pX1;
		y1 = pY1;
	}
	
	public void setFinishDrawPosition(float pX2, float pY2) {
		x2 = pX2;
		y2 = pY2;
	}
	
	@Override
	public void draw() {
		mImage.drawRegion(mX, mY, x1, y1, x2, y2);
	}
}
