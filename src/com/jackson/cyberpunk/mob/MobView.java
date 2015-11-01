package com.jackson.cyberpunk.mob;

import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;

public class MobView extends Entity {
    public MobView(Mob mob) {
	Sprite body = new Sprite(0, 0, "men/" + mob.getPicName());
	attachChild(body);
    }
}