package com.jackson.cyberpunk.health;

public class Kidneys extends Part {
	public Kidneys(String name, float specialValue, float strength, int cost,
			boolean basePart) {
		super(Type.KIDNEYS, "plastic_container", name, 0.32f, specialValue, strength, cost,
				basePart);
	}

	@Override
	public Part deepCopyRealization() {
		return new Kidneys(name, specialValue, strength, cost, basePart);
	}
}