package com.jackson.cyberpunk.health;

public class Kidneys extends Part{
	public Kidneys(String inInventoryName, String name, float specialValue, 
			float strength, int cost){
		super(Type.KIDNEYS, inInventoryName, name, 0.32f, specialValue, strength, cost);
	}
	
	@Override
	public Part deepCopyRealization(){
		return new Kidneys(inInventoryPicName, name, specialValue, strength, cost);
	}
}