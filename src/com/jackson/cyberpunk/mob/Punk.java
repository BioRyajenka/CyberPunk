package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.mob.behavior.WanderBehavior;
import com.jackson.myengine.Utils;

public class Punk extends NPC {
	public Punk() {
		super("punk", "Панк", Utils.rand.nextInt(10), WanderBehavior.class);
	}
	
	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}
}