package com.jackson.cyberpunk.mob;

import java.util.Collection;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.LogText;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.InjuryManager;
import com.jackson.cyberpunk.health.InjuryManager.InjuryType;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.health.PartsManager;
import com.jackson.cyberpunk.item.Ammo;
import com.jackson.cyberpunk.item.Corpse;
import com.jackson.cyberpunk.item.IWeapon;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.CellView;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;
import com.jackson.myengine.Utils.BresenhamLine;
import com.jackson.myengine.Utils.BresenhamLine.BresenhamLineIterator;
import com.jackson.myengine.Utils.IntPair;

//Needs to be entity to be managed updated 
public abstract class Mob extends Entity {
	protected String picName, name;
	protected HealthSystem healthSystem;
	protected Inventory inventory;
	protected IWeapon weapon;

	public enum Action {
		NOTHING, MOVING, ATTACKING
	};

	public enum DieReason {
		MISSINGPART("отсутствия важных органов"), STARVATION("голода"), PAINFUL_SHOCK(
				"болевого шока");

		private String text;

		private DieReason(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	};

	private Action action;
	// 1 / (x * FPS), where x is time in seconds
	private static final float ACTION_PROGRESS_SPEED = 1f / (.4f * Game.TARGET_FPS);
	private float actionProgress;

	protected int leftActionPoints;

	protected int posI, posJ;
	private int targetI, targetJ;

	private MobView view;

	public Mob(String picName, String name, Inventory inventory) {
		this.picName = picName;
		this.name = name;
		this.inventory = inventory;
		healthSystem = new HealthSystem();// изначально здоров
		weapon = healthSystem.getArm();
		action = Action.NOTHING;
		refreshLeftActionPoints();
	}

	@Override
	public void onManagedUpdate() {
		if (healthSystem.checkLethalParts()) {
			die(DieReason.MISSINGPART);
			return;
		}
		if (healthSystem.checkPaintreshold()) {
			die(DieReason.PAINFUL_SHOCK);
			return;
		}
		if (healthSystem.checkStarvation()) {
			die(DieReason.STARVATION);
			return;
		}

		Cell cells[][] = Game.level.getCells();
		if (action != Action.NOTHING) {
			actionProgress += ACTION_PROGRESS_SPEED;
			updateView();
		}
		if (actionProgress >= 1) {
			if (action == Action.MOVING) {
				cells[posI][posJ].remove(this);
				posI = targetI;
				posJ = targetJ;
				cells[posI][posJ].setMob(this);
				cells[targetI][targetJ].setDenyTravelling(false);
			}
			if (action == Action.ATTACKING) {
				IWeapon w = getWeapon();
				Cell tcell = cells[targetI][targetJ];
				Mob enemy = tcell.getMob();
				if (w.isMelee())
					enemy.hurtBy(this);
				else {
					int shots = Math.min(w.getAmmo(), Utils.rand.nextInt(3) + 2);
					w.setAmmo(w.getAmmo() - shots);
					for (int i = 0; i < shots; i++) {
						float manipAc = getHealthSystem().getManipulation();
						float sightAc = getHealthSystem().getSight();
						int dist = Utils.manhattanDist(getI(), getJ(), enemy.getI(),
								enemy.getJ());

						Log.d("sight " + sightAc);

						if (dist > 4) {
							sightAc = Math.max(0, sightAc - 0.2f * (dist - 4));
						}

						float accuracy = sightAc * manipAc;// зависимость от
						// расстояния до
						// цели, зрения и
						// манипуляции

						Log.d("sA " + sightAc + ", mA " + manipAc + ", dist: " + dist
								+ ", ac: " + accuracy);

						if (Utils.rand.nextFloat() <= accuracy) {
							enemy.hurtBy(this);
						} else {
							LogText.add("Ты промазал");
						}
					}
				}
			}
			action = Action.NOTHING;
		}
	}

	public void moveToPos(int targetI, int targetJ) {
		if (getAction() != Action.NOTHING) {
			Log.d("Hero is busy now!");
			return;
		}
		if (leftActionPoints > 0)
			leftActionPoints--;

		action = Action.MOVING;
		this.targetI = targetI;
		this.targetJ = targetJ;
		actionProgress = 0;
		Game.level.getCells()[targetI][targetJ].setDenyTravelling(true);

		// это шаманство нужно, чтобы mobView адекватно рисовался над cellView
		Game.engine.runOnUIThread(new Runnable() {
			public void run() {
				int targetI = Mob.this.targetI, targetJ = Mob.this.targetJ;
				Cell cells[][] = Game.level.getCells();
				CellView fcv = cells[posI][posJ].getView(), tcv = cells[targetI][targetJ]
						.getView();

				if (targetI > posI || targetJ > posJ) {
					getView().detachSelf();
					tcv.attachChild(getView());

					getView().setPosition(fcv.getGlobalX() + 21 - tcv.getGlobalX(), fcv
							.getGlobalY() - 47 - tcv.getGlobalY());
				} else {
					getView().detachSelf();
					fcv.attachChild(getView());

					getView().setPosition(21, -47);
				}
			}
		});
	}

	public void attack(Mob m) {
		if (getAction() != Action.NOTHING) {
			Log.d("Hero is busy now!");
			return;
		}
		if (leftActionPoints > 0)
			leftActionPoints--;
		action = Action.ATTACKING;
		this.targetI = m.getI();
		this.targetJ = m.getJ();
		actionProgress = 0;
	}

	public void die(DieReason reason) {
		LogText.add(getName() + " умер от " + reason.getText());
		Floor cell = (Floor) Game.level.getCells()[getI()][getJ()];
		cell.setMob(null);
		for (Item i : inventory.getItems()) {
			cell.addItem(i);
		}
		for (Part p : healthSystem.getParts()) {
			if (p.equals(PartsManager.getSimple(p.getType()))) {
				cell.addItem(p);
			}
		}
		cell.addItem(new Corpse(this));
		Game.engine.detachOnUIThread(this);
		Game.engine.detachOnUIThread(getView());
	}

	private void hurtBy(Mob initiator) {
		// forcing random hurt
		InjuryType injuryType = initiator.weapon.getInjuryHelper().getRandomInjury();
		Collection<Part> parts = healthSystem.getParts();
		Part p = (Part) (parts.toArray()[Utils.rand.nextInt(parts.size())]);
		p.hurt(injuryType);
		healthSystem.updateView();

		String partname = p.getName();
		String injname = InjuryManager.get(injuryType).getName();
		if (p.getType() == Part.Type.BRAIN) {
			partname = "голова";
		}
		if (initiator == Game.player) {
			LogText.add("Ты нанес " + injname + " " + partname + " противника");
		} else {
			LogText.add(initiator.getName() + " нанес " + injname + " твоему "
					+ partname);
		}
	}

	public boolean isSeeMob(Mob mob) {
		return isSeeCell(mob.getI(), mob.getJ());
	}

	public boolean isSeeCell(int targetI, int targetJ) {
		int d = (int) Utils.dist(1f * getI(), 1f * getJ(), 1f * targetI, 1f * targetJ);
		if (d > getHealthSystem().getSight() * 10) {
			return false;
		}
		BresenhamLine bl = new BresenhamLine(new IntPair(getI(), getJ()), new IntPair(
				targetI, targetJ));
		BresenhamLineIterator it = bl.getIterator();
		boolean see = true;
		while (it.hasNext()) {
			IntPair p = it.next();
			see &= Game.level.getCells()[p.first][p.second].isPassable();
		}
		return see;
	}

	public boolean isMobNear(Mob mob) {
		return Utils.manhattanDist(getI(), getJ(), mob.getI(), mob.getJ()) == 1;
	}

	public boolean isReachableCell(int i, int j) {
		int d[][] = Game.level.bfs(getI(), getJ());
		int dist = d[i][j];

		if (dist == -1)
			return false;
		if (Game.getGameMode() == Mode.EXPLORE)
			return true;
		return dist <= getLeftActionPoints();
	}

	public void makeCloserToLongTermTarget(int lttI, int lttJ) {
		final Cell[][] cells = Game.level.getCells();
		final int n = cells.length;
		final int m = cells[0].length;

		int d[][] = Game.level.bfs(lttI, lttJ);
		int fi = -1;
		int fj = -1;

		// Log.d("!!!" + lttI + " " + lttJ + " " + posI + " " + posJ);

		for (int di = -1; di <= 1; di++)
			for (int dj = -1; dj <= 1; dj++) {
				if ((di == 0 && dj == 0) || (di != 0 && dj != 0))
					continue;
				int ni = posI + di, nj = posJ + dj;
				if (d[ni][nj] == -1 || !Utils.inBounds(ni, 0, n - 1) || !Utils.inBounds(
						nj, 0, m - 1))
					continue;
				Cell c = cells[ni][nj];
				if ((fi == -1 || d[ni][nj] < d[fi][fj]) && !c.hasMob() && !c
						.isDenyTravelling()) {
					fi = ni;
					fj = nj;
				}
			}

		if (fi != -1) {
			moveToPos(fi, fj);
			return;
		}
		Log.e("makeCloserToLongTermTarget works bad");
		Log.printStackTrace();
	}

	public void refreshLeftActionPoints() {
		leftActionPoints = healthSystem.getMoving();
	}

	public HealthSystem getHealthSystem() {
		return healthSystem;
	}

	public int getLeftActionPoints() {
		return leftActionPoints;
	}

	public MobView getView() {
		if (view == null)
			view = new MobView(this);
		return view;
	}

	private void updateView() {
		if (action == Action.MOVING) {
			Cell[][] cells = Game.level.getCells();
			CellView c1 = cells[posI][posJ].getView(), c2 = cells[targetI][targetJ]
					.getView(), par = (CellView) getView().getParent();
			float x1 = c1.getGlobalX() + 21, y1 = c1.getGlobalY() - 47, x2 = c2
					.getGlobalX() + 21, y2 = c2.getGlobalY() - 47, x = x2 - x1, y = y2
							- y1;
			// v = (v2 - v1);
			// v * progress
			getView().setPosition(x1 + x * actionProgress - par.getGlobalX(), y1 + y
					* actionProgress - par.getGlobalY());
		}
		if (action == Action.ATTACKING) {
			// TODO:
		}
	}

	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}

	public IWeapon getWeapon() {
		return weapon;
	}

	public boolean loadRifle(IWeapon w) {
		Inventory inv = getInventory();
		boolean ok = false;
		for (Item i : inv.getItems()) {
			if (i instanceof Ammo) {
				Ammo a = (Ammo) i;
				Ammo.Type wtype = w.getAmmoType();
				if (a.getType() == wtype) {
					ok = true;
					int sum = a.getAmount() + w.getAmmo();
					if (sum > 40) {
						a.setAmount(sum - 40);
						w.setAmmo(40);
						break;
					}
					if (sum <= 40) {
						w.setAmmo(sum);
						inv.remove(a);
					}
					if (sum == 40) {
						break;
					}
				}
			}
		}
		return ok;
	}

	public String getName() {
		return name;
	}

	public String getPicName() {
		return picName;
	}

	public int getI() {
		return posI;
	}

	public int getJ() {
		return posJ;
	}

	/**
	 * За этим полностью следит Cell
	 */
	public void setPos(int i, int j) {
		posI = i;
		posJ = j;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public Inventory getInventory() {
		return inventory;
	}
}