package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.mob.behavior.WanderBehavior;
import com.jackson.myengine.Utils;

public class Punk extends NPC {
	private static int id = 0;

	public Punk() {
		super("punk", "Панк " + id++, Utils.rand.nextInt(10), WanderBehavior.class);
	}
}