package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;

public class Door extends Obstacle {
	private boolean isOpened;
	
	public Door(int posI, int posJ, String floorPicName, String doorMaterial) {
		super(posI, posJ, floorPicName, "doors/" + doorMaterial, DoorView.class);
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
			menu.add(Type.LVL_CLOSE_DOOR);
			menu.add(Type.LVL_GO);
		} else {
			menu.add(Type.LVL_OPEN_DOOR);
		}
		return menu;
	}
	
	@Override
	protected String getObstaclePicPath() {
		return super.getObstaclePicPath() + "/" + (isOpened ? "opened" : "closed");
	}
}
