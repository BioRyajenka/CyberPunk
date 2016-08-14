package com.jackson.cyberpunk.item;

import java.util.LinkedList;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.health.Injury;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.Pair;

public abstract class Weapon extends Item {
	public static float WIELD_AP_COST = .1f;
	public static float UNWIELD_AP_COST = .1f;
	
	protected InjuryHelper helper;
	protected boolean twoHanded;
	protected float attackAP;

	protected Weapon(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost, boolean twoHanded, float attackAP, InjuryHelper helper) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.helper = helper;
		this.twoHanded = twoHanded;
		this.attackAP = attackAP;
	}
	
	public boolean isTwoHanded() {
		return twoHanded;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		Player pl = Game.player;

		if (pl.getWeapon() != this) {
			menu.add(Type.INV_WIELD, null, WIELD_AP_COST);
		}
		if (pl.getWeapon() != null && pl.getWeapon() == this) {
			menu.add(Type.INV_UNWIELD, null, UNWIELD_AP_COST);
		}
		return menu;
	}

	public abstract boolean isMelee();

	public boolean isRanged() {
		return !isMelee();
	}
	
	public float getAttackAP() {
		return attackAP;
	}

	public InjuryHelper getInjuryHelper() {
		return helper;
	}

	public static class InjuryHelper {
		private LinkedList<Pair<Injury, Float>> pairs;

		public InjuryHelper() {
			pairs = new LinkedList<>();
		}

		@SafeVarargs
		public InjuryHelper(Pair<Injury, Float>... pairs) {
			for (Pair<Injury, Float> p : pairs) {
				add(p);
			}
		}

		public void add(Injury i, Float f) {
			pairs.add(new Pair<Injury, Float>(i, f));
		}

		public void add(Pair<Injury, Float> p) {
			pairs.add(p);
		}

		/**
		 * @param power
		 *            from 0 to 1f
		 */
		private Injury getInjury(float power) {
			for (int i = 0; i < pairs.size(); i++) {
				Pair<Injury, Float> p = pairs.get(i);
				if (p.second >= power) {
					return pairs.get(i).first;
				}
			}
			return pairs.getLast().first;
		}

		public Injury getRandomInjury() {
			return getInjury(Utils.rand.nextFloat());
		}
	}
}