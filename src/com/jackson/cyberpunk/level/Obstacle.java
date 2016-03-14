package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.Game;

public abstract class Obstacle extends Floor {
	private String obstaclePicPath;

	public Obstacle(int posI, int posJ, String floorPicName, String obstaclePicName,
			Class<? extends ObstacleView> viewClass) {
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
		int i = getI();
		int j = getJ();
		// - type 0
		if ((j + 1 < cells[0].length && test(cells[i][j + 1])) || (j > 0 && test(
				cells[j][j - 1])))
			type = 0;
		// | type 1
		if ((i + 1 < cells.length && test(cells[i + 1][j])) || (i > 0 && test(cells[i
				- 1][j]))) {
			type = 1;
		}
		// _|_ type 2
		if (j + 1 < cells[0].length && test(cells[i][j + 1]) && i + 1 < cells.length
				&& test(cells[i + 1][j]) && i > 0 && test(cells[i - 1][j]))
			type = 2;
		// _ _ type 3
		// .|
		if (j > 0 && test(cells[i][j - 1]) && i + 1 < cells.length && test(cells[i
				+ 1][j]) && i > 0 && test(cells[i - 1][j]))
			type = 3;
		// |_ type 4
		// |
		if (i + 1 < cells.length && test(cells[i + 1][j]) && j + 1 < cells[0].length
				&& test(cells[i][j + 1]) && j > 0 && test(cells[i][j - 1]))
			type = 4;
		// _| type 5
		// .|
		if (i > 0 && test(cells[i - 1][j]) && j + 1 < cells[0].length && test(cells[i][j
				+ 1]) && j > 0 && test(cells[i][j - 1]))
			type = 5;
		// + type 6
		if (i + 1 < cells.length && test(cells[i + 1][j]) && i > 0 && test(cells[i
				- 1][j]) && j + 1 < cells[0].length && test(cells[i][j + 1]) && j > 0
				&& test(cells[i][j - 1]))
			type = 6;
		// _ type 7
		// |
		if (i + 1 < cells.length && test(cells[i + 1][j]) && !(i > 0 && test(cells[i
				- 1][j])) && j + 1 < cells[0].length && test(cells[i][j + 1]) && !(j > 0
						&& test(cells[i][j - 1])))
			type = 7;
		// _| type 8
		if (!(i + 1 < cells.length && test(cells[i + 1][j])) && i > 0 && test(cells[i
				- 1][j]) && !(j + 1 < cells[0].length && test(cells[i][j + 1])) && j > 0
				&& test(cells[i][j - 1]))
			type = 8;
		// |_ type 9
		if (!(i + 1 < cells.length && test(cells[i + 1][j])) && i > 0 && test(cells[i
				- 1][j]) && j + 1 < cells[0].length && test(cells[i][j + 1]) && !(j > 0
						&& test(cells[i][j - 1])))
			type = 9;
		// _ type 10
		// |
		if (i + 1 < cells.length && test(cells[i + 1][j]) && !(i > 0 && test(cells[i
				- 1][j])) && !(j + 1 < cells[0].length && test(cells[i][j + 1])) && j > 0
				&& test(cells[i][j - 1]))
			type = 10;
		return type;
	}

	protected String getObstaclePicPath() {
		return obstaclePicPath;
	}
}