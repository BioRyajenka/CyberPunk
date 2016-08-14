package com.jackson.cyberpunk;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.jackson.cyberpunk.item.ItemManager;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Engine;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Log;

public class Game {
	public static final int SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480, TARGET_FPS = 60;

	public static enum Mode {
		EXPLORE, FIGHT
	};

	private static AppGameContainer appgc;

	public static Engine engine;
	public static MyScene scene;

	public static Level level;
	public static Player player;

	private static Mode gameMode;

	public static void switchShowFPS() {
		appgc.setShowFPS(!appgc.isShowingFPS());
	}

	public static void main(String[] args) {
		try {
			appgc = new AppGameContainer(engine = new Engine("CyberPunk",
					scene = new MyScene()));
			appgc.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
			appgc.setTargetFrameRate(TARGET_FPS - 1);

			appgc.setShowFPS(false);
			appgc.start();
			Log.d("This code won't be executed");
		} catch (SlickException e) {
			Log.e(e.toString());
		}
	}

	public static void init() {
		GameLog.init();
		ItemManager.init();
		player = new Player();
		level = new Level();
		scene.init();
		setGameMode(Mode.EXPLORE);
		doMobsSteps();
	}

	public static void doMobsSteps() {
		for (Entity e : Game.level.mobs_not_views.getChildren()) {
			Mob m = ((Mob) e);
			if (m instanceof NPC) {
				if (((NPC) m).doLogic()) {
					m.getHealthSystem().update();
				}
			}
		}
	}

	public static void setGameMode(Mode gameMode) {
		if (Game.gameMode == gameMode) {
			return;
		}
		Game.gameMode = gameMode;
		if (gameMode == Mode.EXPLORE) {
			MyScene.gameModeText.hide();
			MyScene.endTurnButton.hide();
			player.refreshLeftActionPointsAndTurn();
			GameLog.add("Ты перешел в режим исследования");
		} else {
			player.resetLongTermTarget();
			for (Entity e : Game.level.mobs_not_views.getChildren()) {
				Mob m = ((Mob) e);
				m.refreshLeftActionPointsAndTurn();
			}
			MyScene.gameModeText.show();
			MyScene.endTurnButton.show();
			MyScene.gameModeText.setText("FIGHT MODE");
			GameLog.add("Ты перешел в боевой режим");
		}
	}

	public static Mode getGameMode() {
		return gameMode;
	}

	public static void terminate() {
		appgc.exit();
	}
}