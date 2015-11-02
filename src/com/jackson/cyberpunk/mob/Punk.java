package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.mob.behavior.WanderBehavior;

public class Punk extends NPC {
	private static int id = 0;

	public Punk() {
		super("punk", "Панк " + id++, generateRandomInventory(), new WanderBehavior());
	}
}