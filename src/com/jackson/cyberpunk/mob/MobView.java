package com.jackson.cyberpunk.mob;

import com.jackson.myengine.Sprite;

public class MobView extends Sprite {
	public MobView(Mob mob) {
		super(0, 0, "men/" + mob.getPicName());
	}
}