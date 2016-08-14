package com.jackson.cyberpunk.health;

public class BooleanEffect extends Effect {
	public enum Type implements Effect.Type {
		APPETITE, APATY;
	}
	
	public BooleanEffect(Type type) {
		super(type);
	}
}
