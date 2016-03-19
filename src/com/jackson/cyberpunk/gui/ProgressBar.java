package com.jackson.cyberpunk.gui;

import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Rectangle;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Text;

public abstract class ProgressBar extends Entity implements Rectangle {
	protected Sprite progressBar;
	protected Sprite dropOut;
	protected Sprite backGround;
	protected Text descriptionText;
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
	 * <br><b>contract: </b> dropOut is guarantee to be last child
	 */
	public ProgressBar(float pX, float pY, String dropOutTextPrefix, Sprite progressBar,
			float maxValue) {
		super(pX, pY);
		this.dropOutTextPrefix = dropOutTextPrefix;
		this.progressBar = progressBar;

		backGround = new Sprite(0, 0, "res/gui/progressbar_bg");

		dropOut = new Sprite(0, 0, "res/gui/dark_bg") {
			@Override
			public void setSize(float pWidth, float pHeight) {
				super.setSize(pWidth, pHeight);
				dropOut.setPosition(ProgressBar.this.getWidth() + 1, ProgressBar.this
						.getHeight() - getHeight());
			}
		};
		dropOut.setSize(150, 40);

		descriptionText = new Text(5, 5, "00") {
			@Override
			public void setText(String newText) {
				super.setText(newText);
				float newWidth;
				float newHeight;
				newWidth = Math.max(150, getWidth() + 10);
				newHeight = Math.max(40, getHeight() + 10);
				dropOut.setSize(newWidth, newHeight);
			}
		};
		descriptionText.setColor(.8f, .8f, .8f, 1f);
		dropOut.attachChild(descriptionText);

		attachChildren(backGround, progressBar, dropOut);

		setAlpha(.85f);
		setMaxValue(maxValue);
		update(maxValue);
	}
	
	public void update(float val) {
		descriptionText.setText(dropOutTextPrefix + ": " + val + "/" + maxValue);
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (!isVisible()) {
			return;
		}
		float mx = MyScene.mx, my = MyScene.my;
		if (isSelected(mx, my)) {
			dropOut.show();
		}
		if (!isSelected(mx, my) && !dropOut.isSelected(mx, my)) {
			dropOut.hide();
		}
	}
}