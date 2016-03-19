package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.item.Item;

public class Notebook extends Item {
	public Notebook() {
		super("notebook", "Ноутбук", "res/items/notebook", 2, 2, 700);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	@Override
	public Item copy() {
		return new Notebook();
	}
}