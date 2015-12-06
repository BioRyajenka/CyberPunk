package com.jackson.cyberpunk.mob;

import java.util.LinkedList;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.Key;
import com.jackson.cyberpunk.item.KnapsackFactory;
import com.jackson.cyberpunk.item.WeaponFactory;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IEntity;

public class Player extends Mob {
	/**
	 * A man which we are bearing
	 */
	// private Mob manOnShoulders;

	private int longTermTargetI, longTermTargetJ, turnsAmount = 0;

	private LinkedList<Runnable> toRunOnTravelFinish = new LinkedList<Runnable>();

	public Player() {
		super("body", "Алан", new Inventory(KnapsackFactory.create(
				KnapsackFactory.Type.SIMPLE)));
		inventory.add(WeaponFactory.create(WeaponFactory.Type.RUSTY_KNIFE));
		inventory.add(WeaponFactory.create(WeaponFactory.Type.M16));
		inventory.add(new Ammo(Ammo.Type.GUN, 80));
		inventory.add(new Key(LockType.KEY1));
		resetLongTermTarget();
	}

	@Override
	public void onManagedUpdate() {
		if (getAction() == Action.NOTHING) {
			checkMode();
			Mode mode = Game.getGameMode();
			if (mode == Mode.FIGHT && leftActionPoints == 0) {
				boolean ok = false, busy = false;
				Entity mobs = Game.level.mobs_not_views;
				for (int i = 0; i < mobs.getChildCount(); i++) {
					IEntity e = mobs.getChild(i);
					Mob m = ((Mob) e);
					ok |= m.leftActionPoints > 0;
					busy |= m.getAction() != Action.NOTHING;
				}
				if (ok) {
					//not all mobs are finished their steps yet
					if (!busy) {
						//moving animation is in progress
						Game.doMobsSteps();
					}
				} else {
					//all mobs are finished
					for (int i = 0; i < mobs.getChildCount(); i++) {
						//refreshing all including player
						IEntity e = mobs.getChild(i);
						Mob m = ((Mob) e);
						m.refreshLeftActionPoints();
					}
				}
			}
			if (mode == Mode.EXPLORE || (mode == Mode.FIGHT && leftActionPoints > 0)) {
				if (longTermTargetI == posI && longTermTargetJ == posJ) {
					for (Runnable r : toRunOnTravelFinish)
						r.run();
					toRunOnTravelFinish.clear();
				} else {
					makeCloserToLongTermTarget(longTermTargetI, longTermTargetJ);
					getHealthSystem().update();
				}
			}
		}
		super.onManagedUpdate();
	}

	private void checkMode() {
		if (isFightMode()) {
			Game.setGameMode(Mode.FIGHT);
		} else {
			Game.setGameMode(Mode.EXPLORE);
		}
	}

	private boolean isFightMode() {
		IEntity mobs = Game.level.mobs_not_views;
		for (int i = 0; i < mobs.getChildCount(); i++) {
			IEntity e = mobs.getChild(i);
			if (e instanceof Player)
				continue;
			NPC m = ((NPC) e);
			if (m.isSeeMob(Game.player) || m.getBehavior().isChasingPlayer()) {
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