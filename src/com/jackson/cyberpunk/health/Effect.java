package com.jackson.cyberpunk.health;

public abstract class Effect {
	public interface Type {
	}

	private Type type;
	
	protected Effect(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
