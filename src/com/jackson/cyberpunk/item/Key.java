package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.level.Door.LockType;

public class Key extends Item {
	private LockType keyType;
	
	public Key(LockType keyType) {
		super("key", "электронный ключ", "res/items/keys/key", 1, 1, 0);
		this.keyType = keyType;
	}
	
	@Override
	public String getDescription() {
		return keyType.getName() + " " + name;
	}
	
	public LockType getKeyType() {
		return keyType;
	}
	
	@Override
	public ItemView getView() {
		if (view == null)
			view = new KeyView(this);
		return view;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	@Override
	public Item copy() {
		return new Key(keyType);
	}
}
