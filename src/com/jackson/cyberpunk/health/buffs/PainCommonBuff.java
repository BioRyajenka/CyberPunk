package com.jackson.cyberpunk.health.buffs;

import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.NumericEffect;

public class PainCommonBuff extends Buff<HealthSystem> {
	public PainCommonBuff(HealthSystem holder) {
		super(holder, null, null);
	}

	@Override
	protected int checkLevel() {
		float pain = holder.getSummaryEffectWithoutRegardToHealth(NumericEffect.Type.PAIN);
		if (pain > 300f) {
			return 4;
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
