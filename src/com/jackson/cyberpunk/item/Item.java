package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.health.Arm;
import com.jackson.cyberpunk.mob.Player;

public abstract class Item {
	protected String inInventoryPicName, name;
	// вес в фунтах. 1 фунт = 0.45 кг
	protected float weight;
	protected int sizeI, sizeJ, cost, posI, posJ;

	protected ItemView view;

	public Item(String inInventoryPicName, String name, float weight, int sizeI,
			int sizeJ, int cost) {
		this.inInventoryPicName = "items/" + inInventoryPicName;
		this.name = name;
		this.weight = weight;
		this.sizeI = sizeI;
		this.sizeJ = sizeJ;
		this.cost = cost;

		posI = posJ = 0;
	}

	public ContextMenu getContextMenu() {
		ContextMenu res = new ContextMenu();
		Player pl = Game.player;
		Inventory inv = pl.getInventory();

		if (inv.getItems().contains(this))
			res.add(Type.INV_DROP);
		else if (!inv.getKnapsack().equals(this) && !pl.getWeapon().equals(this))
			res.add(Type.INV_PICK);

		if (this instanceof IWeapon) {
			if (pl.getWeapon().equals(this)) {
				if (!(this instanceof Arm))
					res.add(Type.INV_UNWIELD);
			} else if (pl.getWeapon() instanceof Arm)
				res.add(Type.INV_WIELD);
		}
		return res;
	}

	public float getWeight() {
		return weight;
	}

	public int getSizeI() {
		return sizeI;
	}

	public int getSizeJ() {
		return sizeJ;
	}

	public String getName() {
		return name;
	}

	public void setIJ(int i, int j) {
		this.posI = i;
		this.posJ = j;
	}

	public int getI() {
		return posI;
	}

	public int getJ() {
		return posJ;
	}

	public String getInInventoryPicName() {
		return inInventoryPicName;
	}

	public ItemView getView() {
		if (view == null)
			view = new ItemView(this);
		return view;
	}

	@Override
	public String toString() {
		return "item " + name;
	}
}