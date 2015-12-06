package com.jackson.cyberpunk.item;

import java.util.ArrayList;
import java.util.List;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.mob.Player;

public class SurgeryKit extends Item {
	private static final int CAPABILITY = 10; 
	private List<Item> items;
	
	public SurgeryKit() {
		super("surgery_kit", "Хирургический набор", 1f, 2, 2, 1000);
		items = new ArrayList<>();
	}
	
	public void add(Item i) {
		if (items.size() == CAPABILITY) {
			//TODO: drop
			return;
		}
		items.add(i);
	}
	
	public void remove(Item i) {
		items.remove(i);
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public void clear() {
		items.clear();
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		for (Item i : items) {
			menu.add(Type.INV_REMOVE_FROM_CONTAINER, i);
		}
		Player p = Game.player;
		for (Item i : p.getInventory().getItems()) {
			if (i instanceof IMedicine) {
				menu.add(Type.INV_ADD_TO_CONTAINER, i);
			}
		}
		return menu;
	}
}