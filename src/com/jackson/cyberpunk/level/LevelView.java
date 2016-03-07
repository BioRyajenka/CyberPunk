package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;

public class LevelView extends Entity {
	private Level level;

	public LevelView(Level level) {
		Log.d("Creating level view");
		this.level = level;
		Cell[][] cells = level.getCells();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				Cell c = cells[i][j];
				attachChild(c.getView());
				if (c.getMob() != null) {
					c.getView().attachChild(c.getMob().getView());
					c.getMob().getView().setPosition(21, -47);
				}
				c.getView().hide();
			}
		}
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (MyScene.isSceneBlocked) {
			return;
		}

		Player pl = Game.player;
		Cell[][] cells = level.getCells();

		// TODO: вынести в процедуру;
		// если fps упадет, можно не все поле обновлять
		for (int i = 0; i < cells.length; i++)
			for (int j = 0; j < cells[0].length; j++) {
				Cell c = cells[i][j];
				CellView cv = c.getView();
				if (c instanceof Obstacle) {
					ObstacleView ov = (ObstacleView) cv;
					ov.getObstacleSprite().setAlpha(1);
				}
				// пока делаем false, а немного позже нужные помечаем true
				c.setVisibleForPlayer(false);
				cv.setColor(1f, 1f, 1f);// туман войны
				if (c.hasMob()) {
					c.getMob().getView().show();
				}
			}

		// if (pi != -1 && pj != -1) {
		// cells[pi][pj].getView().setColor(1f, 1f, 1f);
		// }

		updatePlayerViewZoneAndSight();

		float lx = MyScene.mx - getX(), ly = MyScene.my - getY();
		int i = convertMousetoI(lx, ly), j = convertMousetoJ(lx, ly);

		// Log.d("ij: " + i + " " + j);

		if (Utils.inBounds(i, 0, cells.length - 1) && Utils.inBounds(j, 0,
				cells[0].length - 1)) {
			Cell c = cells[i][j];
			CellView cv = c.getView();
			if (c.isVisibleForPlayer()) {
				cv.setColor(.5f, .5f, .5f);
			} else {
				cv.setColor(.25f, .25f, .25f);
			}
			updateViewZone(i, j);
			Mob mob = c.getMob();
			if (Game.getGameMode() == Mode.FIGHT && c.hasMob() && !pl.equals(mob) && c
					.isVisibleForPlayer()) {
				showMobTurnZone(cells[i][j].getMob());
			}
		}
		if (Game.getGameMode() == Mode.FIGHT && pl.getLeftActionPoints() > 0) {
			showMobTurnZone(pl);
		}
	}

	public void updatePlayerViewZoneAndSight() {
		// можно делать только в конце хода
		Player pl = Game.player;
		updateViewZone(pl.getI(), pl.getJ());

		updatePlayerSight();
	}

	private void updatePlayerSight() {
		Player pl = Game.player;
		Cell[][] cells = Game.level.getCells();
		int n = cells.length;
		int m = cells[0].length;

		boolean was[][] = new boolean[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = cells[i][j];
				if (pl.isSeeCell(i, j)) {
					c.setVisibleForPlayer(true);
				}
			}
		}

		breakIslands(was, pl.getI(), pl.getJ(), cells);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = cells[i][j];
				if (c.isVisibleForPlayer() && !was[i][j]) {
					c.setVisibleForPlayer(false);
				}
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = cells[i][j];
				CellView cv = c.getView();
				if (!c.isVisibleForPlayer()) {
					// fog of war
					float wa = 0;
					if (c instanceof Obstacle) {
						ObstacleView ov = (ObstacleView) cv;
						wa = ov.getObstacleSprite().getAlpha();
					}
					cv.setColor(cv.getRed() * .5f, cv.getGreen() * .5f, cv.getBlue()
							* .5f);
					if (c instanceof Obstacle) {
						ObstacleView ov = (ObstacleView) cv;
						ov.getObstacleSprite().setAlpha(wa);
					}
					if (c.hasMob()) {
						c.getMob().getView().hide();
					}
				} else {
					cv.show();
				}
			}
		}
	}

	private static void breakIslands(boolean was[][], int i, int j, Cell[][] cells) {
		was[i][j] = true;
		int n = cells.length;
		int m = cells[0].length;
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				if (di == dj || di == -dj)
					continue;
				int ni = i + di;
				int nj = j + dj;
				if (ni < 0 || ni >= n || nj < 0 || nj >= m) {
					continue;
				}
				if (!was[ni][nj] && cells[ni][nj].isVisibleForPlayer()) {
					breakIslands(was, ni, nj, cells);
				}
			}
		}
	}

	private void updateViewZone(int posI, int posJ) {
		Cell[][] cells = level.getCells();
		for (int i = posI; i <= Math.min(cells.length - 1, posI + 3); i++)
			for (int j = posJ; j <= Math.min(cells[0].length - 1, posJ + 3); j++) {
				CellView cv = cells[i][j].getView();
				if (cv instanceof ObstacleView
						&& (!(cv instanceof DoorView)/* || pc
													 .hasMob()*/)) {
					ObstacleView ov = (ObstacleView) cv;
					ov.getObstacleSprite().setAlpha(.2f);
				}
			}
	}

	private void showMobTurnZone(Mob mob) {
		final Cell[][] cells = level.getCells();
		int d[][] = level.bfs(mob.getI(), mob.getJ());

		int x;
		if (mob.equals(Game.player)) {
			x = mob.getLeftActionPoints();
		} else {
			x = mob.getHealthSystem().getMovingAP();
		}

		int n = cells.length;
		int m = cells[0].length;

		for (int i = mob.getI() - x; i <= mob.getI() + x; i++)
			for (int j = mob.getJ() - x; j <= mob.getJ() + x; j++) {
				if (!Utils.inBounds(i, 0, n - 1) || !Utils.inBounds(j, 0, m - 1)) {
					continue;
				}
				if (d[i][j] != -1 && d[i][j] <= x) {
					Cell c = cells[i][j];
					CellView cv = c.getView();
					float pr = 0;
					float pg = 0;
					float pb = 0;

					if (cv instanceof ObstacleView) {
						pr = ((ObstacleView) cv).obstacleSprite.getRed();
						pg = ((ObstacleView) cv).obstacleSprite.getGreen();
						pb = ((ObstacleView) cv).obstacleSprite.getBlue();
					}

					float pmr = 0;
					float pmg = 0;
					float pmb = 0;
					if (c.hasMob()) {
						pmr = c.getMob().getView().getRed();
						pmg = c.getMob().getView().getGreen();
						pmb = c.getMob().getView().getBlue();
					}

					if (mob instanceof Player) {
						cv.setColor(cv.getRed() * .6f, cv.getGreen(), cv.getBlue()
								* .6f);
					} else {
						cv.setColor(cv.getRed(), cv.getGreen() * .6f, cv.getBlue()
								* .6f);
					}
					if (cv instanceof ObstacleView) {
						((ObstacleView) cv).obstacleSprite.setColor(pr, pg, pb);
					}
					if (c.hasMob()) {
						c.getMob().getView().setColor(pmr, pmg, pmb);
					}
				}
			}
	}

	public int convertMousetoI(float mx, float my) {
		mx -= 40;
		return (int) (my / 40 - mx / 80);
	}

	public int convertMousetoJ(float mx, float my) {
		mx -= 40;
		return (int) (my / 40 + mx / 80);
	}
}