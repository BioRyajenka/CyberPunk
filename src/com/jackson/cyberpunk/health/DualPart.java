package com.jackson.cyberpunk.health;

import java.util.ArrayList;

import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemView;
import com.jackson.cyberpunk.item.PartProfit;

public class DualPart extends Part {
	private boolean isLeft = false;

	public DualPart(Type type, String name, String description, String pictureName,
			int sizeI, int sizeJ, float strength, int cost, boolean organic,
			ArrayList<PartProfit> profits) {
		super(type, name, description, pictureName, sizeI, sizeJ, strength, cost,
				organic, profits);
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public boolean isRight() {
		return !isLeft();
	}

	@Override
	public String getDescription() {
		// TODO: левый/правый
		return (isLeft ? "левая " : "правая ") + super.getDescription();
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
		DualPart res = new DualPart(type, name, description, pictureName, sizeI, sizeJ, strength, cost, organic, profits);
		res.setLeft(isLeft);
		return res;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DualPart)) {
			return false;
		}
		return super.equals(obj) && isLeft == ((DualPart) obj).isLeft;
	}
}