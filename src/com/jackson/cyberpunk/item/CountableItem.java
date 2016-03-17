package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;

public class CountableItem extends Item {
	private int amount, stackSize;

	public CountableItem(String name, String description, String pictureName, int sizeI,
			int sizeJ, int cost, int stackSize) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.stackSize = stackSize;
	}
	
	@Override
	public String getDescription() {
		return description + ": " + amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public int getStackSize() {
		return stackSize;
	}

	public ItemView getView() {
		if (view == null) {
			view = new CountableItemView(this) {
				@Override
				protected int getAmount() {
					return CountableItem.this.getAmount();
				}
			};
		}
		return view;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	@Override
	public Item copy() {
		return null;
	}
}