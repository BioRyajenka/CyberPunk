package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;

public class Wall extends Obstacle {
	public Wall(int posI, int posJ, String floorPicName, String wallMaterial) {
		super(posI, posJ, floorPicName, "walls/" + wallMaterial, WallView.class);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	@Override
	protected String getObstaclePicPath() {
		String newpath = "";
		String path = super.getObstaclePicPath();

		int type = getObstaclePositionType();

		if (type == 0 || type == 1)
			newpath = path + "/1";
		if (type == 2 || type == 4)
			newpath = path + "/2";
		if (type == 3 || type == 5)
			newpath = path + "/3";
		if (type == 6)
			newpath = path + "/4";
		if (type == 7)
			newpath = path + "/7";
		if (type == 8)
			newpath = path + "/6";
		if (type == 9)
			newpath = path + "/5";
		if (type == 10)
			newpath = path + "/5";
		return newpath;
	}
}
