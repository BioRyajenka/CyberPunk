package com.jackson.cyberpunk.health;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.jackson.cyberpunk.health.Part.Type;
import com.jackson.cyberpunk.health.buffs.Buff;
import com.jackson.cyberpunk.health.buffs.HungerBuff;
import com.jackson.cyberpunk.health.buffs.MoodBuff;
import com.jackson.cyberpunk.health.buffs.PainCommonBuff;
import com.jackson.cyberpunk.health.buffs.WithdrawalManager;
import com.jackson.cyberpunk.item.Drug;
import com.jackson.cyberpunk.item.ItemManager;
import com.jackson.myengine.Utils;

public final class HealthSystem {
	private Set<Part> parts = new HashSet<Part>();
	private Set<Buff<HealthSystem>> commonBuffs = new HashSet<>();
	private List<Effect> commonEffects = new ArrayList<>();

	public enum ArmOrientation {
		LEFT, RIGHT;

		public static ArmOrientation getRandom() {
			return Utils.dice() < 0.07f ? LEFT : RIGHT;
		}
	}

	private ArmOrientation armOrientation;

	public enum DieReason {
		MISSINGPART("отсутствия важных органов"), STARVATION("голода"), PAINFUL_SHOCK(
				"болевого шока");

		private String text;

		private DieReason(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	};

	private boolean dead;
	private DieReason dieReason;

	public void die(DieReason dieReason) {
		dead = true;
		this.dieReason = dieReason;
	}

	public boolean isDead() {
		return dead;
	}

	public DieReason getDieReason() {
		return dieReason;
	}

	public HealthSystem(ArmOrientation orientation) {
		armOrientation = orientation;

		DualPart dp = (DualPart) ItemManager.getItem("arm");
		dp.setLeft(true);
		parts.add(dp);

		dp = (DualPart) ItemManager.getItem("arm");
		dp.setLeft(false);
		parts.add(dp);

		dp = (DualPart) ItemManager.getItem("leg");
		dp.setLeft(true);
		parts.add(dp);

		dp = (DualPart) ItemManager.getItem("leg");
		dp.setLeft(false);
		parts.add(dp);

		commonBuffs.add(new HungerBuff(this));
		commonBuffs.add(new PainCommonBuff(this));
		commonBuffs.add(new MoodBuff(this));
	}

	public void addBuff(Buff<HealthSystem> buff) {
		commonBuffs.add(buff);
	}

	public void applyEffect(Effect e) {
		commonEffects.add(e);
	}

	public float getSummaryEffectWithoutRegardToHealth(NumericEffect.Type type) {
		float res = parts.stream().collect(Collectors.summingDouble(p -> NumericEffect
				.getSummaryEffect(p.getEffects(), type))).floatValue();
		return NumericEffect.getSummaryEffect(res, commonEffects, type);
	}

	public List<Buff<?>> getAllBuffs() {
		List<Buff<?>> res = new ArrayList<>(commonBuffs);
		res.addAll(parts.stream().flatMap(p -> p.getBuffs().stream()).collect(Collectors
				.toList()));
		return res;
	}

	@SuppressWarnings("unchecked")
	private <T> T getBuff(Class<T> clazz) {
		return (T) commonBuffs.stream().filter(p -> p.getClass() == clazz).findFirst().get();
	}

	public void receptDrug(Drug drug) {
		getBuff(WithdrawalManager.class).receptDrug(drug);
	}

	public void affectMood(float value) {
		getBuff(MoodBuff.class).affectMood(value);
	}

	/**
	 * @return mood value, from -100f to 100f
	 */
	public float getMood() {
		return getBuff(MoodBuff.class).getMood();
	}

	public void update() {
		// clearing effects
		commonEffects.clear();
		// updating buffs and parts. strictly in this order
		parts.forEach(p -> p.update());
		commonBuffs.forEach(b -> b.update());
	}
	
	public void updatePainCommonBuff() {
		getBuff(PainCommonBuff.class).update();
	}

	/**
	 * if {@code p} is DualPart, then the orientation of it must be set.
	 * 
	 * @return emplaced part or null
	 */
	public Part addPart(Part newP) {
		Part prevP = null;
		for (Part p : parts) {
			if (p.type == newP.type && ((DualPart) p).isLeft() == ((DualPart) newP).isLeft()) {
				prevP = p;
				break;
			}
		}
		if (prevP != null) {
			parts.remove(prevP);
		}
		parts.add(newP);
		return prevP;
	}

	// TODO: consider this and implement buff icon
	// static final float NOT_APPROPRIATE_ARM_PENALTY = .1f;

	private float getArmAP(Part arm) {
		return (arm.getHealth() / 100) * NumericEffect.getSummaryEffect(arm.getEffects(),
				NumericEffect.Type.MANIPULATION_AP);
	}

	// TODO: check for null everywhere where using this method
	public Arm getCombatArm() {
		Arm res = null;
		for (Part p : parts) {
			if (p.type == Type.ARM && (res == null || getArmAP(p) > getArmAP(res))) {
				res = (Arm) p;
			}
		}
		return res;
	}

	public float getSight() {
		return 1f;// TODO:
	}

	public float getSuccessChance() {
		return NumericEffect.getSummaryEffect(1, commonEffects,
				NumericEffect.Type.SUCCESS_CHANCE);
	}

	private float getSummaryEffectWithRegardToHealth(NumericEffect.Type type) {
		float res = parts.stream().collect(Collectors.summingDouble(p -> (p.getHealth() / 100)
				* NumericEffect.getSummaryEffect(p.getEffects(), type))).floatValue();
		return NumericEffect.getSummaryEffect(res, commonEffects, type);
	}

	/**
	 * Get moving action points with respect to corresponding part condition
	 */
	public float getMovingAP() {
		float res = getSummaryEffectWithRegardToHealth(NumericEffect.Type.MOVING_AP);
		return NumericEffect.getSummaryEffect(res, commonEffects,
				NumericEffect.Type.OVERALL_AP);
	}

	public Arm getLeftArm() {
		for (Part p : parts) {
			if (p instanceof Arm && ((Arm) p).isLeft()) {
				return (Arm) p;
			}
		}
		return null;
	}

	public Arm getRightArm() {
		for (Part p : parts) {
			if (p instanceof Arm && !((Arm) p).isLeft()) {
				return (Arm) p;
			}
		}
		return null;
	}

	/**
	 * Get manipulation action points with respect to corresponding part
	 * condition
	 */
	public float getManipulationAP(boolean twoHanded) {
		Arm workArm = armOrientation == ArmOrientation.LEFT ? getLeftArm() : getRightArm();
		Arm anotherArm = armOrientation == ArmOrientation.LEFT ? getRightArm() : getLeftArm();
		if (workArm == null && anotherArm == null) {
			return 0;
		}
		float workArmAP = workArm == null ? 0 : getArmAP(workArm);
		float anotherArmAP = anotherArm == null ? 0 : getArmAP(anotherArm);
		float res = getSummaryEffectWithRegardToHealth(NumericEffect.Type.MANIPULATION_AP);
		if (twoHanded) {
			return res;
		}
		return res - Math.min(workArmAP, anotherArmAP);
	}

	public ArmOrientation getArmOrientation() {
		return armOrientation;
	}

	public float getAverageHealth() {
		float res = 0;
		for (Part p : parts) {
			res += p.getHealth();
		}
		res /= parts.size();
		return res;
	}

	public List<Part> getParts() {
		return new ArrayList<>(parts);
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