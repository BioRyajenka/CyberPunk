package com.jackson.cyberpunk;

import java.util.LinkedList;

import com.jackson.myengine.Entity;
import com.jackson.myengine.Text;

public class GameLogView extends Entity {
	private static final int N = 5;
	private LinkedList<Text> textEntityList;

	public GameLogView() {
		textEntityList = new LinkedList<Text>();
		for (int i = 0; i < N; i++) {
			textEntityList.add(new Text(20, 440 - i * (Text.getDefaultFont().getLineHeight()
					- 1), ""));
			attachChild(textEntityList.getLast());
		}
		setColor(1f, 0f, 0f);
		for (int i = 0; i < N; i++)
			textEntityList.get(i).setAlpha(1f - .6f / N * i);
	}

	public void update() {
		LinkedList<String> log = GameLog.getLog();
		for (int i = 0; i < 5; i++) {
			Text te = textEntityList.get(i);
			te.setText(log.get(log.size() - i - 1));
		}
	}
}