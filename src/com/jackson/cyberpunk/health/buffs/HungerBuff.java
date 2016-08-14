package com.jackson.cyberpunk.health.buffs;

import com.jackson.cyberpunk.health.HealthSystem;

public class HungerBuff extends Buff<HealthSystem> {
	// from -100 to 100
	private float satiety = 100;

	private static float HUNGER = 70f / 100;

	public HungerBuff(HealthSystem holder) {
		super(holder, "hunger", new String[] { 
				"Голод\nНебольшой дебаф к настроению",
				"Сильный голод\nДебаф к настроению", 
				"Смертельный голод\nДебаф к настроению\nВерятность смерти от истощения" });
	}

	@Override
	protected int checkLevel() {
		int res = 4;
		if (satiety > 5f) {
			res = 3;
		}
		if (satiety > 30f) {
			res = 2;
		}
		if (satiety > 60f) {
			res = 1;
		}
		if (satiety > 90f) {
			res = 0;
		}
		return res;
	}

	@Override
	protected void update2() {
		satiety -= HUNGER;
		switch (level) {
		case 0:
			break;
		case 1:
			holder.affectMood(-MoodBuff.SMALL_MOOD_BUFF);
			break;
		case 2:
			holder.affectMood(-MoodBuff.MEDIUM_MOOD_BUFF);
			break;
		case 3:
			holder.affectMood(-MoodBuff.MEDIUM_MOOD_BUFF);
			break;
		case 4:
			holder.die(HealthSystem.DieReason.STARVATION);
			break;
		}
	}
}