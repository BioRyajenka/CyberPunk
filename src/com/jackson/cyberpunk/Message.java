package com.jackson.cyberpunk;

import org.newdawn.slick.UnicodeFont;

import com.jackson.cyberpunk.gui.Button;
import com.jackson.cyberpunk.gui.Panel;
import com.jackson.myengine.Text;

public class Message extends Panel {
	private static Message singleton = null;

	private Text text;
	private Button ok, cancel;

	private Message() {
		super(0, 0, 1, 1);
		this.text = new Text(25, 15, "");
		ok = new Button(0, 0, "Ok");
		cancel = new Button(0, 0, "Отмена");
		attachChildren(this.text, ok, cancel);
	}

	private void setOkAction(Runnable action) {
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

	@Deprecated
	public void show() {
	}

	private void show(String text, Runnable okAction, float x, float y, float width,
			float height) {
		super.show();
		setPosition(x, y);
		this.text.setText(text);
		setSize(width, height);
		setOkAction(okAction);
		final float buttonsTop = height - ok.getHeight() - 10;
		if (okAction != null) {
			ok.setPosition(width / 3 - ok.getWidth() / 2, buttonsTop);
			cancel.setPosition(2 * width / 3 - ok.getWidth() / 2, buttonsTop);
			cancel.setVisible(true);
		} else {
			ok.setPosition(width / 2 - ok.getWidth() / 2, buttonsTop);
			cancel.setVisible(false);
		}
	}
	
	public static void showMessage(String text, Runnable okAction) {
		getInstance().show(text, okAction);
	}

	private void show(String text, Runnable okAction) {
		UnicodeFont font = Text.getDefaultFont();
		int width = font.getWidth(text) + 150;
		int height = 175;
		int x = (Game.SCREEN_WIDTH - width) / 2;
		int y = (Game.SCREEN_HEIGHT - height) / 2;
		show(text, okAction, x, y, width, height);
		MyScene.isSceneBlocked = true;
	}

	public static Message getInstance() {
		if (singleton == null) {
			singleton = new Message();
		}
		return singleton;
	}
}