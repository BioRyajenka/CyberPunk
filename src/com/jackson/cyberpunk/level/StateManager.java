package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Utils;

public class StateManager {
	protected enum Status {
		HACKED, BROKEN, WORKS
	};
	
	protected Status status;
	
	public StateManager() {
		this(Status.WORKS);
	}
	
	public StateManager(Status status) {
		this.status = status;
	}
	
	public boolean isBroken() {
		return status == Status.BROKEN;
	}
	
	public boolean isHacked() {
		return status == Status.HACKED;
	}
	
	public boolean isWorks() {
		return status == Status.WORKS;
	}

	public boolean tryHack() {
		if (Utils.rand.nextFloat() < Player.SUCCESSFUL_HACK_PROBABILITY) {
			status = Status.HACKED;
			return true;
		} else {
			status = Status.BROKEN;
			return false;
		}
	}
}
