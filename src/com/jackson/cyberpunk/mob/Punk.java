package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.behavior.WanderBehavior;

public class Punk extends NPC {
    private static int id = 0;
    
    public Punk() {
	super("punk", "���� " + id++, generateRandomInventory(), 
		WanderBehavior.getInstance());
    }
}