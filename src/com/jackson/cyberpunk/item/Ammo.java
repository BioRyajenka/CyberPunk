package com.jackson.cyberpunk.item;

public class Ammo extends CountableItem {
	public Ammo(int amount) {
		super("ammo", "������� ������������� �������", "res/items/ammos/gun", 1, 1, 30,
				40);
		setAmount(amount);
	}
}