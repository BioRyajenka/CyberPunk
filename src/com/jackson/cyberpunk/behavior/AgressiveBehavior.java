package com.jackson.cyberpunk.behavior;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.IWeapon;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.cyberpunk.mob.Player;

public class AgressiveBehavior extends Behavior {
    private static Behavior singleton = new AgressiveBehavior();
    
    private AgressiveBehavior() { }
    
    @Override
    public void doLogic(NPC handler) {
	Player pl = Game.player;
	Game.setGameMode(Mode.FIGHT);

	wieldCoolestWeapon(handler);
	loadRifle(handler);
	if (!handler.isSeeMob(pl) 
		|| (handler.getWeapon().isMelee() && !handler.isMobNear(pl))) {
	    handler.makeCloserToLongTermTarget(pl.getI(), pl.getJ());
	    return;
	}
	handler.attack(pl);
    }
    
    private void loadRifle(NPC handler) {
	IWeapon w = handler.getWeapon();
	if (w.isMelee()) return;
	handler.loadRifle(w);
    }
    
    private void wieldCoolestWeapon(NPC handler) {
	Inventory inv = handler.getInventory();
	IWeapon resw = handler.getHealthSystem().getArm();
	for (Item i : inv.getItems()) {
	    if (i instanceof Weapon) {
		Weapon w = (Weapon) i;
		if (getWeaponTypeCoolness(w.getWeaponType()) > getWeaponTypeCoolness(resw.getWeaponType()) &&
			hasAmmo(handler, w.getAmmoType())) {
		    resw = w;
		}
	    }
	}
	handler.setWeapon(resw);
    }
    
    private int getWeaponTypeCoolness(Weapon.Type weaponType) {
	switch (weaponType) {
	case MELEE:
	    return 0;
	case GUN:
	    return 1;
	case SHOTGUN:
	    return 2;
	case SNIPER_RIFLE:
	    return 3;
	}
	return 666;
    }
    
    private boolean hasAmmo(NPC handler, Ammo.Type ammoType) {
	for (Item i : handler.getInventory().getItems()) {
	    if (i instanceof Ammo) {
		if (((Ammo)i).getType() == ammoType) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    public static Behavior getInstance() {
	return singleton;
    }
}