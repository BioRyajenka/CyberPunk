package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Sprite;

public class DoorView extends ObstacleView {
	private Sprite keyMark;

	public DoorView(Door d) {
		super(d);
		keyMark = new Sprite(0, -2 * (HEIGHT + 2),
				"res/level/doors/stone/key_mark_closed");

		attachChild(keyMark);

		updateRotation();
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		updateMarkColor();
	}

	private void updateMarkColor() {
		LockType type = ((Door) cell).lockType;
		keyMark.setColor(red * type.getRed(), green * type.getGreen(), blue * type
				.getBlue(), alpha * type.getAlpha());
	}

	private void updateRotation() {
		int type = ((Door) cell).getObstaclePositionType();
		if (type == 1 || type == 2 || type == 3 || type == 9) {
			obstacleSprite.flipHorizontally();
			keyMark.flipHorizontally();
		}
		updateMarkColor();
	}

	@Override
	public void onManagedUpdate() {
		Door d = (Door) cell;
		obstacleSprite.setImage(d.getObstaclePicPath());
		keyMark.setImage("res/level/doors/stone/key_mark_" + (d.isOpened() || d
				.isBroken() ? "opened" : "closed"));
		updateRotation();
		super.onManagedUpdate();
	}
}
