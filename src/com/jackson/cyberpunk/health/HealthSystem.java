package com.jackson.cyberpunk.health;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.health.Part.Type;
import com.jackson.cyberpunk.item.ItemsManager;
import com.jackson.cyberpunk.item.PartProfit;

public class HealthSystem {
	private Set<Part> parts;
	private float satiety, paintreshold;

	private HealthSystemView view;

	public HealthSystem() {
		parts = new HashSet<Part>();

		DualPart dp = (DualPart) ItemsManager.getItem("arm");
		dp.setLeft(true);
		parts.add(dp);

		dp = (DualPart) ItemsManager.getItem("arm");
		dp.setLeft(false);
		parts.add(dp);

		dp = (DualPart) ItemsManager.getItem("leg");
		dp.setLeft(true);
		parts.add(dp);

		dp = (DualPart) ItemsManager.getItem("leg");
		dp.setLeft(false);
		parts.add(dp);

		// parts.add((Part) ItemsManager.getItem("brain"));

		/*Log.d("HealthSystem created");
		for (Part p : parts) {
			Log.d("" + p);
		}*/

		satiety = 100;
		paintreshold = 90;
	}

	/**
	 * if {@code p} is DualPart, then the orientation of it must be set
	 * It must be call only in explore mode [and only on player]
	 */
	public void addPart(Part newP) {
		if (newP.type != Type.ARM && newP.type != Type.LEG && newP.type != Type.EYE) {
			return;
		}
		Part toRemove = null;
		for (Part p : parts) {
			if (p.type == newP.type && ((DualPart) p).isLeft() == ((DualPart) newP)
					.isLeft()) {
				toRemove = p;
				break;
			}
		}
		if (toRemove != null) {
			parts.remove(toRemove);
		}
		parts.add(newP);
		Game.player.refreshLeftActionPointsAndTurnFinished();
		
		updateView();
	}

	public void update() {
		// тут лечим и обновляем голод
		for (Part p : parts) {
			p.update();
		}

		satiety -= 70f / 100;

		updateView();
	}

	public void updateView() {
		for (Part p : parts) {
			p.updateStateView();
		}
		if (view != null) {
			view.update();
		}
	}

	public Arm getCombatArm() {
		Arm res = null;
		for (Part p : parts) {
			if (p.getType() == Type.ARM) {
				if (res == null || res.getHealth() < p.getHealth()) {
					res = (Arm) p;
				}
			}
		}
		return res;
	}

	public float getPain() {
		float res = 0;
		for (Part p : parts) {
			res += p.getPain();
		}
		return res;
	}

	public float getSight() {
		for (Part p : parts) {
			if (p.getType() == Type.EYE) {
			}
		}
		return 1f;// TODO:
	}

	/**
	 * Get moving action points with respect to corresponding part condition
	 */
	public int getMovingAP() {
		return getSummaryIntProfitValue(PartProfit.Type.MOVING_AP);
	}

	/**
	 * Get manipulation action points with respect to corresponding part
	 * condition
	 */
	public float getManipulationAP() {
		return getSummaryFloatProfitValue(PartProfit.Type.MANIPULATION_AP);
	}

	private float getSummaryFloatProfitValue(PartProfit.Type type) {
		float res = 0;
		for (Part p : parts) {
			for (PartProfit pr : p.getProfits()) {
				if (pr.getType() == type) {
					res += pr.getFloatValue() * (p.getHealth() / 100);
				}
			}
		}
		return res;
	}

	private int getSummaryIntProfitValue(PartProfit.Type type) {
		int res = 0;
		for (Part p : parts) {
			for (PartProfit pr : p.getProfits()) {
				if (pr.getType() == type) {
					res += pr.getIntValue();
				}
			}
		}
		return res;
	}

	public float getHealth() {
		float res = 0;
		for (Part p : parts) {
			res += p.getHealth();
		}
		res /= parts.size();
		res *= Math.max(0, 1 - getPain() / 100);
		return res;
	}

	public float getSatiety() {
		return satiety;
	}

	public List<Part> getParts() {
		return new ArrayList<>(parts);
	}

	public HealthSystemView getView() {
		if (view == null)
			view = new HealthSystemView(this);
		return view;
	}

	public boolean checkPaintreshold() {
		return getPain() >= paintreshold;
	}

	public boolean checkStarvation() {
		return satiety <= .02f;
	}
}

/*
 * Рубцы(scarring), parts biological/mechanical, голод, болезни.
 * 
 * Parts: 2eyes, 2legs, 2arms, lungs, kidneys, heart isExists, injuries,
 * getHealth()
 * 
 * Injuries: Cut, Scratch, Bite, Gunshot, Shredded, Bruise Pain, Combines?,
 * Bleeding, Naturally heals?, destroyed desc, treated desc, isTreated. if
 * treated, heals fast and pains less.
 * 
 * Health Systems: getSight, getMoving, getManipulation, getSatiety,
 * getImmunity(изначально 1f, при болезнях ослабляется, при лечении улучшается)
 * 
 * Buffs: Infection, Anesthetic, Pain, Starvation, Blood loss, Shock, Periodical
 * consciousness losing(todo) Name, natural treating time, effect(runnable),
 * cure()
 * 
 * FULL: Health Systems: Consciousness, Data Processing, Sight, Hearing, Moving,
 * Manipulation, Breathing, Blood Filtration, Blood Pumping, Metabolism
 */