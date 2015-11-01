package com.jackson.cyberpunk.item;

import java.util.LinkedList;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.health.InjuryManager.InjuryType;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.Pair;

public class Weapon extends Item implements IWeapon {
    // дамаг просчитывается из веса
    public static enum Type {
	MELEE, GUN, SHOTGUN, SNIPER_RIFLE
    };

    private int ammo;
    private Type type;
    private InjuryHelper helper;

    protected Weapon(String inInventoryPicName, String name, float weight,
	    int sizeI, int sizeJ, int cost, Type type, InjuryHelper helper) {
	super("weapons/" + inInventoryPicName, name, weight, sizeI, sizeJ, cost);
	this.type = type;
	ammo = 0;
	this.helper = helper;
    }

    @Override
    public ContextMenu getContextMenu() {
	ContextMenu res = super.getContextMenu();
	if (!isMelee()) {
	    if (getAmmo() > 0)
		res.add(ContextMenu.Type.INV_UNLOAD_RIFLE);
	    if (getAmmo() < 40)
		res.add(ContextMenu.Type.INV_LOAD_RIFLE);
	}
	return res;
    }

    public void setAmmo(int ammo) {
	this.ammo = ammo;
    }

    public int getAmmo() {
	return ammo;
    }
    
    public Ammo.Type getAmmoType() {
	return Ammo.Type.valueOf(getWeaponType().name());
    }

    public Type getWeaponType() {
	return type;
    }

    public boolean isMelee() {
	return type == Type.MELEE;
    }

    public InjuryHelper getInjuryHelper() {
	return helper;
    }

    public static class InjuryHelper {
	private LinkedList<Pair<InjuryType, Float>> mInjuryTypes;

	public InjuryHelper() {
	    mInjuryTypes = new LinkedList<Pair<InjuryType, Float>>();
	}

	@SafeVarargs
	public InjuryHelper(Pair<InjuryType, Float>... pInjuryTypes) {
	    this();
	    for (Pair<InjuryType, Float> p : pInjuryTypes)
		add(p);
	}

	public void add(Pair<InjuryType, Float> p) {
	    mInjuryTypes.add(p);
	}

	/**
	 * @param power from 0 to 1f
	 */
	private InjuryType getInjuryType(float power) {
	    for (int i = 0; i < mInjuryTypes.size(); i++) {
		Pair<InjuryType, Float> p = mInjuryTypes.get(i);
		if (p.second >= power)
		    return mInjuryTypes.get(i).first;
	    }
	    return mInjuryTypes.getLast().first;
	}

	public InjuryType getRandomInjury() {
	    return getInjuryType(Utils.rand.nextFloat());
	}
    }
    
    @Override
    public ItemView getView() {
	if (view == null) {
	    view = new CountableItemView(this) {
	        @Override
	        protected int getAmount() {
	            if (isMelee()) return -1;
	            return getAmmo();
	        }
	    };
	}
	return view;
    }
}