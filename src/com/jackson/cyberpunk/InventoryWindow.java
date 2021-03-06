package com.jackson.cyberpunk;

import java.util.ArrayList;
import java.util.List;

import com.jackson.cyberpunk.gui.Button;
import com.jackson.cyberpunk.gui.Panel;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemView;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IlluminatedSprite;
import com.jackson.myengine.IlluminatedSprite.IlluminationMode;

public class InventoryWindow extends Entity {
	private static InventoryWindow singleton = null;

	private Panel bg;
	private ItemView weaponView;
	private IlluminatedSprite weaponBG;
	private InventoryGridView leftGV, rightGV;

	private InventoryWindow() {
		Inventory inventory = Game.player.getInventory();

		bg = new Panel(0, 0, 560, 400);
		IlluminatedSprite knapsackBG = new IlluminatedSprite(12, 25,
				"res/gui/white_pixel", IlluminationMode.IMPOSITION);
		knapsackBG.setSize(InventoryGridView.CELL_WIDTH * 2, InventoryGridView.CELL_WIDTH
				* 3);
		ItemView knapsackView = inventory.getKnapsack().getView();
		knapsackView.setPosition((knapsackBG.getWidth() - knapsackView.getWidth()) / 2,
				(knapsackBG.getHeight() - knapsackView.getHeight()) / 2);
		knapsackBG.attachChild(knapsackView);

		weaponBG = new IlluminatedSprite(12, 35 + knapsackBG.getHeight(),
				"res/gui/white_pixel", IlluminationMode.IMPOSITION);
		weaponBG.setSize(InventoryGridView.CELL_WIDTH * 2, InventoryGridView.CELL_WIDTH);

		leftGV = new InventoryGridView() {
			public String toString() {
				return "leftGV";
			}
		};
		leftGV.setPosition(110, 25);
		rightGV = new InventoryGridView() {
			public String toString() {
				return "rightGV";
			};
		};
		rightGV.setPosition(leftGV.getX() + leftGV.getWidth() + 12, 25);

		Button close = new Button(15, 350, "�������");
		close.setAction(new Runnable() {
			public void run() {
				hide();
			}
		});

		attachChildren(bg, weaponBG, knapsackBG, leftGV, rightGV, close);
		setPosition(40, (Game.SCREEN_HEIGHT - bg.getHeight()) / 2);
	}

	List<Item> getItems() {
		List<Item> res = new ArrayList<>();
		for (Item i : leftGV.getInventory().getItems()) {
			res.add(i);
		}
		for (Item i : rightGV.getInventory().getItems()) {
			res.add(i);
		}
		res.add(weaponView.getItem());
		res.add(Game.player.getInventory().getKnapsack());

		return res;
	}

	public void refresh() {
		if (weaponView != null && weaponView.hasParent()) {
			weaponView.detachSelf();
		}
		leftGV.clear();
		rightGV.clear();

		Player pl = Game.player;
		Cell[][] cells = Game.level.getCells();
		Inventory cInv = ((Floor) cells[pl.getI()][pl.getJ()]).getLoot();

		if (cInv.getItems().isEmpty()) {
			bg.setSize(352.5f, 400);
			rightGV.hide();
		} else {
			bg.setSize(569.5f, 400);
			rightGV.show();
		}

		leftGV.setInventory(pl.getInventory());
		rightGV.setInventory(cInv);

		Weapon weapon = pl.getWeapon();
		weaponView = (weapon == null ? pl.getHealthSystem().getCombatArm() : weapon)
				.getView();

		weaponBG.attachChild(weaponView);
		float w = weaponBG.getWidth() * .9f;
		float h = weaponBG.getHeight() * .9f;
		weaponView.setSize(w, h);
		weaponView.setPosition(weaponBG.getWidth() / 2 - w / 2, weaponBG.getHeight() / 2
				- h / 2);
	}

	@Override
	public void show() {
		MyScene.isSceneBlocked = true;
		refresh();
		super.show();
	}

	@Override
	public void hide() {
		MyScene.isSceneBlocked = false;
		super.hide();
	}

	public static InventoryWindow getInstance() {
		if (singleton == null) {
			singleton = new InventoryWindow();
		}
		return singleton;
	}

	@Override
	public String toString() {
		return "inventoryWindow";
	}
}