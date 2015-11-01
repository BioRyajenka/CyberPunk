package com.jackson.cyberpunk.item;

public class CountableItem extends Item {
    private int amount, stackSize;

    public CountableItem(String inInventoryPicName, String name, float weight,
	    int sizeI, int sizeJ, int cost, int stackSize) {
	super(inInventoryPicName, name, weight, sizeI, sizeJ, cost);
	this.stackSize = stackSize;
    }

    public void setAmount(int amount) {
	// updating automaticaly
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
}
