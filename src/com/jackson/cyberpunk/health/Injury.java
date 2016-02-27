package com.jackson.cyberpunk.health;

public class Injury {
	private String name;
	private String description;
	private String treatedDescription; 
	private float pain;
	private float bleeding;
	private int naturalTreatTime;
	private boolean combines; 
	private String fatalDescription;
	
	private int treated;
	private TreatmentQuality treatment;

	public Injury(String name, String description, String treatedDescription, 
			float pain, float bleeding, int naturalTreatTime, boolean combines, 
			String fatalDescription) {
		this.name = name;
		this.description = description;
		this.treatedDescription = treatedDescription;
		this.pain = pain;
		this.bleeding = bleeding;
		this.naturalTreatTime = naturalTreatTime;
		this.combines = combines;
		this.fatalDescription = fatalDescription;
		
		treated = 0;
		treatment = TreatmentQuality.BAD;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	// вызывается во время part.update()
	public void healSlightly() {
		if (treated < getTreatTime()) {
			treated++;
		}
	}

	public void treat(TreatmentQuality quality) {
		treated = (int) (treated / treatment.treatTimeFactor * quality.treatTimeFactor);
		treatment = quality;
	}

	private int getTreatTime() {
		return (int) (naturalTreatTime * treatment.treatTimeFactor);
	}

	private float getTreatProgress() {
		return 1 - 1f * treated / getTreatTime();
	}

	/**
	 * Возвращает модификатор (множитель) повреждения органа.
	 * Это та штука, сумма которой будет умножаться на part.strength
	 */
	public float getHurt() {
		float mult = treatment.qualityMultiplier;
		return pain * (mult + (1 - mult) * getTreatProgress());
	}

	public float getPain() {
		return pain * getTreatProgress();
	}

	public float getBleeding() {
		return bleeding * getTreatProgress();
	}

	public enum TreatmentQuality {
		BAD(.1f, 1f), GOOD(.05f, 1/2), WELL(.002f, 1/3);

		/**
		 * Это есть остаточные повреждения органа
		 */
		private float qualityMultiplier;
		/**
		 * Это есть какую часть от времени естественного лечения составляет 
		 * время лечения при данном качестве лечения
		 */
		private float treatTimeFactor;

		private TreatmentQuality(float qualityMultiplier, float treatTimeFactor) {
			this.qualityMultiplier = qualityMultiplier;
			this.treatTimeFactor = treatTimeFactor;
		}
	}

	/**
	 * Создает новый экземпляр, который совпадает с данным по всем параметрам, 
	 * кроме treatment и treated (они равны BAD и 0 соответственно)
	 */
	public Injury copy() {
		return new Injury(name, description, treatedDescription, pain, bleeding, naturalTreatTime, combines, fatalDescription);
	}
}