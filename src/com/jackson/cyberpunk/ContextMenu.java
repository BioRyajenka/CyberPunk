package com.jackson.cyberpunk;

import java.util.LinkedList;

import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.health.Part;
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

public class ContextMenu {
	// NOT_ACTIVE type corresponds to a not-active button and it's label will be
	// (String)tag
	public static enum Type {
		INV_DROP, INV_PICK, INV_UNLOAD_RIFLE, INV_WIELD, INV_UNWIELD, INV_LOAD_RIFLE,
		/*INV_AMPUTATE, INV_IMPLANT, INV_REMOVE_FROM_CONTAINER, INV_ADD_TO_CONTAINER,*/
		LVL_PICK, LVL_GO, LVL_INFO, LVL_ATTACK, LVL_OPEN_DOOR, LVL_CLOSE_DOOR, LVL_USE_REPAIR_STATION, NOT_ACTIVE
	};

	private LinkedList<ContextMenuItem> items;

	public ContextMenu() {
		items = new LinkedList<>();
	}

	public static String getItemText(ContextMenuItem i) {
		Type type = i.type;
		Object tag = i.tag;
		switch (type) {
		case INV_DROP:
			return "�������";
		case INV_PICK:
			return "�����";
		case INV_UNLOAD_RIFLE:
			return "���������";
		case INV_LOAD_RIFLE:
			return "��������";
		case INV_WIELD:
			return "����� � ����";
		case INV_UNWIELD:
			return "�������� � ���������";
		/*case INV_AMPUTATE:
			return "������������ " + tag;
		case INV_IMPLANT:
			return "�������������� " + tag;
		case INV_ADD_TO_CONTAINER:
			return "�������� " + tag;
		case INV_REMOVE_FROM_CONTAINER:
			return "������� " + tag;*/
		case LVL_PICK:
			return "��������� ���";
		case LVL_GO:
			return "����";
		case LVL_INFO:
			return "����������";
		case LVL_ATTACK:
			return "���������";
		case LVL_OPEN_DOOR:
			return "������� �����";
		case LVL_CLOSE_DOOR:
			return "������� �����";
		case LVL_USE_REPAIR_STATION:
			return "�������� " + ((Part) tag).getDescription();
		case NOT_ACTIVE:
			return (String) tag;
		default:
			return "��";
		}
	}

	public static void onSelect(Type menuItem, Object tag, Object context,
			float mAPCost) {
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
				pl.spendArmActionPoints(mAPCost);
			} else {
				LogText.add("�� ������ �� ������� ����� ��� " + item);
			}
			break;
		case INV_PICK:
			if (inv.canAdd(item)) {
				pc.remove(item);
				inv.add(item);
				pl.spendArmActionPoints(mAPCost);
			} else {
				LogText.add("�� ������� ����� � ��������� ��� " + item);
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
				pl.spendArmActionPoints(mAPCost);
			} else {
				LogText.add("�� ������� ����� � ��������� ��� " + ammo);
			}
			break;
		case INV_LOAD_RIFLE:
			if (inv.reloadRifle(ranged)) {
				pl.spendArmActionPoints(mAPCost);
			} else {
				LogText.add("� ��������� ��� ���������� ��������");
			}
			break;
		case INV_WIELD:
			Game.engine.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					if (unwield(pl.getWeapon())) {
						//pay AP only once
					} else {
						LogText.add("�� ������� ����� � ��������� ��� " + item);
						InventoryWindow.getInstance().refresh();
						return;
					}
					pl.setWeapon(weapon);
					inv2.remove(weapon);
					InventoryWindow.getInstance().refresh();
					pl.spendArmActionPoints(mAPCost);
				}
			});
			break;
		case INV_UNWIELD:
			if (unwield((Weapon) item)) {
				pl.spendArmActionPoints(mAPCost);
			} else {
				LogText.add("�� ������� ����� � ��������� ��� " + item);
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
				// occurs when raising context menu before somebody dies
				break;
			}
			MyScene.isSceneBlocked = true;
			MyScene.newMessage(m.getName() + "\n��������: " + (int) m.getHealthSystem()
					.getHealth() + "/100\n����: " + (int) m.getHealthSystem().getPain()
					+ "/100\nAction: " + m.getAction() + "\n����� ��� ����: " + (pl
							.isSeeMob(m) ? "��" : "���") + "\n����� ����: " + (m
									.isSeeMob(pl) ? "��" : "���"));
			break;
		case LVL_ATTACK:
			Weapon w = pl.getWeapon();
			if (w != null && w.isRanged()) {
				// ranged
				if (((RangedWeapon) w).getAmmo() == 0) {
					LogText.add("����� ������������ ������");
				} else {
					if (pl.getLeftArmActionPoints() < pl.getAttackAPCost()) {
						LogText.add("�� ������� ����� ��������");
					} else {
						pl.attack(cell.getMob());
					}
				}
			} else {
				// melee
				IntPair pa = findClosestNotEqual(pl.getI(), pl.getJ(), cell.getI(), cell
						.getJ());
				final Mob fmob = cell.getMob();
				pl.travelToTheCell(pa.first, pa.second);
				pl.addOnTravelFinish(new Runnable() {
					public void run() {
						if (pl.getLeftArmActionPoints() < pl.getAttackAPCost()) {
							LogText.add("�� ������� ����� ��������");
						} else {
							pl.attack(fmob);
						}
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
						pl.spendArmActionPoints(mAPCost);
						fd.setOpened(true);
					} else {
						if (inv.containsKey(fd.getLockType())) {
							pl.spendArmActionPoints(mAPCost);
							fd.setOpened(true);
							inv.removeKey(fd.getLockType());
							LogText.add(fd.getLockType().getName()
									+ " ���� ��� ������ �� ���������");
						} else {
							LogText.add("����� ����, ����� ������� �����");
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
						pl.spendArmActionPoints(mAPCost);
					} else {
						LogText.add("�������� ������ ���� ������� �����.");
					}
				}
			});
			break;
		case LVL_USE_REPAIR_STATION:
			if (Game.getGameMode() == Mode.FIGHT) {
				LogText.add("������ ������������ �� ����� ���");
				break;
			}
			Part p = (Part) tag;
			p.getInjuries().clear();
			LogText.add(p.getDescription() + " ��������� �������������.");
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
		add(item, tag, 0);
	}

	public void add(Type item, Object tag, float manipulationAPCost) {
		ContextMenuItem p = new ContextMenuItem(item, tag, manipulationAPCost);
		if (items.contains(p)) {
			Log.e("ContextMenu.java: items already contains " + item.name());
			return;
		}
		items.add(p);
	}

	public void clear() {
		items.clear();
	}

	public LinkedList<ContextMenuItem> getItems() {
		return items;
	}

	public static class ContextMenuItem {
		private Type type;
		private Object tag;
		private float manipulationAPCost;

		public ContextMenuItem(Type type, Object tag, float manipulationAPCost) {
			this.type = type;
			this.tag = tag;
			this.manipulationAPCost = manipulationAPCost;
		}

		public Type getType() {
			return type;
		}

		public Object getTag() {
			return tag;
		}

		public float getManipulationAPCost() {
			return manipulationAPCost;
		}
	}
}