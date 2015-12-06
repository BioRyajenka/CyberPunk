package com.jackson.cyberpunk.health;

public class Brain extends Part {
	public Brain(String name, float specialValue, float strength, int cost,
			boolean basePart) {
		super(Type.BRAIN, "plastic_container", name, 3.4f, specialValue, strength, cost,
				basePart);
	}

	@Override
	public Part deepCopyRealization() {
		return new Brain(name, specialValue, strength, cost, basePart);
	}
}