package com.jackson.cyberpunk.mob;

import java.lang.reflect.InvocationTargetException;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.item.ItemsManager;
import com.jackson.cyberpunk.item.Knapsack;
import com.jackson.cyberpunk.mob.behavior.Behavior;
import com.jackson.cyberpunk.mob.behavior.WanderBehavior;
import com.jackson.myengine.Log;

public abstract class NPC extends Mob {
	private Behavior behavior;

	public NPC(String picName, String name) {
		this(picName, name, generateRandomInventory(), WanderBehavior.class);
	}

	public NPC(String picName, String name, Inventory inventory, Class<? extends Behavior> behaviorClass) {
		super(picName, name, inventory);
		try {
			setBehavior(behaviorClass.getConstructor(NPC.class).newInstance(this));
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Log.e("NPC.java:NPC(): " + e);
			e.printStackTrace();
		}
	}

	protected static Inventory generateRandomInventory() {
		Knapsack knapsack = (Knapsack) ItemsManager.getItem("simple_knapsack");
		Inventory res = new Inventory(knapsack);
		// наполняем
		return res;
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