package com.jackson.cyberpunk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.CountableItem;
import com.jackson.cyberpunk.item.CreditCard;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.Key;
import com.jackson.cyberpunk.item.Knapsack;
import com.jackson.cyberpunk.item.MeleeWeapon;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;

public final class Inventory {
	public static final float PICK_AP_COST = .2f;
	public static final float DROP_AP_COST = .2f;

	private Knapsack knapsack;
	private List<Item> items;

	public Inventory(Knapsack knapsack) {
		this.knapsack = knapsack;
		items = new LinkedList<Item>();
	}

	public boolean canAdd(Item item) {
		int n = knapsack.getCapacity() / 5, m = 5, w = item.getSizeJ(), h = item
				.getSizeI();

		if (item instanceof CountableItem) {
			CountableItem i = (CountableItem) item;
			int sum = i.getAmount();
			for (Item e : items)
				if (item.getClass().equals(e.getClass())) {
					// here we are restocking
					CountableItem ce = (CountableItem) e;
					sum += ce.getAmount();
					sum = Math.max(0, sum - ce.getStackSize());
					if (sum == 0) {
						return true;
					}
				}
			// sum > 0
		}

		for (int i = 0; i < n - h + 1; i++)
			for (int j = 0; j < m - w + 1; j++) {
				boolean ok = true;
				for (Item e : items) {
					ok &= (!Utils.boundingBox(i, j, i + h - 1, j + w - 1, e.getI(), e
							.getJ(), e.getI() + e.getSizeI() - 1, e.getJ() + e.getSizeJ()
									- 1));
				}
				if (ok) {
					return true;
				}
			}
		return false;
	}

	private boolean contains(Function<Item, Boolean> rule) {
		for (Item i : items) {
			if (rule.apply(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsSteelArms() {
		return contains(t -> {
			if (!(t instanceof MeleeWeapon)) {
				return false;
			}
			return ((MeleeWeapon) t).isSteelArms();
		});
	}

	public boolean contains(Class<? extends Item> c) {
		// .isAssignableFrom
		return contains(t -> t.getClass() == c);
	}

	public void remove(Class<? extends Item> c) {
		if (!contains(c)) {
			Log.e("Trying to remove smth by class which isn't exists!");
			return;
		}
		Item toRemove = null;
		for (Item i : items) {
			// .isAssignableFrom
			if (i.getClass() == c) {
				toRemove = i;
				break;
			}
		}
		items.remove(toRemove);
	}

	public void add(Item item) {
		if (!canAdd(item)) {
			Log.e("Trying to add smth in full inventory!");
			// TODO: if cant, drop
			return;
		}
		int n = knapsack.getCapacity() / 5;
		int m = 5;
		int w = item.getSizeJ();
		int h = item.getSizeI();
		int i1 = -1;
		int j1 = -1;

		if (item instanceof CountableItem) {
			CountableItem ci = (CountableItem) item;
			for (Item e : items)
				if (item.getClass().equals(e.getClass())) {
					CountableItem ai = (CountableItem) e;
					int sum = ai.getAmount() + ci.getAmount();
					ai.setAmount(Math.min(sum, ci.getStackSize()));
					sum = Math.max(0, sum - ci.getStackSize());
					ci.setAmount(sum);
					if (sum == 0)
						return;
				}
		}

		// searching for good cell to place
		for1: for (int i = 0; i < n - h + 1; i++) {
			for (int j = 0; j < m - w + 1; j++) {
				boolean ok = true;
				for (Item e : items)
					ok &= (!Utils.boundingBox(i, j, i + h - 1, j + w - 1, e.getI(), e
							.getJ(), e.getI() + e.getSizeI() - 1, e.getJ() + e.getSizeJ()
									- 1));
				if (ok) {
					i1 = i;
					j1 = j;
					break for1;
				}
			}
		}
		if (i1 == -1) {
			Log.e("Inventory.java: smth wrong...");
		}
		item.setIJ(i1, j1);
		items.add(item);
	}

	public void remove(Item item) {
		if (!items.contains(item)) {// using equals
			Log.e("Trying to remove smth which don't exist!");
			return;
		}
		items.remove(item);
	}

	public List<Item> getItems() {
		return items;
	}

	public boolean containsKey(LockType keyType) {
		for (Item i : items) {
			if (i instanceof Key && ((Key) i).getKeyType() == keyType) {
				return true;
			}
		}
		return false;
	}

	public void removeKey(LockType keyType) {
		Item toRemove = null;
		for (Item i : items) {
			if (i instanceof Key && ((Key) i).getKeyType() == keyType) {
				toRemove = i;
				break;
			}
		}
		if (toRemove == null) {
			Log.e("There is no such key to remove! " + keyType.getName());
			return;
		}
		items.remove(toRemove);
	}

	public Knapsack getKnapsack() {
		return knapsack;
	}

	public void setKnapsack(Knapsack knapsack) {
		this.knapsack = knapsack;
		// TODO: replace items
	}

	/**
	 * Check if there a certain amount of countable item.
	 * 
	 * @param needRemove
	 *            - true if this amount of items need to be removed even if
	 *            exact number wasn't found.
	 * @return found amount of items
	 */
	private int searchForCountable(Class<? extends CountableItem> clazz, int amount,
			boolean needRemove) {
		int acc = 0;
		List<Item> toRemove = new ArrayList<>();
		CountableItem toReduce = null;
		int leftAmount = 0;
		for (Item i : items) {
			if (clazz.isInstance(i)) {
				CountableItem a = (CountableItem) i;
				acc += a.getAmount();
				if (acc > amount) {
					toReduce = (CountableItem) i;
					leftAmount = acc - amount;
					break;
				}
				if (acc <= amount) {
					toRemove.add(i);
				}
				if (acc == amount) {
					break;
				}
			}
		}
		if (needRemove) {
			items.removeAll(toRemove);
			if (toReduce != null) {
				toReduce.setAmount(leftAmount);
			}
		}
		return acc;
	}

	public boolean removeMoney(int amount) {
		int foundMoney = searchForCountable(CreditCard.class, amount, false);
		if (foundMoney == amount) {
			searchForCountable(CreditCard.class, amount, true);
			return true;
		}
		return false;
	}

	/**
	 * Reloading rifle w using ammo from this inventory. Returns false if
	 * inventory has not appropriate ammos
	 */
	public boolean reloadRifle(RangedWeapon w) {
		int foundAmmo = searchForCountable(Ammo.class, w.getMaxAmmo(), true);
		if (foundAmmo == 0) {
			return false;
		}
		w.setAmmo(foundAmmo);
		return true;
	}
}