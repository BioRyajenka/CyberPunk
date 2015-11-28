package com.jackson.cyberpunk;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.jackson.cyberpunk.health.InjuryManager;
import com.jackson.cyberpunk.health.PartsManager;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Engine;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IEntity;
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

	public static void main(String[] args) {
		try {
			appgc = new AppGameContainer(engine = new Engine("CyberPunk",
					scene = new MyScene()));
			appgc.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
			appgc.setTargetFrameRate(TARGET_FPS - 1);
			appgc.start();
			Log.d("This code won't be executed");
		} catch (SlickException e) {
			Log.e(e.toString());
		}
	}

	public static void init() {
		LogText.init();
		PartsManager.init();
		InjuryManager.init();
		player = new Player();
		level = new Level();
		scene.init();
		setGameMode(Mode.EXPLORE);
		doMobsSteps();
	}

	public static void doMobsSteps() {
		Entity mobs = level.mobs_not_views;
		for (int i = 0; i < mobs.getChildCount(); i++) {
			IEntity e = mobs.getChild(i);
			Mob m = ((Mob) e);
			if (m instanceof NPC) {
				Log.d("NPC " + m.getName());
				((NPC) m).doLogic();
				m.getHealthSystem().update();
			}
		}
	}

	public static void setGameMode(Mode gameMode) {
		if (Game.gameMode == gameMode) {
			return;
		}
		Game.gameMode = gameMode;
		if (gameMode == Mode.EXPLORE) {
			//MyScene.gameModeText.setText("EXPLORE MODE");
			MyScene.gameModeText.hide();
			LogText.add("�� ������� � ����� ������������");
		} else {
			player.resetLongTermTarget();
			Entity mobs = level.mobs_not_views;
			for (int i = 0; i < mobs.getChildCount(); i++) {
				IEntity e = mobs.getChild(i);
				Mob m = ((Mob) e);
				m.refreshLeftActionPoints();
			}
			MyScene.gameModeText.show();
			MyScene.gameModeText.setText("FIGHT MODE");
			LogText.add("�� ������� � ������ �����");
		}
	}

	public static Mode getGameMode() {
		return gameMode;
	}

	public static void terminate() {
		appgc.exit();
	}
}