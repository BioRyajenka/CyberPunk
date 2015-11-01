package com.jackson.cyberpunk;

import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;

import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.level.LevelView;
import com.jackson.cyberpunk.windows.InventoryWindow;
import com.jackson.cyberpunk.windows.Message;
import com.jackson.myengine.Log;
import com.jackson.myengine.Scene;
import com.jackson.myengine.Text;
import com.jackson.myengine.Text.Align;

public class MyScene extends Scene {
	public static float mx, my;
	public static boolean isLeftPressed, isRightPressed, isLeftDown, isMidDown;

	public static boolean isSceneBlocked;
	private static float px, py;
	public static LevelView levelView;

	public static InventoryWindow inventoryWindow;

	public static Text gameModeText;

	@Override
	public void init() {
		Log.d("Initializing scene");
		levelView = Game.level.getView();

		inventoryWindow = new InventoryWindow();
		inventoryWindow.hide();

		gameModeText = new Text(Game.SCREEN_WIDTH / 2, 40, "");
		gameModeText.setAlign(Align.CENTER);
		gameModeText.setColor(1f, .3f, .3f);
		gameModeText.setFont("Verdana", 20);

		attachChildren(levelView, gameModeText, LogText.getView(), Game.player
				.getHealthSystem().getView(), inventoryWindow, ContextMenuView
						.getInstance());

		isSceneBlocked = false;
	}

	@Override
	public void onManagedUpdate() {
		Level level = Game.level;
		LevelView lv = level.getView();
		Input in = Game.engine.getInput();
		// Player pl = Game.player;
		// Cell pc = level.getCells()[player.getI()][player.getJ()];

		mx = in.getMouseX();
		my = in.getMouseY();
		isLeftPressed = in.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		isRightPressed = in.isMousePressed(Input.MOUSE_RIGHT_BUTTON);
		isLeftDown = in.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		isMidDown = in.isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON);

		if (levelView == null) {
			super.onManagedUpdate();
			return;
		}

		// updating player and mobs
		if (!isSceneBlocked)
			level.mobs_not_views.onManagedUpdate();

		// move map
		if (isMidDown && !isSceneBlocked)
			lv.setPosition(lv.getX() + mx - px, lv.getY() + my - py);
		float moveSp = 2;
		if (in.isKeyDown(Input.KEY_LEFT))
			lv.setPosition(lv.getX() + moveSp, lv.getY());
		if (in.isKeyDown(Input.KEY_RIGHT))
			lv.setPosition(lv.getX() - moveSp, lv.getY());
		if (in.isKeyDown(Input.KEY_UP))
			lv.setPosition(lv.getX(), lv.getY() + moveSp);
		if (in.isKeyDown(Input.KEY_DOWN))
			lv.setPosition(lv.getX(), lv.getY() - moveSp);

		if (in.isKeyPressed(Input.KEY_I))
			if (isSceneBlocked) {
				if (inventoryWindow.isVisible())
					inventoryWindow.hide();
			} else {
				inventoryWindow.show();
			}

		px = mx;
		py = my;

		super.onManagedUpdate();
	}

	public static Message newMessage(String text) {
		UnicodeFont font = Text.getDefaultFont();
		int width = font.getWidth(text) + 150, height = 175, x = (Game.SCREEN_WIDTH
				- width) / 2, y = (Game.SCREEN_HEIGHT - height) / 2;
		Message mes = new Message(text, x, y, width, height);
		Game.engine.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				Game.scene.attachChild(mes);
			}
		});
		MyScene.isSceneBlocked = true;
		return mes;
	}
}