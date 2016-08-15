package com.jackson.cyberpunk.health.buffs;

import java.util.stream.Collectors;

import com.jackson.cyberpunk.health.NumericEffect;
import com.jackson.cyberpunk.health.NumericEffect.InfluenceType;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.health.buffs.Buff.PartBuff;
import com.jackson.myengine.Log;

//pain indirectly affects AP (through health)
public class PainLocalBuff extends PartBuff {
	public PainLocalBuff(Part part) {
		super(part, "pain", new String[] {
				"Боль в " + part.getDescription() + "\nНебольшое уменьшение ОД",
				"Сильная боль в " + part.getDescription() + "\nУменьшение ОД", 
				"Нестерпимая боль в " + part.getDescription() + 
					"\nСерьезное уменьшение ОД\nВерятность смерти от болевого шока" 
		});
	}
	
	private float calcPain() {
		return holder.getInjuries().stream().collect(Collectors.summingDouble(i -> i
				.getPain())).floatValue();
	}

	@Override
	protected int checkLevel() {
		float pain = calcPain();
		if (pain > 60f) {
			return 3;
		}
		if (pain > 40f) {
			return 2;
		}
		if (pain > 15f) {
			return 1;
		}
		return 0;
	}

	@Override
	protected void update2() {
		float pain = calcPain();
		holder.applyEffect(new NumericEffect(NumericEffect.Type.PAIN, InfluenceType.SUMMAND,
				pain));
	}
}