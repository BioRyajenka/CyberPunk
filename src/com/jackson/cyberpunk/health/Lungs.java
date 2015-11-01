package com.jackson.cyberpunk.health;

public class Lungs extends Part{
	public Lungs(String inInventoryName, String name, float specialValue, 
			float strength, int cost){
		super(Type.LUNGS, inInventoryName, name, 1.1f, specialValue, strength, cost);
	}
	
	@Override
	public Part deepCopyRealization(){
		return new Lungs(inInventoryPicName, name, specialValue, strength, cost);
	}
}