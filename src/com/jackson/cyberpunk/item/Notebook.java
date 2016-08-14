package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;

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