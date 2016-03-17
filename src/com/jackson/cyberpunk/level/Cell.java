package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Mob.Action;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Log;

public abstract class Cell {
	protected boolean isPassable;
	/**
	 * ���������� �� seeCell ���������� ��������������� ��������
	 */
	private boolean isVisibleForPlayer;
	/**
	 * ���������, ��� ���-�� ��� ������������ � ��� ������
	 */
	private boolean isDenyTravelling = false;

	// blood can not be exist
	protected Mob mob;
	protected int posI, posJ;

	protected CellView view;
	protected Class<? extends CellView> viewClass;

	public Cell(int posI, int posJ, boolean isPassable,
			Class<? extends CellView> viewClass) {
		this.posI = posI;
		this.posJ = posJ;
		this.isPassable = isPassable;
		this.viewClass = viewClass;
	}

	/**
	 * It's called from CellView
	 */
	public ContextMenu getContextMenu() {
		Player p = Game.player;
		ContextMenu res = new ContextMenu();
		if (hasMob() && getMob().getAction() == Action.NOTHING && p.isSeeMob(getMob())) {
			res.add(Type.LVL_INFO);
			if (getMob() != p) {
				res.add(Type.LVL_ATTACK, null, p.getAttackAPCost());
			}
		}
		return onContextMenuCreate(res);
	}

	protected abstract ContextMenu onContextMenuCreate(ContextMenu menu);

	public CellView getView() {
		if (view == null)
			try {
				view = viewClass.getConstructor(this.getClass()).newInstance(this);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Cell.java:getView(): " + e);
			}
		return view;
	}

	public void setMob(Mob m) {
		mob = m;
		if (m != null) {
			mob.setPos(posI, posJ);
		}
	}

	public void remove(final Mob m) {
		if (!m.equals(mob)) {
			Log.e("Trying to remove not appropriate man");
			return;
		}
		mob.setPos(-1, -1);
		mob = null;
	}

	public Mob getMob() {
		return mob;
	}

	public boolean hasMob() {
		return mob != null;
	}

	public boolean isDenyTravelling() {
		return isDenyTravelling;
	}

	public void setDenyTravelling(boolean isDenyTravelling) {
		this.isDenyTravelling = isDenyTravelling;
	}

	public int getI() {
		return posI;
	}

	public int getJ() {
		return posJ;
	}

	public boolean isPassable() {
		return isPassable;
	}

	public boolean isVisibleForPlayer() {
		return isVisibleForPlayer;
	}

	public void setVisibleForPlayer(boolean isVisibleForPlayer) {
		this.isVisibleForPlayer = isVisibleForPlayer;
	}
}