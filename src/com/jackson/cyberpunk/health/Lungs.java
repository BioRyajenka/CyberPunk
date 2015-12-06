package com.jackson.cyberpunk.health;

public class Lungs extends Part {
	public Lungs(String name, float specialValue, float strength,
			int cost, boolean basePart) {
		super(Type.LUNGS, "plastic_container", name, 1.1f, specialValue, strength, cost,
				basePart);
	}

	@Override
	public Part deepCopyRealization() {
		return new Lungs(name, specialValue, strength, cost,
				basePart);
	}
}