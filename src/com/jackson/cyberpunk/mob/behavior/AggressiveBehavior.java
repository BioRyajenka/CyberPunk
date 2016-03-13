package com.jackson.cyberpunk.mob.behavior;

import java.util.function.BiFunction;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Door;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Utils.IntPair;

public class AggressiveBehavior extends Behavior {
	private final static int CHASING_TIME = 5;

	protected int timeChasingBlindfold = CHASING_TIME + 1;

	private boolean isChasingPlayer() {
		return timeChasingBlindfold <= CHASING_TIME;
	}

	public AggressiveBehavior(NPC handler) {
		super(handler);
	}

	private int lastSeenPlayerPosI;
	private int lastSeenPlayerPosJ;

	@Override
	public void onPlayerSeen() {
		Player pl = Game.player;
		timeChasingBlindfold = 0;
		lastSeenPlayerPosI = pl.getI();
		lastSeenPlayerPosJ = pl.getJ();
	}

	@Override
	public void doLogic() {
		Player pl = Game.player;

		timeChasingBlindfold++;

		if (!isChasingPlayer()) {
			handler.setBehavior(new WanderBehavior(handler));
			handler.doLogic();
			return;
		}

		// TODO: нуу моб может не тратя ОД менять оружие. ну ок.
		wieldCoolestRangedWeapon();
		if (pl.getWeapon() != null && pl.getWeapon().isRanged()) {
			if (!loadRifle()) {
				wieldCoolestMeleeWeapon();
			}
		} else {
			wieldCoolestMeleeWeapon();
		}

		// TODO: isReachable
		if (pl.isSeeMob(handler)) {
			// see him
			if ((handler.getWeapon() == null || handler.getWeapon().isMelee())
					&& !handler.isMobNear(pl)) {
				// melee
				if (handler.getLeftLegActionPoints() == 0) {
					handler.finishTurn();
					return;
				}
				handler.makeStepCloserToTarget(pl.getI(), pl.getJ());
			} else {
				// ranged or standing near
				if (handler.getLeftArmActionPoints() == 0) {
					handler.finishTurn();
					return;
				}
				handler.attack(pl);
			}
		} else {			
			// chasing him
			Level level = Game.level;
			Cell[][] cells = level.getCells();
			BiFunction<Integer, Integer, Boolean> validator = (i1, j1) -> {
				Cell c = cells[i1][j1];
				return (c.isPassable() || c instanceof Door) && !c.hasMob() && !c
						.isDenyTravelling();
			};
			IntPair pair = level.getStepTowardsTarget(handler.getI(), handler.getJ(),
					lastSeenPlayerPosI, lastSeenPlayerPosJ, validator);
			if (pair.first == -1) {
				// the way is closed
				if (tries++ == 4) {
					handler.finishTurn();
				}
				return;
			}
			tries = 0;
			Cell tc = cells[pair.first][pair.second];
			if (tc instanceof Door) {
				((Door) tc).setOpened(true);
			}
			if (handler.getLeftLegActionPoints() == 0) {
				handler.finishTurn();
				return;
			}
			handler.moveToPos(tc.getI(), tc.getJ());
		}
	}
	
	int tries = 0;

	private boolean loadRifle() {
		Weapon w = handler.getWeapon();
		return handler.getInventory().reloadRifle((RangedWeapon) w);
	}

	private void wieldCoolestRangedWeapon() {
		Inventory inv = handler.getInventory();
		Weapon resw = null;
		for (Item i : inv.getItems()) {
			if (i instanceof Weapon) {
				Weapon w = (Weapon) i;
				if (!w.isRanged()) {
					continue;
				}
				if (getWeaponCoolness(w) > getWeaponCoolness(resw)) {
					resw = w;
				}
			}
		}
		handler.setWeapon(resw);
	}

	private void wieldCoolestMeleeWeapon() {
		Inventory inv = handler.getInventory();
		Weapon resw = null;
		for (Item i : inv.getItems()) {
			if (i instanceof Weapon) {
				Weapon w = (Weapon) i;
				if (!w.isMelee()) {
					continue;
				}
				if (getWeaponCoolness(w) > getWeaponCoolness(resw)) {
					resw = w;
				}
			}
		}
		handler.setWeapon(resw);
	}

	private int getWeaponCoolness(Weapon weapon) {
		if (weapon == null) {
			return handler.getHealthSystem().getCombatArm().getCost();
		}
		return weapon.getCost();
	}

	@Override
	public boolean isFightMode() {
		// Log.d("Time chasing player " + timeChasingBlindfold);
		return Game.player.isSeeMob(handler) || isChasingPlayer();
	}
}