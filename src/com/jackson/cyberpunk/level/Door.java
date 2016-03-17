package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;

public class Door extends Obstacle {
	private final static float OPEN_AP_COST = .1f;
	private final static float CLOSE_AP_COST = .1f;
	
	private boolean isOpened;
	protected LockType lockType;

	public Door(int posI, int posJ, LockType keyType, String floorPicName,
			String doorMaterial) {
		super(posI, posJ, floorPicName, "doors/" + doorMaterial, DoorView.class);
		this.lockType = keyType;
	}

	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
		isPassable = isOpened;
	}

	public boolean isOpened() {
		return isOpened;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		if (hasMob()) {
			return menu;
		}
		if (isOpened) {
			menu.add(Type.LVL_CLOSE_DOOR, null, CLOSE_AP_COST);
			menu.add(Type.LVL_GO);
		} else {
			menu.add(Type.LVL_OPEN_DOOR, null, OPEN_AP_COST);
		}
		return menu;
	}

	@Override
	protected String getObstaclePicPath() {
		return super.getObstaclePicPath() + "/" + (isOpened ? "opened" : "closed");
	}

	public LockType getLockType() {
		return lockType;
	}

	public enum LockType {
		NONE("никакой :(", 1f, 1f, 1f, 0f), KEY1("Зеленый", .4f, 1f, .4f), KEY2(
				"Золотой", 1f, 1f, .2f);

		private float mRed, mGreen, mBlue, mAlpha;
		private String name;

		public float getRed() {
			return mRed;
		}

		public float getGreen() {
			return mGreen;
		}

		public float getBlue() {
			return mBlue;
		}

		public float getAlpha() {
			return mAlpha;
		}

		public String getName() {
			return name;
		}

		private LockType(String name, float pRed, float pGreen, float pBlue) {
			this(name, pRed, pGreen, pBlue, 1f);
		}

		private LockType(String name, float pRed, float pGreen, float pBlue,
				float pAlpha) {
			this.name = name;
			mRed = pRed;
			mGreen = pGreen;
			mBlue = pBlue;
			mAlpha = pAlpha;
		}
	}
}
