package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.IWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;

public class Arm extends Part implements IDualPart, IWeapon {
    private boolean isLeft;
    private InjuryHelper helper;

    public Arm(String inInventoryName, String name, float weight,
	    float specialValue, float strength, int cost, InjuryHelper helper) {
	super(Type.ARM, inInventoryName, name, weight, specialValue, strength,
		cost);
	this.helper = helper;
    }

    public void setLeft(boolean isLeft) {
	this.isLeft = isLeft;
    }

    public boolean isLeft() {
	return isLeft;
    }

    @Override
    public String getName() {
	return (isLeft ? "левая " : "правая ") + super.getName();
    }

    public InjuryHelper getInjuryHelper() {
	return helper;
    }

    @Override
    public Part deepCopyRealization() {
	return new Arm(inInventoryPicName, name, weight, specialValue,
		strength, cost, helper);
    }

    @Override
    public boolean isMelee() {
	return true;
    }

    @Deprecated
    public int getAmmo() {
	return 0;
    }

    @Deprecated
    public void setAmmo(int ammo) {}

    @Override
    public Weapon.Type getWeaponType() {
	return Weapon.Type.MELEE;
    }

    @Override
    public Ammo.Type getAmmoType() {
	return null;
    }
}