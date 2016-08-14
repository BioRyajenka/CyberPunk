package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.mob.Player;

public abstract class Item {
	/**
	 * {@code name} is for multilanguage support 
	 */
	protected String name;
	protected String description, pictureName;
	// вес в фунтах. 1 фунт = 0.45 кг
	// protected float weight;
	protected int sizeI, sizeJ, cost, posI, posJ;

	protected ItemView view;

	public Item(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost) {
		this.name = name;
		this.description = description;
		this.pictureName = pictureName;
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
			res.add(Type.INV_DROP, null, Inventory.DROP_AP_COST);
		} else if (w == null || w != this) {
			res.add(Type.INV_PICK, null, Inventory.PICK_AP_COST);
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

	public abstract Item copy();
}