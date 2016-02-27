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
	// >дамаг просчитывается из веса TODO: what????
	protected InjuryHelper helper;
	protected boolean twoHanded;//TODO:

	protected Weapon(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost, boolean twoHanded, InjuryHelper helper) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.helper = helper;
		this.twoHanded = twoHanded;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		Player pl = Game.player;

		if (pl.getWeapon() == null) {
			menu.add(Type.INV_WIELD);
		} else {
			if (pl.getWeapon().equals(this)) {
				menu.add(Type.INV_UNWIELD);
			}
		}
		return menu;
	}

	public abstract boolean isMelee();

	public boolean isRanged() {
		return !isMelee();
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
		 * @param power from 0 to 1f
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