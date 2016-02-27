package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.Game;

public abstract class Obstacle extends Floor {
	private String obstaclePicPath;

	public Obstacle(int posI, int posJ, String floorPicName, String obstaclePicName,
			Class<? extends CellView> viewClass) {
		super(posI, posJ, floorPicName);
		this.viewClass = viewClass;
		this.obstaclePicPath = "res/level/" + obstaclePicName;
		this.isPassable = false;
	}
	
	protected boolean test(Cell c) {
		return c instanceof Obstacle;
	}
	
	protected int getObstaclePositionType() {
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
		//  |
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
		//  |
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

	protected String getObstaclePicPath() {
		return obstaclePicPath;
	}
}