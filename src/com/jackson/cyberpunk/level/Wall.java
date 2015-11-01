package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.Game;

public class Wall extends Obstacle {
	public Wall(int posI, int posJ, String floorPicName, String wallMaterial) {
		super(posI, posJ, floorPicName, "walls/" + wallMaterial, WallView.class);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	private boolean test(Cell c) {
		return c instanceof Wall || c instanceof Door;
	}

	protected int getWallType() {
		Cell[][] cells = Game.level.getCells();

		int type = 0;
		// - type 0
		if ((getJ() + 1 < cells[0].length && test(cells[getI()][getJ() + 1]))
				|| (getJ() > 0 && test(cells[getI()][getJ() - 1])))
			type = 0;
		// | type 1
		if ((getI() + 1 < cells.length && test(cells[getI() + 1][getJ()])) || (getI() > 0
				&& test(cells[getI() - 1][getJ()]))) {
			type = 1;
		}
		// _|_ type 2
		if (getJ() + 1 < cells[0].length && test(cells[getI()][getJ() + 1]) && getI()
				+ 1 < cells.length && test(cells[getI() + 1][getJ()]) && getI() > 0
				&& test(cells[getI() - 1][getJ()]))
			type = 2;
		// _ _ type 3
		// |
		if (getJ() > 0 && test(cells[getI()][getJ() - 1]) && getI() + 1 < cells.length
				&& test(cells[getI() + 1][getJ()]) && getI() > 0 && test(cells[getI()
						- 1][getJ()]))
			type = 3;
		// |_ type 4
		// |
		if (getI() + 1 < cells.length && test(cells[getI() + 1][getJ()]) && getJ()
				+ 1 < cells[0].length && test(cells[getI()][getJ() + 1]) && getJ() > 0
				&& test(cells[getI()][getJ() - 1]))
			type = 4;
		// _| type 5
		// |
		if (getI() > 0 && test(cells[getI() - 1][getJ()]) && getJ() + 1 < cells[0].length
				&& test(cells[getI()][getJ() + 1]) && getJ() > 0 && test(
						cells[getI()][getJ() - 1]))
			type = 5;
		// + type 6
		if (getI() + 1 < cells.length && test(cells[getI() + 1][getJ()]) && getI() > 0
				&& test(cells[getI() - 1][getJ()]) && getJ() + 1 < cells[0].length
				&& test(cells[getI()][getJ() + 1]) && getJ() > 0 && test(
						cells[getI()][getJ() - 1]))
			type = 6;
		// _ type 7
		// |
		if (getI() + 1 < cells.length && test(cells[getI() + 1][getJ()]) && !(getI() > 0
				&& test(cells[getI() - 1][getJ()])) && getJ() + 1 < cells[0].length
				&& test(cells[getI()][getJ() + 1]) && !(getJ() > 0 && test(
						cells[getI()][getJ() - 1])))
			type = 7;
		// _| type 8
		if (!(getI() + 1 < cells.length && test(cells[getI() + 1][getJ()])) && getI() > 0
				&& test(cells[getI() - 1][getJ()]) && !(getJ() + 1 < cells[0].length
						&& test(cells[getI()][getJ() + 1])) && getJ() > 0 && test(
								cells[getI()][getJ() - 1]))
			type = 8;
		// |_ type 9
		if (!(getI() + 1 < cells.length && test(cells[getI() + 1][getJ()])) && getI() > 0
				&& test(cells[getI() - 1][getJ()]) && getJ() + 1 < cells[0].length
				&& test(cells[getI()][getJ() + 1]) && !(getJ() > 0 && test(
						cells[getI()][getJ() - 1])))
			type = 9;
		// _ type 10
		// |
		if (getI() + 1 < cells.length && test(cells[getI() + 1][getJ()]) && !(getI() > 0
				&& test(cells[getI() - 1][getJ()])) && !(getJ() + 1 < cells[0].length
						&& test(cells[getI()][getJ() + 1])) && getJ() > 0 && test(
								cells[getI()][getJ() - 1]))
			type = 10;
		return type;
	}

	@Override
	protected String getObstaclePicPath() {
		String newpath = "";
		String path = super.getObstaclePicPath();

		int type = getWallType();

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
