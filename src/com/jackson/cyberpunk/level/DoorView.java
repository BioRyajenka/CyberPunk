package com.jackson.cyberpunk.level;

public class DoorView extends ObstacleView {
	private boolean prevIsOpened;
	
	public DoorView(Door d) {
		super(d);
		prevIsOpened = d.isOpened();
	}
	
	@Override
	public void onManagedUpdate() {
		Door d = (Door) cell;
		if (d.isOpened() != prevIsOpened) {
			prevIsOpened = d.isOpened();
			obstacleSprite.setImage(d.getObstaclePicPath());
		}
		super.onManagedUpdate();
	}
}
