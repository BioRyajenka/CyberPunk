package com.jackson.cyberpunk;

import com.jackson.cyberpunk.gui.CycledProgressBar;
import com.jackson.cyberpunk.gui.ProgressBar;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;

public class ActionPointsView extends Entity {
	private static ActionPointsView instance;

	private ProgressBar armAP, legAP;

	private ActionPointsView() {
		attachChild(new Sprite(12, 61, "res/gui/arm_action_point"));
		attachChild(new Sprite(12, 76, "res/gui/leg_action_point"));

		Player pl = Game.player;
		
		armAP = new CycledProgressBar(26, 61, "ОД взаимодействия", "res/gui/progressbar_unit", pl
				.getLeftArmActionPoints());
		legAP = new CycledProgressBar(26, 76, "ОД передвижения", "res/gui/progressbar_unit", pl
				.getLeftLegActionPoints());

		attachChildren(armAP, legAP);
	}

	@Override
	public void onManagedUpdate() {
		Player pl = Game.player;
		armAP.update(pl.getLeftArmActionPoints());
		legAP.update(pl.getLeftLegActionPoints());
		super.onManagedUpdate();
	}

	public static ActionPointsView getInstance() {
		if (instance == null) {
			instance = new ActionPointsView();
		}
		return instance;
	}
}
