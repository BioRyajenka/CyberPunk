package com.jackson.cyberpunk.mob.behavior;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.myengine.Utils;

public class WanderBehavior extends Behavior {
	@Override
	public void doLogic(NPC handler) {
		if (handler.isSeeMob(Game.player)) {
			Game.setGameMode(Mode.FIGHT);
			handler.setBehavior(new AgressiveBehavior());
			return;
		}
		timeChasingBlindfold = -1;//dont chasing
		int i = handler.getI();
		int j = handler.getJ();
		int ni = i;
		int nj = j;
		Cell[][] cells = Game.level.getCells();
		int n = cells.length;
		int m = cells[0].length;
		Cell c = null;
		int attempts = 0;
		while (true) {
			if (attempts++ == 8) {
				return;
			}
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
			if (!Utils.inBounds(ni, 0, n - 1) || !Utils.inBounds(nj, 0, m - 1)) {
				continue;
			}
			c = cells[ni][nj];
			if (!c.isPassable() || c.hasMob() || c.isDenyTravelling()) {
				continue;
			}
			break;
		}
		handler.moveToPos(ni, nj);
	}
}