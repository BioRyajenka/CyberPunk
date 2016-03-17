package com.jackson.cyberpunk.mob;

import java.util.LinkedList;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.LogText;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.ItemsManager;
import com.jackson.cyberpunk.item.Key;
import com.jackson.cyberpunk.item.Knapsack;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Entity;

public class Player extends Mob {
	/**
	 * A man which we are bearing
	 */
	// private Mob manOnShoulders;

	private int longTermTargetI, longTermTargetJ, turnsAmount = 0;

	private LinkedList<Runnable> toRunOnTravelFinish = new LinkedList<Runnable>();

	public Player() {
		super("body", "Алан", new Inventory((Knapsack) ItemsManager.getItem(
				"simple_knapsack")));
		inventory.add(ItemsManager.getItem("rusty_knife"));
		inventory.add(ItemsManager.getItem("m16"));
		inventory.add(new Ammo(80));
		inventory.add(new Key(LockType.KEY1));
		resetLongTermTarget();
	}

	@Override
	public void onManagedUpdate() {
		if (getAction() == Action.NOTHING) {
			checkSeePlayer();
			checkMode();
			Mode mode = Game.getGameMode();
			if (isTurnFinished()) {
				boolean ok = false, busy = false;
				for (Entity e : Game.level.mobs_not_views.getChildren()) {
					Mob m = ((Mob) e);
					ok |= !m.isTurnFinished();
					busy |= m.getAction() != Action.NOTHING;
				}
				if (ok) {
					// not all mobs are finished their steps yet
					if (!busy) {
						// moving animation is in progress. here mobs doing
						// their turn
						checkSeePlayer();
						Game.doMobsSteps();
					}
				} else {
					// all mobs are finished
					for (Entity e : Game.level.mobs_not_views.getChildren()) {
						// refreshing all including player
						Mob m = ((Mob) e);
						m.refreshLeftActionPointsAndTurnFinished();
					}
				}
			}
			if (mode == Mode.EXPLORE || (mode == Mode.FIGHT && !isTurnFinished())) {
				if (longTermTargetI == posI && longTermTargetJ == posJ) {
					for (Runnable r : toRunOnTravelFinish) {
						r.run();
					}
					toRunOnTravelFinish.clear();
				} else {
					if (mode == Mode.EXPLORE || leftLegActionPoints > 0) {
						makeStepCloserToTarget(longTermTargetI, longTermTargetJ);
						getHealthSystem().update();
					} else {
						if (mode == Mode.FIGHT) {
							// means movementAP had gone out
							LogText.add("Не хватает ОД передвижения");
						}
						toRunOnTravelFinish.clear();
						longTermTargetI = targetI;
						longTermTargetJ = targetJ;
					}
				}
			}
		}
		super.onManagedUpdate();
	}

	private void checkSeePlayer() {
		for (Entity e : Game.level.mobs_not_views.getChildren()) {
			if (e instanceof Player) {
				continue;
			}
			NPC m = (NPC) e;
			if (m.isSeeMob(this)) {   
				m.getBehavior().onPlayerSeen();
			}
		}
	}

	private void checkMode() {
		if (isFightMode()) {
			Game.setGameMode(Mode.FIGHT);
		} else {
			Game.setGameMode(Mode.EXPLORE);
		}
	}

	private boolean isFightMode() {
		for (Entity e : Game.level.mobs_not_views.getChildren()) {
			if (e instanceof Player) {
				continue;
			}
			NPC m = (NPC) e;
			if (m.getBehavior().isFightMode()) {
				return true;
			}
		}
		return false;
	}

	public void travelToTheCell(int longTermTargetI, int longTermTargetJ) {
		this.longTermTargetI = longTermTargetI;
		this.longTermTargetJ = longTermTargetJ;
		toRunOnTravelFinish.clear();
	}

	@Override
	/**
	 * Move to adjoined cell
	 */
	public void moveToPos(int targetI, int targetJ) {
		turnsAmount++;
		super.moveToPos(targetI, targetJ);
		if (Game.getGameMode() == Mode.EXPLORE) {
			Game.doMobsSteps();
		}
	}

	public void resetLongTermTarget() {
		longTermTargetI = posI;
		longTermTargetJ = posJ;
	}

	@Override
	public void die(DieReason reason) {
		// MyScene.levelView.hide();
		MyScene.newMessage("Ты умер от " + reason.getText() + " спустя " + turnsAmount
				+ " ходов.").setOkAction(new Runnable() {
					@Override
					public void run() {
						Game.terminate();
					}
				});
	}

	public void addOnTravelFinish(Runnable r) {
		toRunOnTravelFinish.add(r);
	}
}