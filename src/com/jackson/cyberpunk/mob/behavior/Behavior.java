package com.jackson.cyberpunk.mob.behavior;

import com.jackson.cyberpunk.mob.NPC;

public abstract class Behavior {
	protected int timeChasingBlindfold = 0;
	
    public abstract void doLogic(NPC handler);
    
    public boolean isChasingPlayer() {
    	return timeChasingBlindfold <= 5 && timeChasingBlindfold != -1;
    }
    
    //public abstract static Behavior getInstance(); //can't make it abstract
}