package com.jackson.myengine;

import com.jackson.cyberpunk.MyScene;

public class CycledSprite extends Sprite {
	private float mWidth, mHeight;
	//private boolean shiftLastSprite;
	
	public CycledSprite(float pX, float pY, String sourcePath) {
		super(pX, pY, sourcePath);
		setSize(mImage.getWidth(), mImage.getHeight());
	}

	public CycledSprite(float pX, float pY, String sourcePath, float pWidth,
			float pHeight) {
		super(pX, pY, sourcePath);
		setSize(pWidth, pHeight);
	}
	
//	public void setShiftLastSprite(boolean shiftLastSprite) {
//		this.shiftLastSprite = shiftLastSprite;
//	}
	
	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	public void setWidth(float pWidth) {
		mWidth = pWidth;
	}

	public void setHeight(float pHeight) {
		mHeight = pHeight;
	}

	public void draw() {
		float imageWidth = mImage.getWidth() - 1; //(shiftLastSprite ? 1 : 0);
		float imageHeight = mImage.getHeight() - 1; //(shiftLastSprite ? 1 : 0);
		
		float gx = getGlobalX();
		float gy = getGlobalY();
		
		for (int i = 0; i < (int)(mWidth / imageWidth); i++) {
			for (int j = 0; j < (int)(mHeight / imageHeight); j++) {
				mImage.draw(gx + i * imageWidth, gy + j * imageHeight);
			}
		}
		float leftWidth = mWidth - (int) (mWidth / imageWidth) * imageWidth;
		float leftHeight = mHeight - (int) (mHeight / imageHeight) * imageHeight;

		if (leftWidth != 0) {
			for (int j = 0; j < (int)(mHeight / imageHeight); j++) {
				mImage.drawRegion(gx + mWidth - leftWidth, gy + j * imageHeight, 0, 0,
						leftWidth, imageHeight);
			}
		}

		if (leftHeight != 0) {
			for (int i = 0; i < (int)(mWidth / imageWidth); i++) {
				mImage.drawRegion(gx + i * imageWidth, gy + mHeight - leftHeight, 0, 0,
						imageWidth, leftHeight);
			}
		}

		if (leftWidth != 0 && leftHeight != 0) {
			mImage.drawRegion(gx + mWidth - leftWidth, gy + mHeight - leftHeight, 0, 0,
					leftWidth, leftHeight);
		}
	}
}