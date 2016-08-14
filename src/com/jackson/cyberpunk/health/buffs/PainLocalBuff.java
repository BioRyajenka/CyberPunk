package com.jackson.cyberpunk.health.buffs;

import java.util.stream.Collectors;

import com.jackson.cyberpunk.health.NumericEffect;
import com.jackson.cyberpunk.health.NumericEffect.InfluenceType;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.health.buffs.Buff.PartBuff;

public class PainLocalBuff extends PartBuff {
	public PainLocalBuff(Part part) {
		super(part, null, null);
	}

	@Override
	protected int checkLevel() {
		return 0;
	}

	@Override
	protected void update2() {
		float pain = holder.getInjuries().stream().collect(Collectors.summingDouble(i -> i
				.getPain())).floatValue();
		holder.applyEffect(new NumericEffect(NumericEffect.Type.PAIN, InfluenceType.SUMMAND,
				pain));
	}
}