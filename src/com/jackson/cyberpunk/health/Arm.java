package com.jackson.cyberpunk.health;

import java.util.ArrayList;

import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.PartProfit;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;

public class Arm extends DualPart {
	private InjuryHelper helper;
	private float attackAP;

	public Arm(String name, String description, String pictureName, int sizeI, int sizeJ,
			float strength, int cost, float attackAP, InjuryHelper helper,
			boolean organic, ArrayList<PartProfit> profits) {
		super(Type.ARM, name, description, pictureName, sizeI, sizeJ, strength, cost,
				organic, profits);
		this.helper = helper;
		this.attackAP = attackAP;
	}

	public InjuryHelper getInjuryHelper() {
		return helper;
	}
	
	public float getAttackAP() {
		return attackAP;
	}

	@Override
	public Item copy() {
		Arm res = new Arm(name, description, pictureName, sizeI, sizeJ, strength, cost,
				attackAP, helper, organic, profits);
		res.setLeft(isLeft());
		return res;
	}
}
