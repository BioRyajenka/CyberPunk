package com.jackson.cyberpunk.health;

public class Heart extends Part {
	public Heart(String name, float specialValue, float strength, int cost,
			boolean basePart) {
		super(Type.HEART, "plastic_container", name, 0.73f, specialValue, strength, cost,
				basePart);
	}

	@Override
	public Part deepCopyRealization() {
		return new Heart(name, specialValue, strength, cost, basePart);
	}
}