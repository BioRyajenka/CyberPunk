package com.jackson.cyberpunk.level;

public class WallView extends ObstacleView {
	public WallView(Wall w) {
		super(w);

		int type = w.getObstaclePositionType();
		if (type == 1 || type == 2 || type == 3 || type == 9) {
			obstacleSprite.flipHorizontally();
		}
	}
}
