package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;

public class Door extends Obstacle {
	private final static float OPEN_AP_COST = .1f;
	private final static float CLOSE_AP_COST = .1f;

	private boolean opened, wasOpened;
	protected LockType lockType;
	private StateManager stateManager;

	public Door(int posI, int posJ, LockType keyType, String floorPicName,
			String doorMaterial) {
		super(posI, posJ, floorPicName, "doors/" + doorMaterial, DoorView.class);
		this.lockType = keyType;
		stateManager = new StateManager();
	}

	public boolean isBroken() {
		return stateManager.isBroken();
	}
	
	public boolean isHacked() {
		return stateManager.isHacked();
	}

	public boolean tryHack() {
		if (stateManager.tryHack()) {
			opened = true;
			return true;
		} else {
			return false;
		}
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
		isPassable = opened;
		if (opened) {
			wasOpened = true;
		}
	}

	public boolean isOpened() {
		return opened;
	}
	
	public boolean isWasOpened() {
		return wasOpened;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		if (hasMob()) {
			return menu;
		}
		if (isBroken()) {
			menu.add(Type.NOT_ACTIVE, "Дверь заблокирована");
		} else {
			if (opened) {
				menu.add(Type.LVL_CLOSE_DOOR, null, CLOSE_AP_COST);
				menu.add(Type.LVL_GO);
			} else {
				menu.add(Type.LVL_OPEN_DOOR, null, OPEN_AP_COST);
				if (lockType != LockType.NONE && !wasOpened && !isHacked()) {
					menu.add(Type.LVL_HACK);
				}
			}
		}
		return menu;
	}

	@Override
	protected String getObstaclePicPath() {
		return super.getObstaclePicPath() + "/" + (isBroken() ? "broken"
				: opened ? "opened" : "closed");
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
