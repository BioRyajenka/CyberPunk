package com.jackson.cyberpunk.level;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.item.ItemManager;
import com.jackson.cyberpunk.level.Door.LockType;
import com.jackson.cyberpunk.mob.Dealer;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.cyberpunk.mob.Punk;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.IntPair;
import com.jackson.myengine.Utils.Orientation;

public class Level {
	private Cell cells[][];
	private LevelView view;
	public Entity mobs_not_views;

	public Level() {
		mobs_not_views = new Entity() {
			@Override
			public String toString() {
				return "mobs_not_views";
			}
		};
		//cells = generateSimple();
		LevelGenerator lg = new LevelGenerator(20, 20);
		cells = lg.generate(this);
	}

	@SuppressWarnings("unused")
	private Cell[][] generateSimple() {
		Log.d("Generating simple level");
		String s[] = (
				  "####################\n" 
				+ "#..................#\n"
				+ "#......#r#.........#\n"
				+ "#....#.....#.......#\n"
				+ "#....r.<...#.......#\n"
				+ "#....#.....#.......#\n"
				+ "#....##d####..######\n" 
				+ "#.............#...m#\n"
				+ "#.............#....#\n" 
				+ "#.............#....#\n"
				+ "#......m......#....#\n" 
				+ "#.............#....#\n"
				+ "#.............#....#\n" 
				+ "#.............#....#\n"
				+ "#.............##dd##\n" 
				+ "#..................#\n"
				+ "#...#dd12d#........#\n" 
				+ "#...#.....#........#\n"
				+ "#...#.....#........#\n" 
				+ "####################").split("\n");
		
		//s =  ("#..\n" 
		//	+ "r.<\n"
		//	+ "...\n").split("\n");


		int w = s[0].length(), 
			h = s.length;

		Cell cells[][] = new Cell[h][w];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) {
				Floor f = null;
				if (s[i].charAt(j) == '#') {
					f = new Wall(i, j, "stone", "stone");
				}
				if (s[i].charAt(j) == 'd') {
					f = new Door(i, j, LockType.NONE, "stone", "stone");
				}
				if (s[i].charAt(j) == '1') {
					f = new Door(i, j, LockType.KEY1, "stone", "stone");
				}
				if (s[i].charAt(j) == '2') {
					f = new Door(i, j, LockType.KEY2, "stone", "stone");
				}
				if (s[i].charAt(j) == '3') {
					f = new Door(i, j, LockType.NONE, "stone", "stone");
					((Door) f).setOpened(true);
				}
				if (s[i].charAt(j) == 'r') {
					f = new RepairStation(i, j, "stone", "stone");
				}
				if (s[i].charAt(j) == '<' || s[i].charAt(j) == '.' || s[i].charAt(
						j) == '>' || s[i].charAt(j) == 'o' || s[i].charAt(j) == 'm'
						|| s[i].charAt(j) == 'D') {
					f = new Floor(i, j, "stone");
				}
				if (s[i].charAt(j) == '<') {
					f.setMob(Game.player);
					Game.player.resetLongTermTarget();
					mobs_not_views.attachChild(Game.player);
				}
				if (s[i].charAt(j) == 'o') {
					f.addItem(ItemManager.getItem("rusty_knife"));
				}
				if (s[i].charAt(j) == 'm') {
					Punk punk = new Punk();
					f.setMob(punk);
					mobs_not_views.attachChild(punk);
				}
				if (s[i].charAt(j) == 'D') {
					Dealer d = new Dealer();
					f.setMob(d);
					mobs_not_views.attachChild(d);
				}
				// if (s[i].charAt(j) == '>')
				// c.add(new Stairs);
				if (f == null) {
					Log.e("c == null in Level.java");
				}
				cells[i][j] = f;
			}
		return cells;
	}

	public LevelView getView() {
		if (view == null)
			view = new LevelView(this);
		return view;
	}

	public Cell[][] getCells() {
		return cells;
	}

	static class LevelGenerator {
		private int n, m;
		private char f[][];
		private boolean was[][];
		private ArrayList<Room> rooms;
		private Cell cells[][];

		public LevelGenerator(int n, int m) {
			this.n = n;
			this.m = m;
			f = new char[n][m];
			was = new boolean[n][m];
			rooms = new ArrayList<Room>();
			cells = new Cell[n][m];
		}

		public Cell[][] generate(Level levelInstance) {
			Log.d("Generating level " + n + "x" + m);
			initField();
			Log.d("Generating rooms");
			gen(0, n - 1, 0, m - 1);
			Log.d("Recognizing rooms");
			recognizeRooms();
			Log.d("Making doors");
			makeDoors();
			Log.d("Converting to cells");
			logField();
			convertToCells();
			// addObstacles();TODO:
			Log.d("Adding player");
			addPlayer();
			Log.d("Adding environment");
			addEnvironment();
			Log.d("Adding enemies");
			addEnemies();
			Log.d("Adding mobs to mobs_not_views");
			addMobsToMobsNotViews(levelInstance);
			Log.d("Level created!");
			return cells;
		}

		private void addPlayer() {
			cells[1][1].setMob(Game.player);
			Game.player.resetLongTermTarget();
		}

		private void logField() {
			for (int i = 0; i < n; i++) {
				Log.d(new String(f[i]));
			}
		}

		private void convertToCells() {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++) {
					// if (f[i][j] == '+')
					if (f[i][j] == '.') {
						cells[i][j] = new Floor(i, j, "stone");
					}
					if (f[i][j] == '+') {
						cells[i][j] = new Door(i, j, LockType.NONE, "stone", "stone");
					}
					if (f[i][j] == '#') {
						cells[i][j] = new Wall(i, j, "stone", "stone");
					}
				}
		}

		private final static float TARGET_REPAIR_STATIONS_PER_LEVEL = 3f;
		private final static float TARGET_DEALERS_PER_LEVEL = 3f;

		private void addEnvironment() {
			addEnvironment(c -> {
				if (c instanceof Wall) {
					int posType = ((Wall) c).getObstaclePositionType(cells);
					if (posType == 0 || posType == 1 || posType == 3 || posType == 5) {
						return true;
					}
				}
				return false;
			} , TARGET_REPAIR_STATIONS_PER_LEVEL,
					c -> cells[c.posI][c.posJ] = new RepairStation(c.posI, c.posJ,
							"stone", "stone"));
			addEnvironment(c -> c.isPassable() && !c.hasMob(), TARGET_DEALERS_PER_LEVEL,
					c -> c.setMob(new Dealer()));
		}

		private void addEnvironment(Function<Cell, Boolean> checker, float targetNumber,
				Consumer<Cell> consumer) {
			// calculating number of vacant places
			int freePlaces = 0;
			for (int i = 1; i < cells.length - 1; i++) {
				for (int j = 1; j < cells[i].length - 1; j++) {
					Cell c = cells[i][j];
					if (checker.apply(c)) {
						freePlaces++;
					}
				}
			}

			float probability = targetNumber / freePlaces;

			for (int i = 1; i < cells.length - 1; i++) {
				for (int j = 1; j < cells[i].length - 1; j++) {
					Cell c = cells[i][j];
					if (checker.apply(c)) {
						float dice = Utils.rand.nextFloat();
						if (dice < probability) {
							// ok, placing station
							consumer.accept(c);
						}
					}
				}
			}
		}

		private void addEnemies() {
			for (Room r : rooms) {
				if (r.containsPlayer()) {
					continue;
				}
				float g = (float) Math.abs(Utils.rand.nextGaussian()) + 1;
				float PROB_PER_CELL = 0.06f;
				float enemies = PROB_PER_CELL * r.getSize() * (r.isClosed() ? 2 : 1) * (r
						.isCorridor() ? 0.3f : 1) * g;
				addEnemiesToTheRoom(r, (int) enemies);
			}
		}

		private void addEnemiesToTheRoom(Room r, int num) {
			num = Math.min(num, r.getSize() / 2);// TODO:r.getFreeSpace()
			for (int k = 0; k < num; k++) {
				while (true) {
					int i = r.up + Utils.rand.nextInt(r.down - r.up + 1);
					int j = r.left + Utils.rand.nextInt(r.right - r.left + 1);
					if (cells[i][j].getMob() == null) {
						Punk punk = new Punk();
						cells[i][j].setMob(punk);
						break;
					}
				}
			}
		}

		private void addMobsToMobsNotViews(Level levelInstance) {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++) {
					Cell c = cells[i][j];
					if (c.getMob() != null) {
						levelInstance.mobs_not_views.attachChild(c.getMob());
					}
				}
		}

		private void initField() {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					f[i][j] = '.';
			// making bounds
			for (int i = 0; i < n; i++)
				f[i][0] = f[i][m - 1] = '#';
			for (int i = 0; i < m; i++)
				f[0][i] = f[m - 1][i] = '#';
		}

		private void gen(int i1, int i2, int j1, int j2) {
			if (i2 - i1 <= 3 || j2 - j1 <= 3)
				return;
			if (i2 - i1 <= 5 && j2 - j1 <= 5)
				return;
			if ((i2 - i1 - 1) * (j2 - j1 - 1) <= 30 && Utils.rand.nextBoolean())
				return;
			if ((i2 - i1 - 1) * (j2 - j1 - 1) <= 18 && Utils.rand.nextBoolean())
				return;
			Orientation or = Orientation.VERTICAL;
			int win = -1;
			if (Utils.rand.nextBoolean()) {
				or = Orientation.HORIZONTAL;
			}

			if (i2 - i1 <= 5) {
				or = Orientation.VERTICAL;
			}
			if (j2 - j1 <= 5) {
				or = Orientation.HORIZONTAL;
			}

			for (int i = 0; i < 3; i++) {
				int t, k = (j2 - j1 - 3);
				if (or == Orientation.HORIZONTAL) {
					k = (i2 - i1 - 3);
				}
				t = Utils.rand.nextInt(k);
				if (Math.min(t, k - t - 1) > Math.min(win, k - win - 1)) {
					win = t;
				}
			}

			if (or == Orientation.VERTICAL) {
				win += j1 + 2;
				for (int i = i1 + 1; i < i2; i++) {
					f[i][win] = '#';
				}
				// f[win2][win] = '+';

				gen(i1, i2, j1, win);
				gen(i1, i2, win, j2);
			} else {
				win += i1 + 2;
				for (int j = j1 + 1; j < j2; j++) {
					f[win][j] = '#';
				}
				// f[win][win2] = '+';

				gen(i1, win, j1, j2);
				gen(win, i2, j1, j2);
			}
		}

		private int recDfs(ArrayList<IntPair> v, int i, int j, boolean doorIsObstacle) {
			int res = 1;
			if (v != null) {
				v.add(new IntPair(i, j));
			}
			was[i][j] = true;
			for (int di = -1; di <= 1; di++)
				for (int dj = -1; dj <= 1; dj++) {
					if (di == dj || di == -dj)
						continue;
					int ni = i + di;
					int nj = j + dj;
					if (ni < 0 || ni >= n || nj < 0 || nj >= m) {
						continue;
					}
					if (f[ni][nj] != '#' && !was[ni][nj] && (!doorIsObstacle
							|| f[ni][nj] != '+')) {
						res += recDfs(v, ni, nj, doorIsObstacle);
					}
				}
			return res;
		}

		private void clearWas() {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					was[i][j] = false;
		}

		private void recognizeRooms() {
			clearWas();
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++) {
					if (f[i][j] == '.' && !was[i][j]) {
						ArrayList<IntPair> v = new ArrayList<IntPair>();
						recDfs(v, i, j, false);
						int left = Utils.INF, up = Utils.INF, right = -Utils.INF,
								down = -Utils.INF;
						for (IntPair c : v) {
							left = Math.min(left, c.second);
							right = Math.max(right, c.second);
							up = Math.min(up, c.first);
							down = Math.max(down, c.first);
						}
						rooms.add(new Room(left, up, right, down));
					}
				}
		}

		private void makeDoors() {
			int tsize = 0;
			for (Room r : rooms) {
				tsize += r.getSize();
			}
			int doors = 0;
			do {
				for (Room r : rooms) {
					ArrayList<IntPair> to_try = new ArrayList<IntPair>();
					for (int i = r.up; i <= r.down; i++) {
						to_try.add(new IntPair(i, r.left - 1));
						to_try.add(new IntPair(i, r.right + 1));
					}
					for (int i = r.left; i <= r.right; i++) {
						to_try.add(new IntPair(r.up - 1, i));
						to_try.add(new IntPair(r.down + 1, i));
					}

					Utils.randomShuffle(to_try);

					for (IntPair p : to_try) {
						if (f[p.first][p.second] == '+') {
							continue;
						}

						clearWas();
						int psize = recDfs(null, r.up, r.left, false);

						clearWas();
						f[p.first][p.second] = '+';
						int nsize = recDfs(null, r.up, r.left, false);
						if (nsize > psize + 1) {// +1 is a door
							// extend!
							doors++;
						} else {
							f[p.first][p.second] = '#';
						}
					}
				}

				clearWas();
				if (recDfs(null, 1, 1, false) - doors == tsize) {
					break;
				}
			} while (true);
		}

		private class Room {
			/**
			 * points to left-up and right-down free cells
			 */
			int left, up, right, down;

			public Room(int left, int up, int right, int down) {
				this.left = left;
				this.up = up;
				this.right = right;
				this.down = down;
			}

			public boolean containsPlayer() {
				int i = Game.player.getI();
				int j = Game.player.getJ();
				return i >= up && i <= down && j >= left && j <= right;
			}

			public boolean isClosed() {
				return recDfs(null, up, left, true) == getSize();
			}

			public int getSize() {
				return (right - left + 1) * (down - up + 1);
			}

			public boolean isCorridor() {
				return left == right - 1 || up == down - 1;
			}
		}

	}

	public String takeSnapshot() {
		return takeSnapshot(null);
	}

	/**
	 * Marks mob with capital M
	 */
	public String takeSnapshot(Mob mob) {
		String res = "";
		for (int i = 0; i < cells.length; i++) {
			String row = "";
			for (int j = 0; j < cells[i].length; j++) {
				Cell c = cells[i][j];
				if (c instanceof Door) {
					row += ((Door) c).isOpened() ? "-" : "+";
				} else if (!c.isPassable) {
					row += "#";
				} else if (!c.hasMob()) {
					row += (((Floor) c).getLoot().getItems().isEmpty() ? "." : "o");
				} else if (c.getMob() instanceof Player) {
					row += "p";
				} else if (mob != null && mob == c.getMob()) {
					row += "M";
				} else {
					row += "m";
				}
			}
			res += row + "\n";
		}
		return res;
	}

	public IntPair getStepTowardsTarget(int startPosI, int startPosJ, int finishPosI,
			int finishPosJ, BiFunction<Integer, Integer, Boolean> validator) {
		final Cell[][] cells = Game.level.getCells();
		final int n = cells.length;
		final int m = cells[0].length;

		int d[][] = Game.level.bfs(finishPosI, finishPosJ, validator);
		int resI = -1;
		int resJ = -1;

		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				if ((di == 0 && dj == 0) || (di != 0 && dj != 0)) {
					continue;
				}
				int ni = startPosI + di;
				int nj = startPosJ + dj;
				if (!Utils.inBounds(ni, 0, n - 1) || !Utils.inBounds(nj, 0, m - 1)
						|| d[ni][nj] == -1) {
					continue;
				}
				if (resI == -1 || d[ni][nj] < d[resI][resJ]) {
					resI = ni;
					resJ = nj;
				}
			}
		}
		return new IntPair(resI, resJ);
	}

	public int[][] bfs(int i, int j) {
		BiFunction<Integer, Integer, Boolean> validator = (i1, j1) -> {
			Cell c = cells[i1][j1];
			return c.isPassable() && !c.hasMob();// && !c.isDenyTravelling();
		};
		return bfs(i, j, validator);
	}

	public int[][] bfs(int i, int j, BiFunction<Integer, Integer, Boolean> validator) {
		final int n = cells.length;
		final int m = cells[0].length;
		return Utils.bfs(n, m, i, j, validator);
	}
}