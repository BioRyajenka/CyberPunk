package com.jackson.cyberpunk.level;

public class Station extends Wall {
	protected String stationPicName;

	public Station(int posI, int posJ, String stationPicName, String floorPicName,
			String wallMaterial) {
		super(posI, posJ, floorPicName, wallMaterial, StationView.class);
		this.stationPicName = stationPicName;
	}
}
