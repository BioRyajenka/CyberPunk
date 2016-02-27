package com.jackson.myengine;

import java.util.LinkedList;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.jackson.cyberpunk.Game;

public class Engine extends BasicGame {
	private Scene mScene;
	private Input input;
	private LinkedList<Runnable> toRun;
	private LinkedList<Entity> toDetach;

	public Engine(String gamename, Scene scene) {
		super(gamename);
		mScene = scene;
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		toRun = new LinkedList<Runnable>();
		toDetach = new LinkedList<Entity>();
		input = gc.getInput();
		input.enableKeyRepeat();
		Game.init();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		mScene.onManagedUpdate();
		for (Runnable r : toRun) {
			r.run();
		}
		toRun.clear();

		for (Entity e : toDetach) {
			e.detachSelf();
		}
		toDetach.clear();
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		mScene.draw();
	}

	public void runOnUIThread(Runnable r) {
		toRun.add(r);
	}

	public void detachOnUIThread(Entity e) {
		toDetach.add(e);
	}

	public Input getInput() {
		return input;
	}
}