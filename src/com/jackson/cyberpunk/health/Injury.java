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

	public Injury(String name, String description, String treatedDescription, float pain,
			float bleeding, int naturalTreatTime, boolean combines,
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

	// ���������� �� ����� part.update()
	public void healSlightly() {
		if (treated < getTreatTime()) {
			treated++;
		}
	}

	/**
	 * Setting up new treatment, recalculating left treatment time using
	 * previous treatment
	 * 
	 * @param quality - new treatment
	 */
	public void treat(TreatmentQuality quality) {
		treated = (int) (treated / treatment.treatTimeFactor * quality.treatTimeFactor);
		treatment = quality;
	}

	/**
	 * Returns total treatment time
	 */
	private int getTreatTime() {
		return (int) (naturalTreatTime * treatment.treatTimeFactor);
	}

	/**
	 * Returns left treatment progress in percents (from 0 to 1)
	 */
	private float getLeftTreatProgress() {
		return 1f - 1f * treated / getTreatTime();
	}

	/**
	 * ���������� ����������� (���������) ����������� ������. ��� �� �����,
	 * ����� ������� ����� ���������� �� part.strength
	 */
	public float getHurt() {
		float mult = treatment.qualityMultiplier;
		return pain * (mult + (1 - mult) * getLeftTreatProgress());
	}

	public float getPain() {
		return pain * getLeftTreatProgress();
	}

	public float getBleeding() {
		return bleeding * getLeftTreatProgress();
	}

	public enum TreatmentQuality {
		BAD(.1f, 1f), GOOD(.05f, 1f / 2), WELL(.002f, 1f / 3);

		/**
		 * ��� ���� ���������� ����������� ������
		 */
		private float qualityMultiplier;
		/**
		 * ��� ���� ����� ����� �� ������� ������������� ������� ����������
		 * ����� ������� ��� ������ �������� �������
		 */
		private float treatTimeFactor;

		private TreatmentQuality(float qualityMultiplier, float treatTimeFactor) {
			this.qualityMultiplier = qualityMultiplier;
			this.treatTimeFactor = treatTimeFactor;
		}
	}

	/**
	 * ������� ����� ���������, ������� ��������� � ������ �� ���� ����������,
	 * ����� treatment � treated (��� ����� BAD � 0 ��������������)
	 */
	public Injury copy() {
		return new Injury(name, description, treatedDescription, pain, bleeding,
				naturalTreatTime, combines, fatalDescription);
	}
}