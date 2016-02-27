package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;

public class Knapsack extends Item {
	private int capacity;

	public Knapsack(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost, int capacity) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	public Item copy() {
		return new Knapsack(name, description, pictureName, sizeI, sizeJ, cost,
				capacity);
	}
}