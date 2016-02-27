package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.mob.Player;

public abstract class Item {
	protected String name, description, pictureName;
	// вес в фунтах. 1 фунт = 0.45 кг
	// protected float weight;
	protected int sizeI, sizeJ, cost, posI, posJ;

	protected ItemView view;

	public Item(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost) {
		this.name = name;
		this.description = description;
		this.pictureName = pictureName;
		this.name = name;
		this.sizeI = sizeI;
		this.sizeJ = sizeJ;
		this.cost = cost;

		posI = posJ = 0;
	}

	public ContextMenu getContextMenu() {
		ContextMenu res = new ContextMenu();
		onContextMenuCreate(res);
		Player pl = Game.player;
		Inventory inv = pl.getInventory();
		Weapon w = pl.getWeapon();

		if (inv.getItems().contains(this)) {
			res.add(Type.INV_DROP);
		} else if (!inv.getKnapsack().equals(this) && (w == null || !w.equals(this))) {
			res.add(Type.INV_PICK);
		}

		return res;
	}

	protected abstract ContextMenu onContextMenuCreate(ContextMenu menu);

	public int getSizeI() {
		return sizeI;
	}

	public int getSizeJ() {
		return sizeJ;
	}

	public String getDescription() {
		return description;
	}
	
	public int getCost() {
		return cost;
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

	public String getPictureName() {
		return pictureName;
	}

	public ItemView getView() {
		if (view == null) {
			view = new ItemView(this);
		}
		return view;
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		Item rhs = (Item) obj;
		return rhs.name.equals(name);
	}

	public abstract Item copy();
}