package com.jackson.cyberpunk.health;

import java.util.ArrayList;

import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.PartProfit;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;

public class Arm extends DualPart {
	private InjuryHelper helper;

	public Arm(String name, String description, String pictureName, int sizeI, int sizeJ,
			float strength, int cost, InjuryHelper helper, boolean organic,
			ArrayList<PartProfit> profits) {
		super(Type.ARM, name, description, pictureName, sizeI, sizeJ, strength, cost,
				organic, profits);
		this.helper = helper;
	}

	public InjuryHelper getInjuryHelper() {
		return helper;
	}
	
	@Override
	public Item copy() {
		Arm res = new Arm(name, description, pictureName, sizeI, sizeJ, strength, cost, helper, organic, profits);
		res.setLeft(isLeft());
		return res;
	}
}
