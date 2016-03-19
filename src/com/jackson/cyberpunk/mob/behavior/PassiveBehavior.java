package com.jackson.cyberpunk.mob.behavior;

import com.jackson.cyberpunk.mob.NPC;

public class PassiveBehavior extends Behavior {
	public PassiveBehavior(NPC handler) {
		super(handler);
	}

	@Override
	public void onPlayerSeen() {
	}

	@Override
	public boolean doLogic() {
		handler.finishTurn();
		return false; // never fell hungry
	}

	@Override
	public boolean isFightMode() {
		return false;
	}

	@Override
	public void onAttackedByPlayer() {
		handler.setBehavior(AggressiveBehavior.class);
	}
}