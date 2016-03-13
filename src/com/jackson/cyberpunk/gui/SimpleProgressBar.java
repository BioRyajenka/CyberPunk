package com.jackson.cyberpunk.gui;

import com.jackson.myengine.Sprite;

public class SimpleProgressBar extends ProgressBar {
	public SimpleProgressBar(float pX, float pY, String dropOutTextPrefix, String fillImagePath,
			float maxValue) {
		super(pX, pY, dropOutTextPrefix, new Sprite(0, 0, fillImagePath), maxValue);
	}

	@Override
	public void update(float val) {
		progressBar.setSize(getWidth() * val / maxValue, progressBar.getHeight());
		super.update(val);
	}
}