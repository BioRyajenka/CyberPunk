package com.jackson.myengine;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprite extends Entity implements ChangeableRectangle {
	protected MyImage image;
	private int initialWidth, initialHeight;

	public Sprite(float x, float y, String path) {
		super(x, y);
		image = new MyImage(path);
		updateInitialSizes();
	}

	private void updateInitialSizes() {
		initialWidth = (int) image.getWidth();
		initialHeight = (int) image.getHeight();
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
	}

	public float getWidth() {
		return image.getWidth();
	}

	public float getHeight() {
		return image.getHeight();
	}

	public void setImage(String path) {
		image.setNewPath(path);
		updateInitialSizes();
	}

	public void draw() {
		// if coordinates are fractional, we get a smooth picture
		image.draw(getGlobalX(), getGlobalY());
		super.draw();
	}

	public void setWidth(float pWidth) {
		if ((int) pWidth == (int) getWidth()) {
			return;
		}
		image.setScale((int) pWidth, (int) getHeight());
		setColor(red, green, blue, alpha);
	}

	public void setHeight(float pHeight) {
		if ((int) pHeight == (int) getHeight()) {
			return;
		}
		image.setScale((int) getWidth(), (int) pHeight);
		setColor(red, green, blue, alpha);
	}

	public void restoreSize() {
		setSize(initialWidth, initialHeight);
	}

	public void flipHorizontally() {
		image.flip(true, false);
		setColor(red, green, blue, alpha);
	}
	
	public void flipVertically() {
		image.flip(false, true);
		setColor(red, green, blue, alpha);
	}

	private static Map<MyImage.Options, Image> imagesPool = new HashMap<MyImage.Options, Image>();

	protected class MyImage {
		private Options options;
		private Image image;

		public MyImage(String path) {
			options = new Options();
			setNewPath(path);
		}

		public void setNewPath(String path) {
			options.path = path;
			options.fHor = options.fVer = false;
			options.width = options.height = -1;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				try {
					image = new Image(path + ".png");
				} catch (SlickException e) {
					e.printStackTrace();
				}
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
			image.draw((int) pX, (int) pY, new Color(red, green, blue, alpha));
		}

		public void drawRegion(float pX, float pY, float pX1, float pY1, float pX2,
				float pY2) {
			image.draw((int) pX, (int) pY, (int) (pX + pX2 - pX1), (int) (pY + pY2
					- pY1), (int) pX1, (int) pY1, (int) pX2, (int) pY2, new Color(red,
							green, blue, alpha));
		}

		public void setScale(float pW, float pH) {
			options.width = pW;
			options.height = pH;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				image = image.getScaledCopy((int) pW, (int) pH);
				imagesPool.put(options, image);
				options = options.copy();
			}
		}

		public void flip(boolean hor, boolean ver) {
			options.fHor = hor;
			options.fVer = ver;
			if (imagesPool.containsKey(options)) {
				image = imagesPool.get(options);
			} else {
				image = image.getFlippedCopy(hor, ver);
				imagesPool.put(options, image);
				options = options.copy();
			}
		}

		private class Options {
			float width = -1, height = -1;// means unmodified
			boolean fHor = false, fVer = false;
			String path = null;

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + (fHor ? 1231 : 1237);
				result = prime * result + (fVer ? 1231 : 1237);
				result = prime * result + Float.floatToIntBits(height);
				result = prime * result + Float.floatToIntBits(width);
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
				if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
					return false;
				if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
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
				obj.width = width;
				obj.height = height;
				obj.fHor = fHor;
				obj.fVer = fVer;
				obj.path = path;
				return obj;
			}
		}
	}
}