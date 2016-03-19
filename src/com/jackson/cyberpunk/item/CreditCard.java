package com.jackson.cyberpunk.item;

public class CreditCard extends CountableItem {
	public CreditCard(int value) {
		super("credits", "Кредиты", "res/items/credit_card", 1, 1, value, 10000);
		setAmount(value);
	}
	
	@Override
	public int getCost() {
		return getAmount();
	}
}
