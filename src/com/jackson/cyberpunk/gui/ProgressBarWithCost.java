package com.jackson.cyberpunk.gui;

import com.jackson.myengine.CycledSprite;

public class ProgressBarWithCost extends CycledProgressBar {
	private CycledSprite additionalSprite;

	public ProgressBarWithCost(float pX, float pY, String dropOutTextPrefix,
			String fillMainImagePath) {
		super(pX, pY, dropOutTextPrefix, fillMainImagePath, 0);
		additionalSprite = new CycledSprite(0, 0, fillMainImagePath);
		attachChild(additionalSprite);
	}

	public void update(float val, float cost) {
		if (cost > val) {
			update(0);
			additionalSprite.setWidth(calcWidth(cost));
			//don't know how to implement color mapping, so just creating
			//images for each color
			additionalSprite.setImage("res/gui/progressbar_unit_red");
			additionalSprite.setAlpha(progressBar.getAlpha());
			additionalSprite.setPosition(0, 0);
			additionalSprite.setStartPosXY(0, 0);
		} else {
			float newVal = val - cost;
			update(newVal);
			float pbWidth = calcWidth(val) - calcWidth(cost);
			progressBar.setWidth(pbWidth);
			additionalSprite.setPosition(pbWidth, 0);
			float newWidth = calcWidth(cost);
			additionalSprite.setWidth(newWidth);
			additionalSprite.setImage("res/gui/progressbar_unit_green");
			additionalSprite.setAlpha(0.5f * progressBar.getAlpha());
			float shift = progressBar.getWidth() - ((int) newVal) * getUnitSize() - 1;
			// shift < 0 when AP == 0
			if (shift < 0) {
				shift = 0;
			}
			additionalSprite.setStartPosXY(shift, 0);
		}
	}
}