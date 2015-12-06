package com.jackson.cyberpunk.health;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jackson.myengine.Log;

public class HealthSystem {
	private enum Type {
		LEFT_EYE, RIGHT_EYE, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG, KIDNEYS, LUNGS, HEART, BRAIN
	};

	private Map<Type, Part> parts;
	private float satiety, paintreshold;

	private HealthSystemView view;

	public HealthSystem() {
		parts = new HashMap<Type, Part>();

		Eye eye = (Eye) PartsManager.getSimple(Part.Type.EYE);
		eye.setLeft(true);
		parts.put(Type.LEFT_EYE, eye);
		eye = (Eye) PartsManager.getSimple(Part.Type.EYE);
		eye.setLeft(false);
		parts.put(Type.RIGHT_EYE, eye);

		Arm arm = (Arm) PartsManager.getSimple(Part.Type.ARM);
		arm.setLeft(true);
		parts.put(Type.LEFT_ARM, arm);
		arm = (Arm) PartsManager.getSimple(Part.Type.ARM);
		arm.setLeft(false);
		parts.put(Type.RIGHT_ARM, arm);

		Leg leg = (Leg) PartsManager.getSimple(Part.Type.LEG);
		leg.setLeft(true);
		parts.put(Type.LEFT_LEG, leg);
		leg = (Leg) PartsManager.getSimple(Part.Type.LEG);
		leg.setLeft(false);
		parts.put(Type.RIGHT_LEG, leg);

		parts.put(Type.KIDNEYS, PartsManager.getSimple(Part.Type.KIDNEYS));
		parts.put(Type.LUNGS, PartsManager.getSimple(Part.Type.LUNGS));
		parts.put(Type.HEART, PartsManager.getSimple(Part.Type.HEART));
		parts.put(Type.BRAIN, PartsManager.getSimple(Part.Type.BRAIN));

		Log.d("HealthSystem created");
		for (Part p : parts.values())
			Log.d("" + p.getName());

		satiety = 100;
		paintreshold = 90;
	}

	public void update() {
		// тут лечим и обновляем голод
		for (Part p : parts.values())
			p.update();

		satiety -= 70f / 100;
	}

	public void updateView() {
		for (Part p : parts.values())
			p.updateStateView();
		if (view != null)
			view.update();
	}

	public void setPart(Part part) {
		Type type = null;
		if (part instanceof IDualPart)
			type = Type.valueOf((((IDualPart) part).isLeft() ? "LEFT_" : "RIGHT_") + part
					.getType().name());
		else
			type = Type.valueOf(part.getType().name());
		parts.put(type, part);
	}

	public Arm getArm() {
		Arm l = (Arm) parts.get(Type.LEFT_ARM);
		Arm r = (Arm) parts.get(Type.RIGHT_ARM);
		if (l.isExists() && r.isExists())
			return (l.getHealth() > r.getHealth() ? l : r);
		return (l.isExists() ? l : r);
	}

	public float getHealth() {
		float res = 0;
		for (Part p : parts.values())
			res += p.getHealth();
		res /= parts.values().size();
		res *= Math.max(0, 1 - getPain() / 100);
		return res;
	}

	public float getPain() {
		float res = 0;
		for (Part p : parts.values())
			res += p.getPain();
		return res;
	}

	public float getSight() {
		float left = parts.get(Type.LEFT_EYE).getSpecialValue(), right = parts.get(
				Type.RIGHT_EYE).getSpecialValue();
		return (left + right) / 2;
	}

	public int getMoving() {
		return (int) (parts.get(Type.LEFT_LEG).getSpecialValue() + parts.get(
				Type.RIGHT_LEG).getSpecialValue());
	}

	public float getManipulation() {
		float left = parts.get(Type.LEFT_ARM).getSpecialValue(), right = parts.get(
				Type.RIGHT_ARM).getSpecialValue();
		return (left + right) / 2;
	}

	public float getSatiety() {
		return satiety;
	}

	public float getImmunity() {
		return parts.get(Type.KIDNEYS).getHealth();
	}

	public Collection<Part> getParts() {
		return parts.values();
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

	public boolean checkLethalParts() {
		return !(parts.get(Type.BRAIN).isExists() && parts.get(Type.HEART).isExists()
				&& parts.get(Type.KIDNEYS).isExists() && parts.get(Type.LUNGS)
						.isExists());
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