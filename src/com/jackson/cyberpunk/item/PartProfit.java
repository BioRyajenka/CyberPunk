package com.jackson.cyberpunk.item;

public class PartProfit {
	public enum Type {
		MOVING_AP, MANIPULATION_AP
	}
	
	private Type type;
	private String value;
	
	public PartProfit(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public PartProfit(Type type) {
		this.type = type;
	}
	
	public int getIntValue() {
		return Integer.parseInt(value);
	}
	
	public float getFloatValue() {
		return Float.parseFloat(value);
	}
	
	public Type getType() {
		return type;
	}
}
