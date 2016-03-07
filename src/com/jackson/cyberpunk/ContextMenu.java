package com.jackson.cyberpunk;

import java.util.LinkedList;

import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.MeleeWeapon;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Door;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.IntPair;
import com.jackson.myengine.Utils.Pair;

public class ContextMenu {
	public static enum Type {
		INV_DROP, INV_PICK, INV_UNLOAD_RIFLE, INV_WIELD, INV_UNWIELD, INV_LOAD_RIFLE,
		/*INV_AMPUTATE, INV_IMPLANT, INV_REMOVE_FROM_CONTAINER, INV_ADD_TO_CONTAINER,*/
		LVL_PICK, LVL_GO, LVL_INFO, LVL_ATTACK, LVL_OPEN_DOOR, LVL_CLOSE_DOOR
	};

	private LinkedList<Pair<Type, Object>> items;

	public ContextMenu() {
		items = new LinkedList<>();
	}

	public static String getItemText(Pair<Type, Object> p) {
		Type type = p.first;
		// Object tag = p.second;
		switch (type) {
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
		/*case INV_AMPUTATE:
			return "Ампутировать " + tag;
		case INV_IMPLANT:
			return "Имплантировать " + tag;
		case INV_ADD_TO_CONTAINER:
			return "Положить " + tag;
		case INV_REMOVE_FROM_CONTAINER:
			return "Достать " + tag;*/
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

	public static void onSelect(Type menuItem, Object tag, Object context) {
		Level level = Game.level;
		Player pl = Game.player;
		Cell[][] cells = level.getCells();
		Floor pc = (Floor) cells[pl.getI()][pl.getJ()];
		Inventory inv = pl.getInventory();

		final Item item = (context instanceof Item ? (Item) context : null);
		Cell cell = null;
		Door d = null;
		Inventory inv2 = (pc.getLoot().getItems().contains(item) ? pc.getLoot()
				: pl.getInventory());
		RangedWeapon ranged = null;
		// MeleeWeapon melee = null;
		final Weapon weapon = (item instanceof Weapon ? (Weapon) item : null);

		if (context instanceof Cell) {
			cell = (Cell) context;
		}
		if (cell instanceof Door) {
			d = (Door) cell;
		}
		if (item instanceof RangedWeapon) {
			ranged = (RangedWeapon) item;
		}
		if (item instanceof MeleeWeapon) {
			// melee = (MeleeWeapon) item;
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
			if (ranged.getAmmo() == 0) {
				break;
			}
			Ammo ammo = new Ammo(ranged.getAmmo());
			if (inv.canAdd(ammo)) {
				ranged.setAmmo(0);
				inv.add(ammo);
			} else {
				LogText.add("Не хватает места в инвентаре для " + ammo);
			}
			break;
		case INV_LOAD_RIFLE:
			if (!inv.reloadRifle(ranged)) {
				LogText.add("В инвентаре нет подходящих патронов");
			}
			break;
		case INV_WIELD:
			Game.engine.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					if (!unwield(pl.getWeapon())) {
						LogText.add("Не хватает места в инвентаре для " + item);
						InventoryWindow.getInstance().refresh();
						return;
					}
					pl.setWeapon(weapon);
					inv2.remove(weapon);
					InventoryWindow.getInstance().refresh();
				}
			});
			break;
		case INV_UNWIELD:
			if (!unwield((Weapon) item)) {
				LogText.add("Не хватает места в инвентаре для " + item);
			}
			break;
		/*case INV_AMPUTATE:
			//painkillers, antibiotics, knife, bandages, disinfectants
			//if (inv.contains())
			break;
		case INV_IMPLANT:
			//painkillers, antibiotics, knife, bandages, disinfectants
			break;
		case INV_ADD_TO_CONTAINER:
			break;
		case INV_REMOVE_FROM_CONTAINER:
			break;*/
		case LVL_PICK:
			pl.travelToTheCell(cell.getI(), cell.getJ());
			pl.addOnTravelFinish(new Runnable() {
				public void run() {
					InventoryWindow.getInstance().show();
				}
			});
			break;
		case LVL_GO:
			pl.travelToTheCell(cell.getI(), cell.getJ());
			break;
		case LVL_INFO:
			Mob m = cell.getMob();
			if (m == null) {
				// occurs when clicking before somebody dies
				break;
			}
			MyScene.isSceneBlocked = true;
			MyScene.newMessage(m.getName() + "\nздоровье: " + (int) m.getHealthSystem()
					.getHealth() + "/100\nБоль: " + (int) m.getHealthSystem().getPain()
					+ "/100\nAction: " + m.getAction() + "\nВидим для тебя: " + (pl
							.isSeeMob(m) ? "да" : "нет") + "\nВидит тебя: " + (m
									.isSeeMob(pl) ? "да" : "нет"));
			break;
		case LVL_ATTACK:
			Weapon w = pl.getWeapon();
			if (w != null && w.isRanged()) {
				// ranged
				if (((RangedWeapon) w).getAmmo() == 0) {
					LogText.add("Нужно перезарядить оружие");
				} else {
					pl.attack(cell.getMob());
				}
			} else {
				// melee
				IntPair pa = findClosestNotEqual(pl.getI(), pl.getJ(), cell.getI(), cell
						.getJ());
				final Mob fmob = cell.getMob();
				pl.travelToTheCell(pa.first, pa.second);
				pl.addOnTravelFinish(new Runnable() {
					public void run() {
						pl.attack(fmob);
					}
				});
			}
			break;
		case LVL_OPEN_DOOR:
			final Door fd = d;
			IntPair cl = findClosestNotEqual(pl.getI(), pl.getJ(), d.getI(), d.getJ());
			pl.travelToTheCell(cl.first, cl.second);
			pl.addOnTravelFinish(new Runnable() {
				@Override
				public void run() {
					if (fd.getLockType() == LockType.NONE) {
						fd.setOpened(true);
					} else {
						if (inv.containsKey(fd.getLockType())) {
							fd.setOpened(true);
							inv.removeKey(fd.getLockType());
							LogText.add(fd.getLockType().getName()
									+ " ключ был удален из инвентаря");
						} else {
							LogText.add("Нужен ключ, чтобы открыть дверь");
						}
					}
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
					if (fd2.getLoot().getItems().isEmpty()) {
						fd2.setOpened(false);
					} else {
						LogText.add("Что-то мешает тебе закрыть дверь.");
					}
				}
			});
			break;

		default:
			Log.e("ContextMenu.java: smth wrong");
			break;
		}

		InventoryWindow.getInstance().refresh();
	}

	private static boolean unwield(Weapon w) {
		if (w == null) {
			return true;
		}
		Player pl = Game.player;
		Inventory inv = pl.getInventory();
		if (inv.canAdd(w)) {
			pl.setWeapon(null);
			inv.add(w);
			return true;
		} else {
			return false;
		}
	}

	private static IntPair findClosestNotEqual(int fromI, int fromJ, int toI, int toJ) {
		Cell[][] cells = Game.level.getCells();
		int n = cells.length;
		int m = cells[0].length;
		int x[][] = Game.level.bfs(fromI, fromJ);
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
		add(item, null);
	}

	public void add(Type item, Object tag) {
		Pair<Type, Object> p = new Pair<Type, Object>(item, tag);
		if (items.contains(p)) {
			Log.e("ContextMenu.java: items already contains " + item.name());
			return;
		}
		items.add(p);
	}

	public void clear() {
		items.clear();
	}

	public LinkedList<Pair<Type, Object>> getItems() {
		return items;
	}
}