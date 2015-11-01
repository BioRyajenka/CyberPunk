package com.jackson.cyberpunk.level;

public abstract class Obstacle extends Floor {
	private String obstaclePicPath;

	public Obstacle(int posI, int posJ, String floorPicName, String obstaclePicName,
			Class<? extends CellView> viewClass) {
		super(posI, posJ, floorPicName);
		this.viewClass = viewClass;
		this.obstaclePicPath = "level/" + obstaclePicName;
		this.isPassable = false;
	}

	protected String getObstaclePicPath() {
		return obstaclePicPath;
	}
}