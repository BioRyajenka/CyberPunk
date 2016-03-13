package com.jackson.cyberpunk.gui;

import com.jackson.myengine.CycledSprite;

public class CycledProgressBar extends ProgressBar {
	public CycledProgressBar(float pX, float pY, String dropOutTextPrefix,
			String fillImagePath, float maxValue) {
		super(pX, pY, dropOutTextPrefix, new CycledSprite(0, 0, fillImagePath),
				maxValue);
	}

	private float calcWidth(float val) {
		if (val <= 1) {
			return progressBar.getInitialWidth() * val;
		}
		return (progressBar.getInitialWidth() - 1) * val + 1;
	}

	@Override
	public void setMaxValue(float maxValue) {
		super.setMaxValue(maxValue);
		// assuming 'progressBar' mathes value 1
		backGround.setSize(calcWidth(maxValue), progressBar.getHeight());
	}

	@Override
	public void update(float val) {
		progressBar.setSize(calcWidth(val), progressBar.getHeight());
		super.update(val);
	}
}