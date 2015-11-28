package com.jackson.cyberpunk.health;

public class Heart extends Part {
	public Heart(String inInventoryName, String name, float specialValue, float strength,
			int cost, boolean basePart) {
		super(Type.HEART, inInventoryName, name, 0.73f, specialValue, strength, cost,
				basePart);
	}

	@Override
	public Part deepCopyRealization() {
		return new Heart(inInventoryPicName, name, specialValue, strength, cost,
				basePart);
	}
}