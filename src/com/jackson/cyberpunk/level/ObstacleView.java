package com.jackson.cyberpunk.level;

import com.jackson.myengine.Sprite;

public class ObstacleView extends FloorView {
	protected Sprite obstacleSprite;

	public ObstacleView(Obstacle o) {
		super(o);
		obstacleSprite = new Sprite(0, -2 * (HEIGHT + 2), o.getObstaclePicPath());
		attachChild(obstacleSprite);
	}
	
	public Sprite getObstacleSprite() {
		return obstacleSprite;
	}
}
