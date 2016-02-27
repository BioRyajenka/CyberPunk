package com.jackson.cyberpunk.item;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenuView;
import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Utils;

public class ItemView extends Sprite {
	protected Item item;

	public ItemView(Item item) {
		super(0, 0, item.getPictureName());
		this.item = item;
	}

	@Override
	public void onManagedUpdate() {
		if (!isGlobalVisible())
			return;
		// right click
		float mx = MyScene.mx, my = MyScene.my;
		if (Utils.inBounds(mx - getGlobalX(), 0, getWidth()) && Utils.inBounds(my
				- getGlobalY(), 0, getHeight()) && MyScene.isRightPressed) {
			ContextMenu menu = item.getContextMenu();
			if (!menu.getItems().isEmpty()) {
				ContextMenuView.set(item, menu);
			}
		}
		super.onManagedUpdate();
	}

	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		return item.getDescription() + "View";
	}
}