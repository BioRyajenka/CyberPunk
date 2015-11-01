package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.item.Weapon.InjuryHelper;

public interface IWeapon {
    public InjuryHelper getInjuryHelper();
    public boolean isMelee();
    public int getAmmo();
    public void setAmmo(int ammo);
    public Weapon.Type getWeaponType();
    public Ammo.Type getAmmoType();
}