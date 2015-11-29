package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Sprite;

public class DoorView extends ObstacleView {
	private boolean prevIsOpened;
	private Sprite keyMark;

	public DoorView(Door d) {
		super(d);
		prevIsOpened = d.isOpened();

		keyMark = new Sprite(0, -2 * (HEIGHT + 2), "level/doors/stone/key_mark_closed");
		attachChild(keyMark);
		
		updateRotation();
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue) {
		super.setColor(pRed, pGreen, pBlue);
		updateMarkColor();
	}

	private void updateMarkColor() {
		LockType type = ((Door) cell).lockType;
		keyMark.setColor(mRed * type.getRed(), mGreen * type.getGreen(), mBlue
				* type.getBlue(), mAlpha * type.getAlpha());
	}

	private void updateRotation() {
		int type = ((Door) cell).getObstaclePositionType();
		if (type == 1 || type == 2 || type == 3 || type == 9) {
			obstacleSprite.flipHorizontally();
			keyMark.flipHorizontally();
		}
	}

	@Override
	public void onManagedUpdate() {
		Door d = (Door) cell;
		if (d.isOpened() != prevIsOpened) {
			prevIsOpened = d.isOpened();
			obstacleSprite.setImage(d.getObstaclePicPath());
			keyMark.setImage("level/doors/stone/key_mark_" + (d.isOpened() ? "opened"
					: "closed"));
			updateMarkColor();
			updateRotation();
		}
		super.onManagedUpdate();
	}
}
