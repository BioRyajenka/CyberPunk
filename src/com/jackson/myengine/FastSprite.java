package com.jackson.myengine;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class FastSprite extends Entity {
	private static Map<String, Image> initImages = new HashMap<String, Image>();
	// allows to spend less memory, but no time. so, it's not neccesary
	// private static Set<Image> imageSet = new HashSet<Image>();

	// TODO: hashfunction, that considers size and color with default
	// opportunity

	private Image mImage;
	private float initialWidth, initialHeight;

	public FastSprite(float pX, float pY, String path) {
		super(pX, pY);
		setImage(path);
		initialWidth = getWidth();
		initialHeight = getHeight();
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		mImage.getFlippedCopy(false, false);
		mImage.setImageColor(pRed, pGreen, pBlue, pAlpha);
	}

	public float getWidth() {
		return mImage.getWidth();
	}

	public float getHeight() {
		return mImage.getHeight();
	}

	public Image getImage() {
		return mImage;
	}

	public void setImage(String path) {
		Image image = null;
		if (initImages.containsKey(path)) {
			image = initImages.get(path);
		} else {
			try {
				image = new Image(path);
				initImages.put(path, image);
			} catch (SlickException e) {
				Log.e(e.toString());
			}
		}
		setImage(image);
	}

	private void setImage(Image pImage) {
		this.mImage = pImage;
		mImage.setImageColor(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public void onDraw() {
		mImage.draw(getGlobalX(), getGlobalY());
		super.onDraw();
	}

	public void setSize(float pWidth, float pHeight) {
		mImage = mImage.getScaledCopy((int) pWidth, (int) pHeight);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}

	public void restoreSize() {
		setSize(initialWidth, initialHeight);
	}

	public void flipHorizontally() {
		mImage = mImage.getFlippedCopy(true, false);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}
}