package com.jackson.cyberpunk;

import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;

import com.jackson.cyberpunk.gui.Button;
import com.jackson.cyberpunk.level.CellView;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.level.LevelView;
import com.jackson.cyberpunk.mob.MobView;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Log;
import com.jackson.myengine.Scene;
import com.jackson.myengine.Text;
import com.jackson.myengine.Text.Align;

public class MyScene extends Scene {
	public static boolean wallView;
	
	public static float mx, my;
	public static boolean isLeftPressed, isRightPressed, isLeftDown, isMidDown;

	public static boolean isSceneBlocked;
	private static float px, py;
	public static LevelView levelView;

	public static Text gameModeText;
	public static Button endTurnButton;

	@Override
	public void init() {
		Log.d("Initializing scene");
		levelView = Game.level.getView();

		InventoryWindow inventoryWindow = InventoryWindow.getInstance();
		inventoryWindow.hide();

		gameModeText = new Text(Game.SCREEN_WIDTH / 2, 40, "");
		gameModeText.setAlign(Align.CENTER);
		gameModeText.setColor(1f, .3f, .3f);
		gameModeText.setFont("Verdana", 20);

		Message.getInstance().hide();
		
		endTurnButton = new Button(9, 96, "", "res/gui/end_turn_button");
		endTurnButton.setAction(new Runnable() {
			@Override
			public void run() {
				Game.player.finishTurn();
			}
		});

		attachChildren(levelView, gameModeText, LogText.getView(), endTurnButton,
				Game.player.getHealthSystem().getView(), inventoryWindow,
				ActionPointsView.getInstance(), ContextMenuView.getInstance(), Message
						.getInstance(), DropOutView.getInstance());

		isSceneBlocked = false;

		Player pl = Game.player;
		MobView pw = pl.getView();
		CellView pcw = Game.level.getCells()[pl.getI()][pl.getJ()].getView();
		Game.level.getView().setPosition((int) (Game.SCREEN_WIDTH / 2 - pcw.getX() - pw
				.getX() - pw.getWidth() / 2), (int) (Game.SCREEN_HEIGHT / 2 - pcw.getY()
						- pw.getY() - pw.getHeight() / 2));

		Log.d("finish initializing scene");
	}

	@Override
	public void onManagedUpdate() {
		Level level = Game.level;
		LevelView lv = level.getView();
		Input in = Game.engine.getInput();

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

		// utility
		if (in.isKeyPressed(Input.KEY_F1)) {
			wallView = !wallView;
		}
		if (in.isKeyPressed(Input.KEY_F3)) {
			Game.switchShowFPS();
		}

		// updating player and mobs
		if (!isSceneBlocked)
			level.mobs_not_views.onManagedUpdate();

		// move map
		if (isMidDown && !isSceneBlocked)
			lv.setPosition(lv.getX() + mx - px, lv.getY() + my - py);
		float moveSp = 2;
		if (in.isKeyDown(Input.KEY_LEFT)) {
			lv.setPosition(lv.getX() + moveSp, lv.getY());
		}
		if (in.isKeyDown(Input.KEY_RIGHT)) {
			lv.setPosition(lv.getX() - moveSp, lv.getY());
		}
		if (in.isKeyDown(Input.KEY_UP)) {
			lv.setPosition(lv.getX(), lv.getY() + moveSp);
		}
		if (in.isKeyDown(Input.KEY_DOWN)) {
			lv.setPosition(lv.getX(), lv.getY() - moveSp);
		}

		if (in.isKeyPressed(Input.KEY_I) || in.isKeyPressed(Input.KEY_E)) {
			InventoryWindow iw = InventoryWindow.getInstance();
			if (isSceneBlocked) {
				if (iw.isVisible()) {
					iw.hide();
					ContextMenuView.getInstance().hide();
				}
			} else {
				iw.show();
				ContextMenuView.getInstance().hide();
			}
		}

		px = mx;
		py = my;

		super.onManagedUpdate();
	}

	public static Message newMessage(String text) {
		UnicodeFont font = Text.getDefaultFont();
		int width = font.getWidth(text) + 150;
		int height = 175;
		int x = (Game.SCREEN_WIDTH - width) / 2;
		int y = (Game.SCREEN_HEIGHT - height) / 2;
		Message.getInstance().show(text, x, y, width, height);
		MyScene.isSceneBlocked = true;
		return Message.getInstance();
	}

	@Override
	public String toString() {
		return "MyScene";
	}
}