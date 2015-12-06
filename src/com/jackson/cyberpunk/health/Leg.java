package com.jackson.cyberpunk.health;

public class Leg extends Part implements IDualPart {
	private boolean isLeft;

	public Leg(String inInventoryName, String name, float weight, float specialValue,
			float strength, int cost, boolean basePart) {
		super(Type.LEG, "parts/" + inInventoryName, name, weight, specialValue, strength,
				cost, basePart);
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public String getName() {
		return (isLeft ? "левая " : "правая ") + super.getName();
	}

	@Override
	public Part deepCopyRealization() {
		return new Leg(inInventoryPicName, name, weight, specialValue, strength, cost,
				basePart);
	}
}