package com.jackson.cyberpunk.health.buffs;

import com.jackson.cyberpunk.health.BooleanEffect;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.NumericEffect;
import com.jackson.cyberpunk.health.NumericEffect.InfluenceType;

public class MoodBuff extends Buff<HealthSystem> {
	public MoodBuff(HealthSystem holder) {
		super(holder, "depression", new String[] {
				"Депрессия\nНебольшой дебаф к ОД\nПлохой аппетит",
				"Глубокая депрессия\nАпатия: есть вероятность того, что во время боя "
						+ "персонаж откажется выполнять приказы\n"
						+ "Плохой аппетит\nНемного снижен иммунитет", });
	}

	public static float SMALL_MOOD_BUFF = 100f / 100;
	public static float MEDIUM_MOOD_BUFF = 200f / 100;
	public static float LARGE_MOOD_BUFF = 400f / 100;

	// from -100 to 100 and further
	private float mood = 0;

	/**
	 * mood tends to zero value with speed mood * (MOOD_REGRESSION_SPEED_FACTOR
	 * / 100). i.e. when mood == 100, speed is exactly this value
	 */
	public static float MOOD_REGRESSION_SPEED_FACTOR = 50f / 100;

	public void affectMood(float value) {
		mood += value;
	}

	@Override
	protected int checkLevel() {
		if (mood < -100)
			return 2;
		if (mood < -60)
			return 1;
		return 0;
	}

	@Override
	protected void update2() {
		float moodDelta = mood * (MOOD_REGRESSION_SPEED_FACTOR / 100);
		if (mood > 0) {
			mood = Math.max(0, mood - moodDelta);
		} else {
			mood = Math.max(0, mood + moodDelta);
		}

		// hs.clearEffectsFromRequester(this);
		// это вызывается снаружи
		if (level == 1) {
			holder.applyEffect(new NumericEffect(NumericEffect.Type.OVERALL_AP,
					InfluenceType.MULTIPLIER, .9f));
			holder.applyEffect(new BooleanEffect(BooleanEffect.Type.APPETITE));
		}
		if (level == 2) {
			holder.applyEffect(new BooleanEffect(BooleanEffect.Type.APATY));
			holder.applyEffect(new NumericEffect(NumericEffect.Type.IMMUNITY,
					InfluenceType.SUMMAND, -5f));
			holder.applyEffect(new BooleanEffect(BooleanEffect.Type.APPETITE));
		}
	}

	public float getMood() {
		return mood;
	}
}
