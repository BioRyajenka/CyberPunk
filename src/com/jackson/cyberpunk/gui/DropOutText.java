package com.jackson.cyberpunk.gui;

import org.newdawn.slick.UnicodeFont;

import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Rectangle;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Text;

public class DropOutText extends Entity {
	private Sprite bg;
	private Text text;
	private Rectangle parent;

	public DropOutText(Rectangle parent, boolean toDown) {
		this.parent = parent;

		bg = new Sprite(0, 0, "res/gui/dark_bg") {
			@Override
			public void setSize(float pWidth, float pHeight) {
				super.setSize(pWidth, pHeight);
				float x = DropOutText.this.getGlobalX();
				float y = DropOutText.this.getGlobalY();
				if (toDown) {
					bg.setPosition(x + parent.getWidth() + 1, y + parent.getHeight()
							- getHeight());
				} else {
					bg.setPosition(x + parent.getWidth() + 1, y);
				}
			}
		};
		MyScene.dropOutsLayer.attachChild(bg);
		bg.setSize(150, 40);

		text = new Text(5, 5, "00") {
			@Override
			public void setText(String newText) {
				super.setText(newText);
				float newWidth;
				float newHeight;
				newWidth = Math.max(150, getWidth() + 10);
				newHeight = Math.max(40, getHeight() + 10);
				bg.setSize(newWidth, newHeight);
			}
		};
		text.setColor(.8f, .8f, .8f, 1f);
		bg.attachChild(text);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		bg.setSize(bg.getWidth(), bg.getHeight());
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		float mx = MyScene.mx, my = MyScene.my;
		if (parent.isSelected(mx, my)) {
			bg.show();
		}
		if (!parent.isSelected(mx, my) && !bg.isSelected(mx, my)) {
			bg.hide();
		}
	}

	public String getText() {
		return text.getText();
	}

	public UnicodeFont getFont() {
		return text.getFont();
	}
}