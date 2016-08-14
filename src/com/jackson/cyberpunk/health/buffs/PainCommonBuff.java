package com.jackson.cyberpunk.health.buffs;

import java.util.List;
import java.util.stream.Collectors;

import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.NumericEffect;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.health.NumericEffect.InfluenceType;

public class PainCommonBuff extends Buff<HealthSystem> {
	public PainCommonBuff(HealthSystem holder) {
		super(holder, "pain", new String[] { "Боль\nНебольшое уменьшение ОД",
				"Сильная боль\nУменьшение ОД", "Нестерпимая боль\nСерьезное уменьшение ОД\n"
						+ "Верятность смерти от болевого шока" });
	}

	@Override
	protected int checkLevel() {
		List<Part> parts = holder.getParts();
		float partsPain = parts.stream().collect(Collectors.summingDouble(p -> (double) 
				NumericEffect.getSummaryEffect(p.getEffects(), NumericEffect.Type.PAIN))).floatValue();
		holder.applyEffect(new NumericEffect(NumericEffect.Type.PAIN, InfluenceType.SUMMAND,
				partsPain));
		float pain = holder.getPain();
		if (pain > .9f) {
			return 4;
		}
		if (pain > .6f) {
			return 3;
		}
		if (pain > .4f) {
			return 2;
		}
		if (pain > .15f) {
			return 1;
		}
		return 0;
	}

	@Override
	protected void update2() {
		if (level == 4) {
			holder.die(HealthSystem.DieReason.PAINFUL_SHOCK);
			return;
		}
	}
}
