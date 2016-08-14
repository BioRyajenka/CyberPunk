package com.jackson.cyberpunk.health;

import java.util.List;

import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;

public class Arm extends DualPart {
	private InjuryHelper helper;
	private float attackAP;
	private boolean steelArms;

	public Arm(String name, String description, String pictureName, int sizeI, int sizeJ,
			float strength, int cost, float attackAP, boolean steelArms, InjuryHelper helper,
			boolean organic, List<Effect> profits) {
		super(Type.ARM, name, description, pictureName, sizeI, sizeJ, strength, cost,
				organic, profits);
		this.helper = helper;
		this.attackAP = attackAP;
		this.steelArms = steelArms;
	}

	public InjuryHelper getInjuryHelper() {
		return helper;
	}
	
	public float getAttackAP() {
		return attackAP;
	}
	
	public boolean isSteelArms() {
		return steelArms;
	}

	@Override
	public Item copy() {
		Arm res = new Arm(name, description, pictureName, sizeI, sizeJ, strength, cost,
				attackAP, steelArms, helper, organic, permanentEffects);
		res.setLeft(isLeft());
		return res;
	}
}
