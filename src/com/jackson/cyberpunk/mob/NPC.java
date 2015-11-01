package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.behavior.Behavior;
import com.jackson.cyberpunk.behavior.WanderBehavior;
import com.jackson.cyberpunk.item.Knapsack;
import com.jackson.cyberpunk.item.KnapsackFactory;
import com.jackson.cyberpunk.item.KnapsackFactory.Type;
import com.jackson.myengine.Log;

public abstract class NPC extends Mob {
    // shopkeeper beh, enemyBeh
    private Behavior behavior;

    public NPC(String picName, String name) {
	this(picName, name, generateRandomInventory(), WanderBehavior.getInstance());
    }

    public NPC(String picName, String name, Inventory inventory,
	    Behavior behavior) {
	super(picName, name, inventory);
	setBehavior(behavior);
    }

    protected static Inventory generateRandomInventory() {
	Knapsack knapsack = KnapsackFactory.create(Type.SIMPLE);
	Inventory res = new Inventory(knapsack);
	// наполняем
	return res;
    }

    public void setBehavior(Behavior behavior) {
	this.behavior = behavior;
    }

    public void doLogic() {
	if (getAction() != Action.NOTHING) {
	    Log.e("NPC.java: trying to do " + getName() + "'s logic while busy!");
	    return;
	}
	if (leftActionPoints > 0 || Game.getGameMode() == Game.Mode.EXPLORE)
	    behavior.doLogic(this);
    }
}