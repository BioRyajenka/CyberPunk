package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.health.InjuryManager.InjuryDesc;

public class Injury {
	private InjuryDesc desc;
	private int treated;
	private TreatmentQuality treatment;
	private boolean isFatal;

	public Injury(InjuryDesc desc, boolean isFatal) {
		this.desc = desc;
		this.isFatal = isFatal;
		treatment = TreatmentQuality.BAD;
	}

	// גûחûגאועסÿ גמ גנולÿ part.update()
	public void healSlightly() {
		if (treated < desc.timeToNaturallyHeals)
			treated++;
	}

	public void treat(TreatmentQuality quality) {
		treatment = quality;
		treated = desc.timeToNaturallyHeals;
	}

	public float getHurt() {
		float fact = treatment.getQualityMultiplier();
		return desc.pain * (fact + (1 - fact) * (1 - 1f * treated
				/ desc.timeToNaturallyHeals));
	}

	public float getPain() {
		return desc.pain * (1 - 1f * treated / desc.timeToNaturallyHeals);
	}

	public float getBleeding() {
		return desc.bleeding * (1 - 1f * treated / desc.timeToNaturallyHeals);
	}

	public boolean isFatal() {
		return isFatal;
	}

	public enum TreatmentQuality {
		BAD(.1f), GOOD(.05f), WELL(.002f);

		private float qualityMultiplier;

		private TreatmentQuality(float qualityMultiplier) {
			this.qualityMultiplier = qualityMultiplier;
		}

		public float getQualityMultiplier() {
			return qualityMultiplier;
		}
	};
}