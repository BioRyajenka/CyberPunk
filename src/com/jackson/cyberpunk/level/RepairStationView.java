package com.jackson.cyberpunk.level;

import com.jackson.myengine.Sprite;

public class RepairStationView extends WallView {
	public RepairStationView(RepairStation o) {
		super(o);
		Sprite stationSprite = new Sprite(-24, -2 * (HEIGHT + 2) + HEIGHT / 2,
				"res/level/environment/repair_station");
		if (o.getObstaclePositionType() == 1) {
			stationSprite.flipHorizontally();
			// :/
			stationSprite.setPosition(stationSprite.getX() + 48, stationSprite.getY());
		}
		attachChild(stationSprite);
	}
}
