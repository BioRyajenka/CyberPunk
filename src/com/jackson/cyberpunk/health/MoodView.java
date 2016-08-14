package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.gui.DropOutText;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Rectangle;
import com.jackson.myengine.Sprite;

public class MoodView extends Entity implements Rectangle {
	private static MoodView instance = new MoodView(12, 59);
	
	public static MoodView getInstance() {
		return instance;
	}
	
	private Sprite bg;
	private DropOutText dropOutText;
	
	private MoodView(float x, float y) {
		super(x, y);
		bg = new Sprite(0, 0, "res/gui/neutral_mood");
		dropOutText = new DropOutText(bg, false);
		attachChildren(bg, dropOutText);
	}
	
	@Override
	public void onManagedUpdate() {
		float mood = Game.player.getHealthSystem().getMood();
		if (mood > 60f) {
			bg.setImage("res/gui/good_mood");
			dropOutText.setText("Приподнятое настроение");
		} else if (mood > -60f) {
			bg.setImage("res/gui/neutral_mood");
			dropOutText.setText("Спокойное настроение");
		} else {
			bg.setImage("res/gui/bad_mood");
			dropOutText.setText("Плохое настроение");
		}
		dropOutText.setText(dropOutText.getText() + "\n" + mood + "/100");
		super.onManagedUpdate();
	}

	@Override
	public float getWidth() {
		return bg.getWidth();
	}

	@Override
	public float getHeight() {
		return bg.getHeight();
	}
}
