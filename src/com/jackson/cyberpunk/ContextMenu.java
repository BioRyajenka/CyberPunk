package com.jackson.cyberpunk;

import java.util.LinkedList;

import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.Corpse;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.MeleeWeapon;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Door;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.cyberpunk.level.Level;
import com.jackson.cyberpunk.level.RepairStation;
import com.jackson.cyberpunk.level.Station;
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
		INV_AMPUTATE, /*INV_REMOVE_FROM_CONTAINER, INV_ADD_TO_CONTAINER,*/
		LVL_PICK, LVL_GO, LVL_OPEN_DOOR, LVL_CLOSE_DOOR, LVL_USE_REPAIR_STATION, LVL_HACK,
		MOB_INFO, MOB_ATTACK, MOB_DEALER_INSTALL_IMPLANT, NOT_ACTIVE,
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
		case INV_AMPUTATE:
			return "������������ " + ((Part) tag).getDescription();
		/*case INV_IMPLANT:
			return "�������� " + tag;
		case INV_REMOVE_FROM_CONTAINER:
			return "������� " + tag;*/
		case LVL_PICK:
			return "��������� ���";
		case LVL_GO:
			return "����";
		case LVL_OPEN_DOOR:
			return "������� �����";
		case LVL_CLOSE_DOOR:
			return "������� �����";
		case LVL_USE_REPAIR_STATION:
			return "�������� " + ((Part) tag).getDescription();
		case LVL_HACK:
			return "��������";
		case MOB_INFO:
			return "����������";
		case MOB_ATTACK:
			return "���������";
		case MOB_DEALER_INSTALL_IMPLANT:
			return "���������� " + ((Part) tag).getDescription();
		case NOT_ACTIVE:
			return (String) tag;
		default:
			return "��";
		}
	}

	public static void onSelect(Type menuItem, Object tag, Object context, float mAPCost) {
		Level level = Game.level;
		Player pl = Game.player;
		Cell[][] cells = level.getCells();
		Floor pc = (Floor) cells[pl.getI()][pl.getJ()];
		Inventory inv = pl.getInventory();
		HealthSystem hs = pl.getHealthSystem();

		final Item item = (context instanceof Item ? (Item) context : null);
		Cell cell = null;
		Door d = null;
		Part part = (Part) tag; // can cast safe even if null
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
			pc.addItem(item);
			inv.remove(item);
			pl.spendManipulationAP(mAPCost);
			break;
		case INV_PICK:
			if (inv.canAdd(item)) {
				pc.remove(item);
				inv.add(item);
				pl.spendManipulationAP(mAPCost);
			} else {
				GameLog.add("�� ������� ����� � ��������� ��� " + item);
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
				pl.spendManipulationAP(mAPCost);
			} else {
				GameLog.add("�� ������� ����� � ��������� ��� " + ammo);
			}
			break;
		case INV_LOAD_RIFLE:
			if (inv.reloadRifle(ranged)) {
				pl.spendManipulationAP(mAPCost);
			} else {
				GameLog.add("� ��������� ��� ���������� ��������");
			}
			break;
		case INV_WIELD:
			Game.engine.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					if (unwield(pl.getWeapon())) {
						// pay AP only once
					} else {
						GameLog.add("�� ������� ����� � ��������� ��� " + item);
						InventoryWindow.getInstance().refresh();
						return;
					}
					pl.setWeapon(weapon);
					inv2.remove(weapon);
					InventoryWindow.getInstance().refresh();
					pl.spendManipulationAP(mAPCost);
				}
			});
			break;
		case INV_UNWIELD:
			if (unwield((Weapon) item)) {
				pl.spendManipulationAP(mAPCost);
			} else {
				GameLog.add("�� ������� ����� � ��������� ��� " + item);
			}
			break;
		case INV_AMPUTATE:
			if (!inv.containsSteelArms() && (hs.getLeftArm() == null || !hs.getLeftArm()
					.isSteelArms()) && (hs.getRightArm() == null || !hs.getRightArm()
							.isSteelArms())) {
				GameLog.add("���� ����� �������� ������ ��� �����.");
				break;
			}
			pc.addItem((Part) tag);
			((Corpse) context).removePart((Part) tag);
			break;
		/*case INV_IMPLANT:
			//TODO: painkillers, antibiotics, knife, bandages, disinfectants
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
		case LVL_OPEN_DOOR:
			final Door fd = d;
			IntPair cl = findClosestNotEqual(pl.getI(), pl.getJ(), d.getI(), d.getJ());
			pl.travelToTheCell(cl.first, cl.second);
			pl.addOnTravelFinish(new Runnable() {
				@Override
				public void run() {
					if (fd.getLockType() == LockType.NONE || fd.isHacked() || fd
							.isWasOpened()) {
						pl.spendManipulationAP(mAPCost);
						fd.setOpened(true);
					} else {
						if (inv.containsKey(fd.getLockType())) {
							pl.spendManipulationAP(mAPCost);
							fd.setOpened(true);
							inv.removeKey(fd.getLockType());
							GameLog.add(fd.getLockType().getName()
									+ " ���� ��� ������ �� ���������");
						} else {
							GameLog.add("����� ����, ����� ������� �����");
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
						pl.spendManipulationAP(mAPCost);
					} else {
						GameLog.add("�������� ������ ���� ������� �����.");
					}
				}
			});
			break;
		case LVL_USE_REPAIR_STATION:
			if (Game.getGameMode() == Mode.FIGHT) {
				GameLog.add("������ ������������ �� ����� ���");
				break;
			}
			if (part.getHealth() > .9f) {
				GameLog.add(part.getDescription() + " �� ��������� � �������.");
				break;
			}
			RepairStation rst = (RepairStation) cell;

			Runnable okAction = new Runnable() {
				@Override
				public void run() {
					if (!inv.removeMoney(rst.getRepairCost())) {
						Log.d("� ���� ��� ����� �����.");
						return;
					}
					for (int i = 0; i < 100; i++) {
						// heal slightly 100 times
						part.update();
					}
					GameLog.add(part + " ���� �������� �������������.");
				}
			};

			Message.showMessage("�� ������? ��� ����� ������ " + rst.getRepairCost() + " zm.",
					okAction);

			break;
		case LVL_HACK:
			if (Game.getGameMode() == Mode.FIGHT) {
				GameLog.add("������ ������������ �� ����� ���");
				break;
			}
			// TODO: � �������� ����� �� ������� ������
			if (cell instanceof Station) {
				Station st = (Station) cell;
				if (st.tryHack()) {
					Log.d("�������� ������� �������");
				} else {
					Log.d("����� �� ������. �������� ������������.");
				}
			} else if (cell instanceof Door) {
				if (d.tryHack()) {
					Log.d("����� ������� ��������");
				} else {
					Log.d("����� �� ������. ����� �������������.");
				}
			}
			break;
		case MOB_INFO:
			Mob m = cell.getMob();
			if (m == null) {
				// occurs when raising context menu before somebody dies
				break;
			}
			MyScene.isSceneBlocked = true;
			Message.showMessage(m.getName() + "\n��������: " + (int) m.getHealthSystem()
					.getAverageHealth() + "/100\nAction: " + m.getAction()
					+ "\n����� ��� ����: " + (pl.isSeeMob(m) ? "��" : "���") + "\n����� ����: "
					+ (m.isSeeMob(pl) ? "��" : "���"), null);
			break;
		case MOB_ATTACK:
			Weapon w = pl.getWeapon();
			if (w != null && w.isRanged()) {
				// ranged
				if (((RangedWeapon) w).getAmmo() == 0) {
					GameLog.add("����� ������������ ������");
				} else {
					if (!pl.hasEnoughAPToManipulate(pl.getAttackAPCost())) {
						GameLog.add("�� ������� ����� ��������");
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
						if (!pl.hasEnoughAPToManipulate(pl.getAttackAPCost())) {
							GameLog.add("�� ������� ����� ��������");
						} else {
							pl.attack(fmob);
						}
					}
				});
			}
			break;
		case MOB_DEALER_INSTALL_IMPLANT:
			if (Game.getGameMode() == Mode.FIGHT) {
				GameLog.add("������ ������������ �� ����� ���");
				break;
			}
			IntPair p = findClosestNotEqual(pl.getI(), pl.getJ(), cell.getI(), cell.getJ());
			if (p.first == -1) {
				GameLog.add("�� ���� ��������� ����");
				break;
			}
			pl.travelToTheCell(p.first, p.second);
			pl.addOnTravelFinish(new Runnable() {
				@Override
				public void run() {
					pl.getHealthSystem().addPart(part);
					pl.getInventory().remove(part);
					GameLog.add(part.getDescription() + " ���� �����������");
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

	public void addAll(ContextMenu another) {
		for (ContextMenuItem i : another.items) {
			add(i.type, i.tag, i.manipulationAPCost);
		}
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