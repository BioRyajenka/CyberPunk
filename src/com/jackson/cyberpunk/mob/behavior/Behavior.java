package com.jackson.cyberpunk.mob.behavior;

import com.jackson.cyberpunk.mob.NPC;

public abstract class Behavior {
	protected NPC handler;
	
	public Behavior(NPC handler) {
		this.handler = handler;
	}
	
	public abstract void onPlayerSeen(); 
	
    public abstract void doLogic();
    
    public abstract boolean isFightMode();
}