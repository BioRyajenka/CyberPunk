package com.jackson.cyberpunk.mob;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.item.CreditCard;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemsManager;
import com.jackson.cyberpunk.item.Knapsack;
import com.jackson.cyberpunk.mob.behavior.Behavior;
import com.jackson.cyberpunk.mob.behavior.WanderBehavior;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;

public abstract class NPC extends Mob {
	private Behavior behavior;

	public NPC(String picName, String name, int difficulty) {
		this(picName, name, difficulty, WanderBehavior.class);
	}

	public NPC(String picName, String name, int difficulty,
			Class<? extends Behavior> behaviorClass) {
		this(picName, name, difficulty, generateRandomInventory(difficulty),
				behaviorClass);
	}

	public NPC(String picName, String name, int difficulty, Inventory inventory,
			Class<? extends Behavior> behaviorClass) {
		super(picName, name, inventory);
		setRandomParts(difficulty);
		try {
			setBehavior(behaviorClass.getConstructor(NPC.class).newInstance(this));
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Log.e("NPC.java:NPC(): " + e);
			Log.printStackTrace();
		}
	}

	/**
	 * @param difficulty
	 *            10 means the most cool parts
	 */
	protected void setRandomParts(int difficulty) {
		if (difficulty == 0) {
			return;
		}
		for (int i = 0; i < 5 + Utils.rand.nextInt(difficulty); i++) {
			healthSystem.setPart(getRandomPart(difficulty));
		}
	}

	/**
	 * @param difficulty
	 *            10 means the most cool inventory
	 */
	protected static Inventory generateRandomInventory(int difficulty) {
		Knapsack knapsack = (Knapsack) ItemsManager.getItem("simple_knapsack");
		Inventory res = new Inventory(knapsack);
		if (difficulty == 0) {
			return res;
		}

		// TODO: патроны
		int money = Utils.rand.nextInt(difficulty * difficulty * 100);
		if (money != 0) {
			res.add(new CreditCard(money));
		}
		for (int i = 0; i < 5 + Utils.rand.nextInt(difficulty); i++) {
			Item it = getRandomItem(difficulty);
			if (res.canAdd(it)) {
				res.add(it);
			}
		}
		return res;
	}

	protected static Part getRandomPart(int difficulty) {
		return (Part) getRandom(ItemsManager.getParts(), difficulty);
	}

	protected static Item getRandomItem(int difficulty) {
		return getRandom(ItemsManager.getItemsExeptOrganicParts(), difficulty);
	}

	private static <T extends Item> Item getRandom(List<T> items, int difficulty) {
		items.sort(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return ((Integer) o1.getCost()).compareTo(o2.getCost());
			}
		});
		int ceiling = items.get(items.size() - 1).getCost() / 10 * difficulty;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getCost() > ceiling) {
				return items.get(Utils.rand.nextInt(i + 1));
			}
		}
		Log.e("For some reason, getRandom() returns nothing");
		Log.printStackTrace();
		return null;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public void doLogic() {
		if (getAction() != Action.NOTHING) {
			Log.e("NPC.java: trying to do " + getName() + "'s logic while busy!");
			return;
		}
		if (!isTurnFinished() || Game.getGameMode() == Game.Mode.EXPLORE) {
			behavior.doLogic();
		}
	}
}