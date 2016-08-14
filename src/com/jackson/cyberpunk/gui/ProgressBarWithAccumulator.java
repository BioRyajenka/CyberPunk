package com.jackson.cyberpunk.gui;

import com.jackson.myengine.CycledSprite;

public class ProgressBarWithAccumulator extends CycledProgressBar {
	private CycledSprite accumulatorSprite;

	public ProgressBarWithAccumulator(float pX, float pY, String dropOutTextPrefix,
			String fillMainImagePath) {
		super(pX, pY, dropOutTextPrefix, fillMainImagePath, 0);
		accumulatorSprite = new CycledSprite(0, 0, "res/gui/progressbar_unit_yellow");
		accumulatorSprite.setColor(1f, .95f, 0);
		attachChild(accumulatorSprite);
	}
	
	@Deprecated
	public void update(float val) {
		super.update(val);
	}
	
	public void update(float val, float acc) {
		super.update(val + acc); // just for updating description
		float newWidth = calcWidth(val);
		progressBar.setWidth(newWidth);
		float shift = newWidth - ((int) val) * getUnitSize();
		accumulatorSprite.setStartPosXY(shift, 0);
		accumulatorSprite.setWidth(calcWidth(acc));
		//accumulatorSprite.setWidth(0);
		accumulatorSprite.setPosition(progressBar.getWidth(), 0);
	}
}
