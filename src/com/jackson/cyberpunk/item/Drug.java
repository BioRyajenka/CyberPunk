package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;

public class Drug extends Item {
	public Drug(String name, String description, String pictureName, int sizeI, int sizeJ,
			int cost) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return null;
	}

	@Override
	public Item copy() {
		return null;
	}
}
