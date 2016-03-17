package com.jackson.cyberpunk;

import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemView;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IlluminatedSprite;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Utils;

public class InventoryGridView extends Entity {
	public static final float CELL_WIDTH = 44;

	private Entity itemsEntity;

	private Sprite bg;
	private InventoryCellView invCells[][];
	private Inventory inventory;

	public InventoryGridView() {
		int n = 8, m = 5;
		itemsEntity = new Entity() {
			@Override
			public String toString() {
				return "itemsEntity";
			}
		};

		bg = new Sprite(0, 0, "res/gui/inventory_bg");
		bg.setSize(m * (CELL_WIDTH - .5f), n * CELL_WIDTH);
		attachChild(bg);

		invCells = new InventoryCellView[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				invCells[i][j] = new InventoryCellView(i, j);
				invCells[i][j].setBlackout(.5f);
				attachChild(invCells[i][j]);
			}
		}

		attachChild(itemsEntity);
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
		clear();
		for (Item e : inventory.getItems()) {
			ItemView iv = e.getView();
			iv.restoreSize();
			iv.setPosition(e.getJ() * CELL_WIDTH + (e.getSizeJ() * CELL_WIDTH - iv
					.getWidth()) / 2, e.getI() * CELL_WIDTH + (e.getSizeI() * CELL_WIDTH
							- iv.getHeight()) / 2);
			itemsEntity.attachChild(iv);
		}

		int n = inventory.getKnapsack().getCapacity() / 5, m = 5;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < m; j++)
				invCells[i][j].setVisible(i < n);

		bg.setSize(bg.getWidth(), n * CELL_WIDTH);
	}

	public void clear() {
		itemsEntity.detachChildren();
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (!isGlobalVisible() || ContextMenuView.getInstance().isVisible())
			return;

		float x = MyScene.mx, y = MyScene.my;
		int n = 8, m = 5, i1 = -1, j1 = -1;

		// затемняем
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				invCells[i][j].blackOut();
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (invCells[i][j].isSelected(x, y)) {
					invCells[i][j].blackIn();
					i1 = i;
					j1 = j;
				}
			}
		}

		if (i1 != -1 && j1 != -1) {
			Item item = null;
			for (Entity i : itemsEntity.getChildren()) {
				Item e = ((ItemView) i).getItem();
				if (Utils.inBounds(i1, e.getI(), e.getI() + e.getSizeI() - 1) && Utils
						.inBounds(j1, e.getJ(), e.getJ() + e.getSizeJ() - 1)) {
					item = e;
					break;
				}
			}

			if (item != null) {
				for (int i = item.getI(); i < item.getI() + item.getSizeI(); i++) {
					for (int j = item.getJ(); j < item.getJ() + item.getSizeJ(); j++) {
						invCells[i][j].blackIn();
					}
				}
			}
		}
	}

	public float getWidth() {
		int m = 5;
		float w = CELL_WIDTH;
		return m * (w - .5f);
	}

	private static class InventoryCellView extends IlluminatedSprite {
		public InventoryCellView(int i, int j) {
			super(j * (CELL_WIDTH - .5f), i * CELL_WIDTH, "res/gui/inventory_cell",
					IlluminationMode.IMPOSITION);
		}

		@Override
		public void onManagedUpdate() {
			
		}
	}
	
	@Override
	public String toString() {
		return "inventoryGridView";
	}
	
	public Entity getItemsEntity() {
		return itemsEntity;
	}
}