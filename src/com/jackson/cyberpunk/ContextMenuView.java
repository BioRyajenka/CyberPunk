package com.jackson.cyberpunk;

import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.gui.Button;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Utils;

public class ContextMenuView extends Entity {
	private static ContextMenuView singleton = null;
	private static float ITEM_WIDTH = 130, ITEM_HEIGHT = 20;

	private static Object context;
	private static ItemView itemsv[];
	private static Sprite bg;

	private ContextMenuView() {
		bg = new Sprite(0, 0, "gui/context_menu_bg");
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
		bg.setSize(ITEM_WIDTH + 10, ITEM_HEIGHT * menu.getItems().size() + 10);
		for (int i = 0; i < menu.getItems().size(); i++) {
			itemsv[i].set(menu.getItems().get(i));
			itemsv[i].show();
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

	class ItemView extends Entity {
		Button b;

		public ItemView(int i) {
			b = new Button(5, 5 + ITEM_HEIGHT * i, "õç");
			b.setImage("gui/context_menu_button");
			b.setNegativeMode(true);
			b.getTextEntity().setColor(1f, 1f, 1f);
			attachChild(b);
		}

		public void set(final Type it) {
			b.setText(ContextMenu.getItemText(it));
			b.setAction(new Runnable() {
				public void run() {
					ContextMenu.onSelect(it, context);
					finish();
				}
			});
		}
	}
}