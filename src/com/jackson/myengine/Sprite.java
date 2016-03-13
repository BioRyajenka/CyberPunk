package com.jackson.myengine;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprite extends Entity implements ChangeableRectangle {
	protected MyImage mImage;
	private int initialWidth, initialHeight;

	public Sprite(float pX, float pY, String path) {
		super(pX, pY);
		mImage = new MyImage(path);
		initialWidth = (int)mImage.getWidth();
		initialHeight = (int)mImage.getHeight();
	}
	
	public float getInitialWidth() {
		return initialWidth;
	}
	
	public float getInitialHeight() {
		return initialHeight;
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		mImage.setColor(pRed, pGreen, pBlue, pAlpha);
	}

	public float getWidth() {
		return mImage.getWidth();
	}

	public float getHeight() {
		return mImage.getHeight();
	}

	public void setImage(String path) {
		mImage.setNewPath(path);
	}

	public void draw() {
		// if coordinates are fractional, we get a smooth picture
		mImage.draw((int) getGlobalX(), (int) getGlobalY());
		super.draw();
	}
	
	public void setWidth(float pWidth) {
		if ((int)pWidth == (int)getWidth()) {
			return;
		}
		mImage.setScale((int) pWidth, (int) getHeight());
		setColor(mRed, mGreen, mBlue, mAlpha);
	}
	
	public void setHeight(float pHeight) {
		if ((int)pHeight == (int)getHeight()) {
			return;
		}
		mImage.setScale((int) getWidth(), (int) pHeight);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}

	public void restoreSize() {
		setSize(initialWidth, initialHeight);
	}

	public void flipHorizontally() {
		mImage.flip(true, false);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}

	protected static class MyImage {
		private static Map<Options, Image> imagesPool = new HashMap<Options, Image>();
		private Options options;
		private Image image;

		public MyImage(String path) {
			options = new Options();
			setNewPath(path);
		}

		public void setNewPath(String path) {
			options.path = path;
			options.fHor = options.fVer = false;
			options.mWidth = options.mHeight = -1;
			options.mRed = options.mGreen = options.mBlue = options.mAlpha = 1;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				try {
					image = new Image(path + ".png");
				} catch (SlickException e) {
					e.printStackTrace();
				}
				image.setImageColor(options.mRed, options.mGreen, options.mBlue,
						options.mAlpha);
				imagesPool.put(options, image);
				options = options.copy();
			}
		}

		public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
			options.setColor(pRed, pGreen, pBlue, pAlpha);
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				image = image.copy();
				image.setImageColor(pRed, pGreen, pBlue, pAlpha);
				imagesPool.put(options, image);
				options = options.copy();
			}
		}

		public float getWidth() {
			return image.getWidth();
		}

		public float getHeight() {
			return image.getHeight();
		}

		public void draw(float pX, float pY) {
			image.draw(pX, pY);
		}

		public void drawRegion(float pX, float pY, float pX1, float pY1, float pX2,
				float pY2) {
			image.draw(pX, pY, pX + pX2 - pX1, pY + pY2 - pY1, pX1, pY1, pX2, pY2);
		}

		public void setScale(float pW, float pH) {
			options.mWidth = pW;
			options.mHeight = pH;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				imagesPool.put(options, image.getScaledCopy((int) pW, (int) pH));
				options = options.copy();
			}
		}

		public void flip(boolean hor, boolean ver) {
			options.fHor = hor;
			options.fVer = ver;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				imagesPool.put(options, image.getFlippedCopy(hor, ver));
				options = options.copy();
			}
		}

		private static class Options {
			float mRed = 1, mGreen = 1, mBlue = 1, mAlpha = 1;
			float mWidth = -1, mHeight = -1;// means unmodified
			boolean fHor = false, fVer = false;
			String path = null;

			public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
				mRed = pRed;
				mGreen = pGreen;
				mBlue = pBlue;
				mAlpha = pAlpha;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + (fHor ? 1231 : 1237);
				result = prime * result + (fVer ? 1231 : 1237);
				result = prime * result + Float.floatToIntBits(mAlpha);
				result = prime * result + Float.floatToIntBits(mBlue);
				result = prime * result + Float.floatToIntBits(mGreen);
				result = prime * result + Float.floatToIntBits(mHeight);
				result = prime * result + Float.floatToIntBits(mRed);
				result = prime * result + Float.floatToIntBits(mWidth);
				result = prime * result + ((path == null) ? 0 : path.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Options other = (Options) obj;
				if (fHor != other.fHor)
					return false;
				if (fVer != other.fVer)
					return false;
				if (Float.floatToIntBits(mAlpha) != Float.floatToIntBits(other.mAlpha))
					return false;
				if (Float.floatToIntBits(mBlue) != Float.floatToIntBits(other.mBlue))
					return false;
				if (Float.floatToIntBits(mGreen) != Float.floatToIntBits(other.mGreen))
					return false;
				if (Float.floatToIntBits(mHeight) != Float.floatToIntBits(other.mHeight))
					return false;
				if (Float.floatToIntBits(mRed) != Float.floatToIntBits(other.mRed))
					return false;
				if (Float.floatToIntBits(mWidth) != Float.floatToIntBits(other.mWidth))
					return false;
				if (path == null) {
					if (other.path != null)
						return false;
				} else if (!path.equals(other.path))
					return false;
				return true;
			}

			public Options copy() {
				Options obj = new Options();
				obj.mRed = mRed;
				obj.mGreen = mGreen;
				obj.mBlue = mBlue;
				obj.mAlpha = mAlpha;
				obj.mWidth = mWidth;
				obj.mHeight = mHeight;
				obj.fHor = fHor;
				obj.fVer = fVer;
				obj.path = path;
				return obj;
			}
		}
	}
}