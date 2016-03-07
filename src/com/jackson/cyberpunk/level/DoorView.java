package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Sprite;

public class DoorView extends ObstacleView {
	public static Sprite temp = null;
	
	private boolean prevIsOpened;
	private Sprite keyMark;

	public DoorView(Door d) {
		super(d);
		prevIsOpened = false;

		keyMark = new Sprite(0, -2 * (HEIGHT + 2), "res/level/doors/stone/key_mark_closed") {
			@Override
			public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
				super.setColor(pRed, pGreen, pBlue, 0);
			}
			
			@Override
			public String toString() {
				return "keyMark";
			}
			
			@Override
			public void draw() {
				//Log.d("PIZDEC: " + mAlpha + ", " + getImage().getOptions().mAlpha);
				//getImage().setColor(mRed, mGreen, mBlue, 0.00001f);
				//if (mAlpha != 0) {
					super.draw();
				//}
			}
		};
		
		temp = keyMark;
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
		keyMark.setColor(mRed * type.getRed(), mGreen * type.getGreen(), mBlue
				* type.getBlue(), mAlpha * type.getAlpha());
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
		if (d.isOpened() != prevIsOpened) {
			prevIsOpened = d.isOpened();
			obstacleSprite.setImage(d.getObstaclePicPath());
			keyMark.setImage("res/level/doors/stone/key_mark_" + (d.isOpened() ? "opened"
					: "closed"));
			updateRotation();
		}
		super.onManagedUpdate();
	}
}
