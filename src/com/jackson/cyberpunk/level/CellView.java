package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenuView;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IEntity;

public class CellView extends Entity {
	protected static final float WIDTH = 80;
	protected static final float HEIGHT = 40;

	protected Cell cell;

	public CellView(Cell cell) {
		this.cell = cell;
		setPosition(WIDTH / 2 * (cell.getJ() - cell.getI()), HEIGHT / 2 * (cell.getJ()
				+ cell.getI()));
		// mob views are attached directly to LevelView!
	}

	@Override
	public void onManagedUpdate() {
		Player pl = Game.player;
		Level level = Game.level;
		// Cell pc = level.getCells()[pl.getI()][pl.getJ()];
		LevelView lv = level.getView();
		float mx = MyScene.mx, my = MyScene.my;

		if (MyScene.isRightPressed && !MyScene.isSceneBlocked) {
			int i = lv.convertMousetoI(mx - lv.getX(), my - lv.getY()), j = lv
					.convertMousetoJ(mx - lv.getX(), my - lv.getY());
			if (i == cell.getI() && j == cell.getJ()) {
				ContextMenu menu = cell.getContextMenu();
				if (menu.getItems().size() == 0) {
					// move player
					if (pl.isReachableCell(i, j)) {
						pl.travelToTheCell(i, j);
					}
				} else
					ContextMenuView.set(cell, menu);
			}
		}

		super.onManagedUpdate();
	}

	/**
	 * x & y is global
	 */
	public boolean isSelected(float x, float y) {
		LevelView lv = Game.level.getView();
		return (lv.convertMousetoI(x, y) == cell.getI() && lv.convertMousetoJ(x,
				y) == cell.getJ());
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue) {
		super.setColor(pRed, pGreen, pBlue);
		for (int i = 0; i < Game.level.mobs_not_views.getChildCount(); i++) {
			IEntity e = Game.level.mobs_not_views.getChild(i);
			Mob m = ((Mob) e);
			if (m.getI() == cell.getI() && m.getJ() == cell.getJ())
				m.getView().setColor(pRed, pGreen, pBlue);
			else {
				m.getView().setColor(1f, 1f, 1f);
			}
		}

	}
}