package com.jackson.cyberpunk.item;

public class MeleeWeapon extends Weapon {
	private boolean steelArms;

	public MeleeWeapon(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost, boolean twoHanded, float attackAP, boolean steelArms,
			InjuryHelper helper) {
		super(name, description, pictureName, sizeI, sizeJ, cost, twoHanded, attackAP,
				helper);
		this.steelArms = steelArms;
	}

	/**
	 * is kholodnoe orujie
	 */
	public boolean isSteelArms() {
		return steelArms;
	}

	@Override
	public boolean isMelee() {
		return true;
	}

	@Override
	public ItemView getView() {
		if (view == null) {
			view = new ItemView(this);
		}
		return view;
	}

	@Override
	public Item copy() {
		return new MeleeWeapon(name, description, pictureName, sizeI, sizeJ, cost,
				twoHanded, attackAP, steelArms, helper);
	}
}