package com.jackson.cyberpunk.health;

public class Brain extends Part{
	public Brain(String inInventoryName, String name, float specialValue, 
			float strength, int cost){
		super(Type.BRAIN, inInventoryName, name, 3.4f, specialValue, strength, cost);
	}
	
	@Override
	public Part deepCopyRealization(){
		return new Brain(inInventoryPicName, name, specialValue, strength, cost);
	}
}