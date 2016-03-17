package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.mob.Player;

public abstract class Item {
	/**
	 * {@code name} is needed for multilanguages support 
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
		} else if (!inv.getKnapsack().equals(this) && (w == null || !w.equals(this))) {
			res.add(Type.INV_PICK, null, Inventory.DROP_AP_COST);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cost;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((pictureName == null) ? 0 : pictureName.hashCode());
		result = prime * result + posI;
		result = prime * result + posJ;
		result = prime * result + sizeI;
		result = prime * result + sizeJ;
		result = prime * result + ((view == null) ? 0 : view.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (cost != other.cost)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (pictureName == null) {
			if (other.pictureName != null)
				return false;
		} else if (!pictureName.equals(other.pictureName))
			return false;
		if (posI != other.posI)
			return false;
		if (posJ != other.posJ)
			return false;
		if (sizeI != other.sizeI)
			return false;
		if (sizeJ != other.sizeJ)
			return false;
		if (view == null) {
			if (other.view != null)
				return false;
		} else if (!view.equals(other.view))
			return false;
		return true;
	}

	public abstract Item copy();
}