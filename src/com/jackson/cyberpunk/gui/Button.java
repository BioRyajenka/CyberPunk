package com.jackson.cyberpunk.gui;

import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.IlluminatedSprite;
import com.jackson.myengine.Text;

public class Button extends IlluminatedSprite {
	private boolean isPressing;
	private Runnable action;
	private Text text;

	public Button(float pX, float pY, String text) {
		super(pX, pY, "gui/button", IlluminationMode.IMPOSITION);// SIMPLE
		// быстрее
		isPressing = false;
		action = null;
		this.text = new Text(0, 0, "");
		setText(text);
		attachChild(this.text);
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (!isGlobalVisible())
			return;
		float x = MyScene.mx, y = MyScene.my;

		if (!MyScene.isLeftDown && !MyScene.isLeftPressed && isPressing) {
			if (action != null && isSelected(x, y))
				action.run();
			isPressing = false;
			setBlackout(.3f);
		}

		if (isSelected(x, y) && MyScene.isLeftPressed) {
			isPressing = true;
			setBlackout(.6f);
		}
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