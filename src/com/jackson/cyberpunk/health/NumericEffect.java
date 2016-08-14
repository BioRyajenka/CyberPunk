package com.jackson.cyberpunk.health;

import java.util.List;

public class NumericEffect extends Effect {
	public enum Type implements Effect.Type {
		MOVING_AP, MANIPULATION_AP, OVERALL_AP, PAIN, MOOD, HUNGER, SUCCESS_CHANCE, IMMUNITY;
	}

	public enum InfluenceType {
		SUMMAND, MULTIPLIER;
	}

	private InfluenceType influenceType;
	private float value;

	public NumericEffect(Type type, InfluenceType influenceType, float value) {
		super(type);
		this.influenceType = influenceType;
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public InfluenceType getInfluenceType() {
		return influenceType;
	}

	private static float getSummaryEffect2(List<Effect> effects, NumericEffect.Type type,
			NumericEffect.InfluenceType influenceType) {
		float res = influenceType == InfluenceType.SUMMAND ? 0 : 1f;
		for (Effect e : effects) {
			if (e instanceof NumericEffect) {
				NumericEffect ne = (NumericEffect) e;
				if (ne.influenceType == influenceType && ne.getType() == type) {
					if (influenceType == InfluenceType.SUMMAND) {
						res += ((NumericEffect) e).getValue();
					} else {
						res *= ((NumericEffect) e).getValue();
					}
				}
			}
		}
		return res;
	}

	public static float getSummaryEffect(List<Effect> effects, NumericEffect.Type type) {
		return getSummaryEffect(0, effects, type);
	}
	
	public static float getSummaryEffect(float baseValue, List<Effect> effects, NumericEffect.Type type) {
		float summand = getSummaryEffect2(effects, type, InfluenceType.SUMMAND);
		float multiplier = getSummaryEffect2(effects, type, InfluenceType.MULTIPLIER);
		return (summand + baseValue) * multiplier;
	}
}
