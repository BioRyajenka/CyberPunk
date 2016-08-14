package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.Game;
import com.jackson.myengine.Sprite;

public class StationView extends WallView {
	private Sprite stationSprite;

	public StationView(RepairStation st) {
		this((Station) st);
	}

	// don't know why this constructor can't be invoked
	public StationView(Station st) {
		super(st);
		stationSprite = new Sprite(-24, -2 * (HEIGHT + 2) + HEIGHT / 2, st
				.getStationPicName());
		int posType = st.getObstaclePositionType();
		if (posType == 1 || posType == 3) {
			stationSprite.flipHorizontally();
			// :/
			stationSprite.setPosition(stationSprite.getX() + 48, stationSprite.getY());
		}
		attachChild(stationSprite);
	}
	
	@Override
	public void onManagedUpdate() {
		Cell[][] cells = Game.level.getCells();
		Station rs = (Station) cell;

		//stationSprite.setImage(rs.getStationPicName());
		//refreshSpritePosition();

		int posI = cell.posI;
		int posJ = cell.posJ;
		int posType = rs.getObstaclePositionType();
		if (posType == 0 || posType == 5) {
			stationSprite.setVisible(cells[posI + 1][posJ].isVisibleForPlayer());
		}
		if (posType == 1 || posType == 3) {
			stationSprite.setVisible(cells[posI][posJ + 1].isVisibleForPlayer());
		}
		super.onManagedUpdate();
	}
}
