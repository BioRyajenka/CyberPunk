package com.jackson.myengine;

public class CycledSprite extends Sprite {
	private float mWidth, mHeight;
	private float startPosX, startPosY;

	public CycledSprite(float pX, float pY, String sourcePath) {
		super(pX, pY, sourcePath);
		setSize(mImage.getWidth(), mImage.getHeight());
	}

	public CycledSprite(float pX, float pY, float startPosX, float startPosY,
			String sourcePath, float pWidth, float pHeight) {
		super(pX, pY, sourcePath);
		setSize(pWidth, pHeight);
		setStartPosXY(startPosX, startPosY);
	}

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

	public void setStartPosXY(float startPosX, float startPosY) {
		if (!Utils.inBounds(startPosX, 0, getInitialWidth()) || !Utils.inBounds(
				startPosY, 0, getInitialHeight())) {
			Log.e("startPos (" + startPosX + ", " + startPosY
					+ ") must lie inside the image (" + getInitialWidth() + ", "
					+ getInitialHeight() + ")");
			Log.printStackTrace();
		}
		this.startPosX = startPosX;
		this.startPosY = startPosY;
	}

	public void draw() {
		if (mWidth == 0 || mHeight == 0) {
			return;
		}
		float imageWidth = mImage.getWidth() - 1;
		float imageHeight = mImage.getHeight() - 1;

		float gx = getGlobalX();
		float gy = getGlobalY();

		float width = mWidth;
		float height = mHeight;

		float leftWidth, leftHeight;

		// shift
		if (startPosX != 0 || startPosY != 0) {
			mImage.drawRegion(gx, gy, startPosX, startPosY, Math.min(width + startPosX,
					imageWidth), Math.min(height + startPosY, imageHeight));
		}
		if (startPosY != 0) {
			for (int i = 0; i < (int) ((width - imageWidth + startPosX)
					/ imageWidth); i++) {
				mImage.drawRegion(gx + i * imageWidth + imageWidth - startPosX, gy, 0,
						startPosY, imageWidth, Math.min(height, imageHeight
								+ startPosY));
			}
		}
		if (startPosX != 0) {
			for (int j = 0; j < (int) ((height - imageHeight + startPosY)
					/ imageHeight); j++) {
				mImage.drawRegion(gx, gy + j * imageHeight + imageHeight - startPosY,
						startPosX, 0, Math.min(width + startPosX, imageWidth),
						imageHeight);
			}
		}

		leftWidth = width - (int) ((width - imageWidth + startPosX) / imageWidth)
				* imageWidth;
		leftHeight = height - (int) ((height - imageHeight + startPosY) / imageHeight)
				* imageHeight;
		if (startPosX != 0 || startPosY != 0) {
			if (width < imageWidth) {
				leftWidth = 0;
			} else {
				leftWidth -= imageWidth - startPosX;
			}
			if (height < imageHeight) {
				leftHeight = 0;
			} else {
				leftHeight -= imageHeight - startPosY;
			}
		}
		if (startPosY != 0 && leftWidth > 0) {
			mImage.drawRegion(gx + width - leftWidth, gy, 0, startPosY, leftWidth, Math
					.min(height + startPosY, imageHeight));
		}
		if (startPosX != 0 && leftHeight > 0) {
			mImage.drawRegion(gx, gy + height - leftHeight, startPosX, 0, Math.min(width
					+ startPosX, imageWidth), leftHeight);
		}

		if (width < imageWidth || height < imageHeight) {
			if (startPosX == 0 && startPosY == 0) {
				mImage.drawRegion(gx, gy, 0, 0, width, height);
			}
			return;
		}

		if (startPosX != 0) {
			gx += imageWidth - startPosX;
			width -= imageWidth - startPosX;
		}
		if (startPosY != 0) {
			gy += imageHeight - startPosY;
			height -= imageHeight - startPosY;
		}

		for (int i = 0; i < (int) (width / imageWidth); i++) {
			for (int j = 0; j < (int) (height / imageHeight); j++) {
				mImage.draw(gx + i * imageWidth, gy + j * imageHeight);
			}
		}

		leftWidth = width - (int) (width / imageWidth) * imageWidth;
		leftHeight = height - (int) (height / imageHeight) * imageHeight;

		if (leftWidth != 0) {
			for (int j = 0; j < (int) (height / imageHeight); j++) {
				mImage.drawRegion(gx + width - leftWidth, gy + j * imageHeight, 0, 0,
						leftWidth, imageHeight);
			}
		}

		if (leftHeight != 0) {
			for (int i = 0; i < (int) (width / imageWidth); i++) {
				mImage.drawRegion(gx + i * imageWidth, gy + height - leftHeight, 0, 0,
						imageWidth, leftHeight);
			}
		}

		if (leftWidth != 0 && leftHeight != 0) {
			mImage.drawRegion(gx + width - leftWidth, gy + height - leftHeight, 0, 0,
					leftWidth, leftHeight);
		}
	}
}