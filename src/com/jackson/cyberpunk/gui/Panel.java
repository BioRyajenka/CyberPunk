package com.jackson.cyberpunk.gui;

import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;

public class Panel extends Entity {
	private Sprite bg;

	public Panel(float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY);
		bg = new Sprite(0, 0, "gui/panel_bg");
		bg.setSize(pWidth, pHeight);
		attachChild(bg);

		onCreate();
	}

	protected void onCreate() {

	}

	public void setSize(float pWidth, float pHeight) {
		bg.setSize(pWidth, pHeight);
	}

	public float getWidth() {
		return bg.getWidth();
	}

	public float getHeight() {
		return bg.getHeight();
	}
}
