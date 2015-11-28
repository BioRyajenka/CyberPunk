package com.jackson.cyberpunk;

import com.jackson.cyberpunk.gui.Button;
import com.jackson.cyberpunk.gui.Panel;
import com.jackson.myengine.Text;

public class Message extends Panel {
	private static Message singleton = null;

	private Text text;
	private Button ok;

	private Message() {
		super(0, 0, 1, 1);
		this.text = new Text(25, 15, "");
		ok = new Button(0, 0, "Ok");
		ok.setAction(new Runnable() {
			@Override
			public void run() {
				hide();
				MyScene.isSceneBlocked = false;
			}
		});
		attachChildren(this.text, ok);
	}

	public void setOkAction(Runnable action) {
		this.ok.setAction(new Runnable() {
			@Override
			public void run() {
				if (action != null) {
					action.run();
				}
				hide();
				MyScene.isSceneBlocked = false;
			}
		});
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	@Deprecated
	public void show() {
	}

	public void show(String text, float x, float y, float width, float height) {
		super.show();
		setPosition(x, y);
		this.text.setText(text);
		setSize(width, height);
		ok.setPosition((width - ok.getWidth()) / 2, height - ok.getHeight() - 10);
		setOkAction(null);
	}

	public static Message getInstance() {
		if (singleton == null) {
			singleton = new Message();
		}
		return singleton;
	}
}