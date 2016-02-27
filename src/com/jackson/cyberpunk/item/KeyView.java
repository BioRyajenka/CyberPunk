package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Sprite;

public class KeyView extends ItemView {
	private Sprite keyMark;
	
	public KeyView(Key key) {
		super(key);
		keyMark = new Sprite(0, 0, "res/items/keys/key_mark");
		attachChild(keyMark);
		updateMarkColor();
	}
	
	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		updateMarkColor();
	}
	
	private void updateMarkColor() {
		LockType type = ((Key) item).getKeyType();
		keyMark.setColor(mRed * type.getRed(), mGreen * type.getGreen(), mBlue
				* type.getBlue(), mAlpha * type.getAlpha());
	}
}
