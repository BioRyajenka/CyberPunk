package com.jackson.myengine;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprite extends Entity {
	private MyImage mImage;
	private float initialWidth, initialHeight;

	public Sprite(float pX, float pY, String path) {
		super(pX, pY);
		mImage = new MyImage(path);
		initialWidth = getWidth();
		initialHeight = getHeight();
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

	// public Image getImage() {
	// return mImage;
	// }

	public void setImage(String path) {
		mImage.setNewPath(path);
	}

	public void draw() {
		//if coordinates are fractional, we get a smooth picture
		mImage.draw((int)getGlobalX(), (int)getGlobalY());
		super.draw();
	}

	public void setSize(float pWidth, float pHeight) {
		mImage.setScale((int) pWidth, (int) pHeight);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}

	public void restoreSize() {
		setSize(initialWidth, initialHeight);
	}

	public void flipHorizontally() {
		mImage.flip(true, false);
		setColor(mRed, mGreen, mBlue, mAlpha);
	}
	
	public boolean isSelected(float x, float y) {
		return Utils.inBounds(x, getGlobalX(), getGlobalX() + getWidth() - 1) && Utils
				.inBounds(y, getGlobalY(), getGlobalY() + getHeight() - 1);
	}

	private static class MyImage {
		private static Map<Options, Image> imPool = new HashMap<Options, Image>();
		private Options opt;
		private Image im;

		public MyImage(String path) {
			opt = new Options();
			setNewPath(path);
		}

		public void setNewPath(String path) {
			opt.path = path;
			opt.fHor = opt.fVer = false;
			opt.mWidth = opt.mHeight = -1;
			if (imPool.containsKey(opt)) {
				im = imPool.get(opt);
			} else {
				try {
					im = new Image(path + ".png");
				} catch (SlickException e) {
					e.printStackTrace();
				}
				im.setImageColor(opt.mRed, opt.mGreen, opt.mBlue, opt.mAlpha);
				imPool.put(opt, im);
				opt = opt.copy();
			}
		}

		public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
			opt.setColor(pRed, pGreen, pBlue, pAlpha);
			if (imPool.containsKey(opt)) {
				im = imPool.get(opt);
			} else {
				im = im.copy();
				im.setImageColor(pRed, pGreen, pBlue, pAlpha);
				imPool.put(opt, im);
				opt = opt.copy();
			}
		}

		public float getWidth() {
			return im.getWidth();
		}

		public float getHeight() {
			return im.getHeight();
		}

		public void draw(float pX, float pY) {
			im.draw(pX, pY);
		}

		public void setScale(float pW, float pH) {
			opt.mWidth = pW;
			opt.mHeight = pH;
			if (imPool.containsKey(opt)) {
				im = imPool.get(opt);
			} else {
				imPool.put(opt, im.getScaledCopy((int) pW, (int) pH));
				opt = opt.copy();
			}
		}

		public void flip(boolean hor, boolean ver) {
			opt.fHor = hor;
			opt.fVer = ver;
			if (imPool.containsKey(opt)) {
				im = imPool.get(opt);
			} else {
				imPool.put(opt, im.getFlippedCopy(hor, ver));
				opt = opt.copy();
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