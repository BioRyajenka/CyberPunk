package com.jackson.cyberpunk.behavior;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.myengine.Utils;

public class WanderBehavior extends Behavior {
	private static Behavior singleton = new WanderBehavior();

	private WanderBehavior() {
	}

	@Override
	public void doLogic(NPC handler) {
		if (handler.isSeeMob(Game.player)) {
			Game.setGameMode(Mode.FIGHT);
			handler.setBehavior(AgressiveBehavior.getInstance());
			return;
		}
		int i = handler.getI(), j = handler.getJ(), ni = i, nj = j;
		Cell[][] cells = Game.level.getCells();
		do {
			int dice = Utils.rand.nextInt(4);
			switch (dice) {
			case 0:
				ni = i - 1;
				nj = j;
				break;
			case 1:
				ni = i + 1;
				nj = j;
				break;
			case 2:
				nj = j - 1;
				ni = i;
				break;
			case 3:
				nj = j + 1;
				ni = i;
				break;
			}
		} while (!(Utils.inBounds(ni, 0, cells.length - 1) && Utils.inBounds(nj, 0,
				cells[0].length - 1) && cells[ni][nj].isPassable() && cells[ni][nj]
						.getMob() == null));
		handler.moveToPos(ni, nj);
	}

	public static Behavior getInstance() {
		return singleton;
	}
}