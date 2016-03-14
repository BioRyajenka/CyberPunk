package com.jackson.cyberpunk.gui;

import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.IlluminatedSprite;
import com.jackson.myengine.Text;

public class Button extends IlluminatedSprite {
	private boolean pressing;
	private boolean active;
	private Runnable action;
	private Text text;

	public Button(float pX, float pY, String text) {
		this(pX, pY, text, "res/gui/button");
	}
	
	public Button(float pX, float pY, String text, String imagePath) {
		super(pX, pY, imagePath, IlluminationMode.IMPOSITION);// SIMPLE
		// быстрее
		pressing = false;
		active = true;
		action = null;
		this.text = new Text(0, 0, "");
		setText(text);
		attachChild(this.text);
	}
	
	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (!isGlobalVisible() || !active) {
			return;
		}
		float x = MyScene.mx, y = MyScene.my;

		if (!MyScene.isLeftDown && !MyScene.isLeftPressed && pressing) {
			if (action != null && isSelected(x, y)) {
				action.run();
			}
			pressing = false;
			setBlackout(.3f);
		}

		if (isSelected(x, y) && MyScene.isLeftPressed) {
			pressing = true;
			setBlackout(.6f);
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void setImage(String path) {
		super.setImage(path);
		recalcTextPosition();
	}

	public Text getTextEntity() {
		return text;
	}

	@Override
	public void setSize(float pWidth, float pHeight) {
		super.setSize(pWidth, pHeight);
		recalcTextPosition();
	}
	
	public void setText(String text) {
		this.text.setText(text);
		recalcTextPosition();
	}
	
	public String getText() {
		return text.getText();
	}
	
	private void recalcTextPosition() {
		if (text != null) {
			text.setPosition((getWidth() - text.getWidth()) / 2, (getHeight() - text
					.getHeight()) / 2 - 1);
		}
	}

	public void setAction(Runnable action) {
		this.action = action;
	}
}