package com.jackson.cyberpunk.health;

public class Eye extends Part implements IDualPart {
	private boolean isLeft;

	public Eye(String name, float specialValue, float strength, int cost,
			boolean basePart) {
		super(Type.EYE, "plastic_container", name, 0.02f, specialValue, strength, cost,
				basePart);
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public boolean isLeft() {
		return isLeft;
	}

	@Override
	public String getName() {
		return (isLeft ? "левый " : "правый ") + super.getName();
	}

	@Override
	public Part deepCopyRealization() {
		return new Eye(name, specialValue, strength, cost, basePart);
	}
}