package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.mob.Mob;

public class Corpse extends Item {
    public Corpse(Mob m) {
	super("corpse", "Труп " + m.getName() + "а", 160, 2, 5, 500);
    }
}
