package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Sprite;

public class FloorView extends CellView {
	private Sprite bagSprite;
	
	public FloorView(Floor f) {
		super(f);
		Sprite sprite = new Sprite(0, 0, f.getFloorPicPath() + "/0");
    	attachChild(sprite);
	}
	
	@Override
	public void onManagedUpdate() {
		Floor f = (Floor) cell;
		Player p = Game.player;
		if (!f.getLoot().getItems().isEmpty()) {
			getBagSprite().show();
			if (p.getI() == f.getI() && p.getJ() == f.getJ()) {
				getBagSprite().setAlpha(.6f);
			} else {
				getBagSprite().setAlpha(1f);
			}
		} else {
			getBagSprite().hide();
		}
		super.onManagedUpdate();
	}
	
	private Sprite getBagSprite() {
		if (bagSprite == null) {
			bagSprite = new Sprite(22, -15, "res/items/floor_bag");
			attachChild(bagSprite);
		}
		return bagSprite;
	}
	
	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		if (bagSprite != null) {
			bagSprite.setColor(1f, 1f, 1f, 1f);
		}
	}
}
