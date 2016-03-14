package com.jackson.cyberpunk;

import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.gui.Button;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.Pair;

public class ContextMenuView extends Entity {
	private static ContextMenuView singleton = null;
	private static float ITEM_WIDTH = 130, ITEM_HEIGHT = 20;

	private static Object context;
	private static ItemView itemsv[];
	private static Sprite bg;

	private ContextMenuView() {
		bg = new Sprite(0, 0, "res/gui/context_menu_bg");
		attachChild(bg);
		itemsv = new ItemView[10];
		for (int i = 0; i < 10; i++) {
			itemsv[i] = new ItemView(i);
			attachChild(itemsv[i]);
		}
		hide();
	}

	public static void set(Object context, ContextMenu menu) {
		ContextMenuView.context = context;
		float width = ITEM_WIDTH + 10;
		for (int i = 0; i < menu.getItems().size(); i++) {
			itemsv[i].set(menu.getItems().get(i));
			width = Math.max(width, itemsv[i].getWidth() + 10);
			itemsv[i].show();
		}
		bg.setSize(width, ITEM_HEIGHT * menu.getItems().size() + 10);
		for (int i = 0; i < menu.getItems().size(); i++) {
			itemsv[i].setSize(width - 10, itemsv[i].getHeight());
		}
		for (int i = menu.getItems().size(); i < itemsv.length; i++)
			itemsv[i].hide();
		singleton.setPosition(MyScene.mx, MyScene.my);
		singleton.show();
	}

	@Override
	public void onManagedUpdate() {
		if (!isVisible())
			return;
		float mx = MyScene.mx, my = MyScene.my;
		if (((!Utils.inBounds(mx, bg.getGlobalX(), bg.getGlobalX() + bg.getWidth())
				|| !Utils.inBounds(my, bg.getGlobalY(), bg.getGlobalY() + bg
						.getHeight())) && (MyScene.isLeftPressed
								|| MyScene.isRightPressed)) || !isGlobalVisible()) {
			finish();
			return;
		}
		super.onManagedUpdate();
	}

	private static void finish() {
		singleton.hide();
	}

	public static ContextMenuView getInstance() {
		if (singleton == null) {
			singleton = new ContextMenuView();
		}
		return singleton;
	}

	static class ItemView extends Button {
		public ItemView(int i) {
			super(5, 5 + ITEM_HEIGHT * i, "");
			setImage("res/gui/context_menu_button");
			getTextEntity().setColor(1f, 1f, 1f);
		}

		public void set(final Pair<Type, Object> p) {
			setActive(p.first != Type.NOT_ACTIVE);
			setText(ContextMenu.getItemText(p));
			setSize(Math.max(ITEM_WIDTH, getTextEntity().getWidth() + 10), getHeight());
			setAction(new Runnable() {
				public void run() {
					ContextMenu.onSelect(p.first, p.second, context);
					finish();
				}
			});
		}
	}
}