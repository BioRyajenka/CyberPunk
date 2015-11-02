package com.jackson.cyberpunk;

import java.util.LinkedList;

import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.IWeapon;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Door;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.IntPair;

public class ContextMenu {
	public static enum Type {
		INV_DROP, INV_PICK, INV_UNLOAD_RIFLE, INV_WIELD, INV_UNWIELD, INV_LOAD_RIFLE, LVL_PICK, LVL_GO, LVL_INFO, LVL_ATTACK, LVL_OPEN_DOOR, LVL_CLOSE_DOOR
	};

	private LinkedList<Type> items;

	public ContextMenu() {
		items = new LinkedList<Type>();
	}

	public static String getItemText(Type item) {
		switch (item) {
		case INV_DROP:
			return "Бросить";
		case INV_PICK:
			return "Взять";
		case INV_UNLOAD_RIFLE:
			return "Разрядить";
		case INV_LOAD_RIFLE:
			return "Зарядить";
		case INV_WIELD:
			return "Взять в руки";
		case INV_UNWIELD:
			return "Положить в инвентарь";
		case LVL_PICK:
			return "Осмотреть лут";
		case LVL_GO:
			return "Идти";
		case LVL_INFO:
			return "Разглядеть";
		case LVL_ATTACK:
			return "Атаковать";
		case LVL_OPEN_DOOR:
			return "Открыть дверь";
		case LVL_CLOSE_DOOR:
			return "Закрыть дверь";
		default:
			return "хз";
		}
	}

	public static void onSelect(Type menuItem, Object context) {
		Level level = Game.level;
		Player pl = Game.player;
		Cell[][] cells = level.getCells();
		Floor pc = (Floor) cells[pl.getI()][pl.getJ()];
		Inventory inv = pl.getInventory();

		Item item = null;
		Cell cell = null;
		Door d = null;
		Inventory inv2 = null;
		IWeapon w = null;

		if (context instanceof Item) {
			item = (Item) context;
		}
		if (context instanceof Cell) {
			cell = (Cell) context;
		}
		if (cell instanceof Door) {
			d = (Door) cell;
		}
		if (pc.getLoot().getItems().contains(item)) {
			inv2 = pc.getLoot();
		} else {
			inv2 = pl.getInventory();
		}
		if (item instanceof IWeapon) {
			w = (IWeapon) item;
		}

		switch (menuItem) {
		case INV_DROP:
			if (pc.getLoot().canAdd(item)) {
				pc.addItem(item);
				inv.remove(item);
			} else {
				LogText.add("На клетке не хватает места для " + item);
			}
			break;
		case INV_PICK:
			if (inv.canAdd(item)) {
				pc.remove(item);
				inv.add(item);
			} else {
				LogText.add("Не хватает места в инвентаре для " + item);
			}
			break;
		case INV_UNLOAD_RIFLE:
			if (w.getAmmo() == 0)
				break;
			Ammo.Type type = w.getAmmoType();
			Ammo ammo = new Ammo(type, w.getAmmo());
			ammo.setAmount(w.getAmmo());
			if (inv.canAdd(ammo)) {
				w.setAmmo(0);
				inv.add(ammo);
			} else {
				LogText.add("Не хватает места в инвентаре для " + ammo);
			}
			break;
		case INV_LOAD_RIFLE:
			if (!pl.loadRifle(w)) {
				LogText.add("В инвентаре нет подходящих патронов");
			}
			break;
		case INV_WIELD:
			pl.setWeapon((IWeapon) item);
			inv2.remove(item);
			break;
		case INV_UNWIELD:
			if (inv.canAdd(item)) {
				pl.setWeapon(pl.getHealthSystem().getArm());
				inv.add(item);
			} else {
				LogText.add("Не хватает места в инвентаре для " + item);
			}
			break;
		case LVL_PICK:
			pl.travelToTheCell(cell.getI(), cell.getJ());
			pl.addOnTravelFinish(new Runnable() {
				public void run() {
					MyScene.inventoryWindow.show();
				}
			});
			break;
		case LVL_GO:
			pl.travelToTheCell(cell.getI(), cell.getJ());
			break;
		case LVL_INFO:
			MyScene.isSceneBlocked = true;
			Mob m = cell.getMob();
			MyScene.newMessage(m.getName() + "\nздоровье: " + (int) m.getHealthSystem()
					.getHealth() + "/100\nБоль: " + (int) m.getHealthSystem().getPain()
					+ "/100\nAction: " + m.getAction() + "\nВидим для тебя: " + (pl
							.isSeeMob(m) ? "да" : "нет") + "\nВидит тебя: " + (m
									.isSeeMob(pl) ? "да" : "нет"));
			break;
		case LVL_ATTACK:
			// pl.travelToTheCell(cell.getI(), cell.getJ());
			// pl.addOnTravelFinish(new Runnable(){
			// public void run(){
			w = pl.getWeapon();
			if (!w.isMelee() && w.getAmmo() == 0) {
				LogText.add("Нужно перезарядить оружие");
			} else {
				pl.attack(cell.getMob());
			}
			// }
			// });
			break;
		case LVL_OPEN_DOOR:
			final Door fd = d;
			IntPair cl = findClosestNotEqual(pl.getI(), pl.getJ(), d.getI(), d.getJ());
			pl.travelToTheCell(cl.first, cl.second);
			pl.addOnTravelFinish(new Runnable() {
				@Override
				public void run() {
					fd.setOpened(true);
					pl.checkFightMode();
				}
			});
			break;
		case LVL_CLOSE_DOOR:
			final Door fd2 = d;
			cl = findClosestNotEqual(pl.getI(), pl.getJ(), d.getI(), d.getJ());
			pl.travelToTheCell(cl.first, cl.second);
			pl.addOnTravelFinish(new Runnable() {
				@Override
				public void run() {
					fd2.setOpened(false);
					pl.checkFightMode();
				}
			});
			break;

		default:
			Log.e("ContextMenu.java: smth wrong");
			break;
		}

		MyScene.inventoryWindow.refresh();
	}

	private static IntPair findClosestNotEqual(int fromI, int fromJ, int toI, int toJ) {
		Cell[][] cells = Game.level.getCells();
		int n = cells.length;
		int m = cells[0].length;
		int x[][] = Game.level.bfs(fromI, fromJ, true);
		int resi = -1;
		int resj = -1;
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				if (di == 0 && dj == 0)
					continue;
				if (di != 0 && dj != 0)
					continue;
				int ni = toI + di;
				int nj = toJ + dj;
				if (!Utils.inBounds(ni, 0, n - 1) || !Utils.inBounds(nj, 0, m - 1)) {
					continue;
				}
				if (x[ni][nj] == -1)
					continue;
				if (resi == -1 || x[ni][nj] < x[resi][resj]) {
					resi = ni;
					resj = nj;
				}
			}
		}
		return new IntPair(resi, resj);
	}

	public void add(Type item) {
		if (items.contains(item)) {
			Log.e("ContextMenu.java: items already contains " + item.name());
			return;
		}
		items.add(item);
	}

	public void clear() {
		items.clear();
	}

	public LinkedList<Type> getItems() {
		return items;
	}
}