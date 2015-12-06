package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;

public class Knapsack extends Item{
	private int capacity;
	
	public Knapsack(String picName, String name, 
			float weight, int sizeI, int sizeJ, int cost, int capacity){
		super("knapsacks/" + picName, name, weight, 
				sizeI, sizeJ, cost);
		this.capacity = capacity;
	}
	
	public int getCapacity(){
		return capacity;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}
}