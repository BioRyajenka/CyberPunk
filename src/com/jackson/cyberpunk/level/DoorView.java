package com.jackson.cyberpunk.level;

public class DoorView extends ObstacleView {
	private boolean prevIsOpened;
	
	public DoorView(Door d) {
		super(d);
		prevIsOpened = d.isOpened();
		
		updateRotation();
	}
	
	private void updateRotation() {
		int type = ((Door) cell).getObstaclePositionType();
		if (type == 1 || type == 2 || type == 3 || type == 9) {
			obstacleSprite.flipHorizontally();
		}
	}
	
	@Override
	public void onManagedUpdate() {
		Door d = (Door) cell;
		if (d.isOpened() != prevIsOpened) {
			prevIsOpened = d.isOpened();
			obstacleSprite.setImage(d.getObstaclePicPath());
			updateRotation();
		}
		super.onManagedUpdate();
	}
}
