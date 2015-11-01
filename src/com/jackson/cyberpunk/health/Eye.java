package com.jackson.cyberpunk.health;

public class Eye extends Part implements IDualPart {
    private boolean isLeft;

    public Eye(String inInventoryName, String name, float specialValue,
	    float strength, int cost) {
	super(Type.EYE, inInventoryName, name, 0.02f, specialValue, strength,
		cost);
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
	return new Eye(inInventoryPicName, name, specialValue, strength, cost);
    }
}