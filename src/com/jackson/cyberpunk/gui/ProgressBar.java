package com.jackson.cyberpunk.gui;

import com.jackson.myengine.Entity;
import com.jackson.myengine.Rectangle;
import com.jackson.myengine.Sprite;

public abstract class ProgressBar extends Entity implements Rectangle {
	protected Sprite progressBar;
	protected Sprite backGround;
	protected DropOutText dropOutText;
	private String dropOutTextPrefix;

	protected float maxValue;

	public float getWidth() {
		return backGround.getWidth();
	}

	public float getHeight() {
		return backGround.getHeight();
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * just loading backGround and progressBar and don't modifying it's sizes
	 * 
	 * <br>
	 * <b>contract: </b> dropOut is guarantee to be last child
	 */
	public ProgressBar(float pX, float pY, String dropOutTextPrefix, Sprite progressBar,
			float maxValue) {
		super(pX, pY);
		this.dropOutTextPrefix = dropOutTextPrefix;
		this.progressBar = progressBar;

		backGround = new Sprite(0, 0, "res/gui/progressbar_bg");

		dropOutText = new DropOutText(this, false);

		attachChildren(backGround, progressBar, dropOutText);

		setMaxValue(maxValue);
		update(maxValue);
	}

	public void update(float val) {
		dropOutText.setText(dropOutTextPrefix + ": " + val + "/" + maxValue);
	}

	@Override
	public void onManagedUpdate() {
		if (!isVisible()) {
			return;
		}
		super.onManagedUpdate();
	}

	protected float getUnitSize() {
		return progressBar.getInitialWidth() - 1;
	}
}