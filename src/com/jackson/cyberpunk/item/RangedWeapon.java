package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.myengine.Utils;

public class RangedWeapon extends Weapon {
	private int ammo, maxAmmo;

	public RangedWeapon(String name, String description, String pictureName, int maxAmmo,
			int sizeI, int sizeJ, int cost, boolean twoHanded, InjuryHelper helper) {
		super(name, description, pictureName, sizeI, sizeJ, cost, twoHanded, helper);
		this.maxAmmo = maxAmmo;
		ammo = 0;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		menu = super.onContextMenuCreate(menu);
		if (ammo > 0) {
			menu.add(ContextMenu.Type.INV_UNLOAD_RIFLE);
		}
		if (ammo < maxAmmo) {
			menu.add(ContextMenu.Type.INV_LOAD_RIFLE);
		}
		return menu;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	public int getAmmo() {
		return ammo;
	}

	public int getMaxAmmo() {
		return maxAmmo;
	}

	public int getShots() {
		// TODO: это заглушечка
		return Math.min(ammo, Utils.rand.nextInt(3) + 2);
	}

	@Override
	public ItemView getView() {
		if (view == null) {
			view = new CountableItemView(this) {
				@Override
				protected int getAmount() {
					return getAmmo();
				}
			};
		}
		return view;
	}

	@Override
	public boolean isMelee() {
		return false;
	}

	@Override
	public Item copy() {
		return new RangedWeapon(name, description, pictureName, maxAmmo, sizeI, sizeJ,
				cost, twoHanded, helper);
	}
}