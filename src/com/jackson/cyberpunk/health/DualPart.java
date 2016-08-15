package com.jackson.cyberpunk.health;

import java.util.List;

import com.jackson.cyberpunk.health.buffs.PainLocalBuff;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemView;

public class DualPart extends Part {
	private boolean isLeft = false;

	public DualPart(Type type, String name, String description, String pictureName, int sizeI,
			int sizeJ, float strength, int cost, boolean organic, List<Effect> effects) {
		super(type, name, description, pictureName, sizeI, sizeJ, strength, cost, organic,
				effects);
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
		
		// reloading description for buff. it's crunch
		buffs.clear();
		buffs.add(new PainLocalBuff(this));
	}

	public boolean isLeft() {
		return isLeft;
	}

	public boolean isRight() {
		return !isLeft();
	}

	@Override
	public String getDescription() {
		// TODO: �����/������
		return (isLeft ? "����� " : "������ ") + super.getDescription();
	}

	@Override
	public ItemView getView() {
		if (view == null) {
			view = new DualPartView(this);
		}
		return view;
	}

	@Override
	public Item copy() {
		DualPart res = new DualPart(type, name, description, pictureName, sizeI, sizeJ,
				strength, cost, organic, permanentEffects);
		res.setLeft(isLeft);
		return res;
	}
}