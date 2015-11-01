package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.health.InjuryManager.InjuryType;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;
import com.jackson.myengine.Utils.Pair;

public class WeaponFactory {
    public static enum Type {
	RUSTY_KNIFE, MACHETE, M16
    };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Weapon create(Type type) {
	InjuryHelper helper = new InjuryHelper();
	switch (type) {
	case RUSTY_KNIFE:
	    helper.add(new Pair(InjuryType.SCRATCH, .2f));
	    helper.add(new Pair(InjuryType.CUT, 1f));
	    return new Weapon("rusty_knife", "Ржавый кинжал", 1, 1, 2, 30, Weapon.Type.MELEE, helper);
	case MACHETE:
	    helper.add(new Pair(InjuryType.SCRATCH, .05f));
	    helper.add(new Pair(InjuryType.CUT, 1f));
	    return new Weapon("machete", "Мачете", 2.5f, 1, 3, 100, Weapon.Type.MELEE, helper);
	case M16:
	    helper.add(new Pair(InjuryType.SHOT, 1f));
	    return new Weapon("m16", "M16", 6f, 2, 4, 1000, Weapon.Type.GUN, helper);

	default:
	    return null;
	}
    }
}
