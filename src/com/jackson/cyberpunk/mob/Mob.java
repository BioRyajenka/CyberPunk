package com.jackson.cyberpunk.mob;

import java.util.Collection;
import java.util.function.BiFunction;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.GameLog;
import com.jackson.cyberpunk.health.Arm;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.HealthSystem.ArmOrientation;
import com.jackson.cyberpunk.health.HealthSystem.DieReason;
import com.jackson.cyberpunk.health.Injury;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.item.Corpse;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;
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
	protected Weapon weapon;

	public enum Action {
		NOTHING, MOVING, ATTACKING
	};

	private Action action;
	// 1 / (x * FPS), where x is time in seconds
	private static final float ACTION_PROGRESS_SPEED = 1f / (.4f * Game.TARGET_FPS);
	private float actionProgress;

	/**
	 * Left �� ����� ����������
	 */
	protected float spentManipulationAP;
	protected float spentMovingAP;

	protected int posI, posJ;
	protected int targetI, targetJ;

	private MobView view;

	public Mob(String picName, String name, Inventory inventory,
			ArmOrientation armOrientation) {
		this.picName = picName;
		this.name = name + " " + Utils.generateRandomName();
		this.inventory = inventory;
		healthSystem = new HealthSystem(armOrientation);// ���������� ������
		weapon = null;// ������� �����
		action = Action.NOTHING;
		spentManipulationAP = 0;
		spentMovingAP = 0;
		turnFinished = false;
	}

	/**
	 * Calling from Cell.java
	 */
	public ContextMenu getContextMenu() {
		ContextMenu menu = new ContextMenu();
		Player p = Game.player;
		if (getAction() == Action.NOTHING && p.isSeeMob(this)) {
			menu.add(Type.MOB_INFO);
			if (this != p) {
				menu.add(Type.MOB_ATTACK, null, p.getAttackAPCost());
			}
		}
		return onContextMenuCreate(menu);
	}

	protected abstract ContextMenu onContextMenuCreate(ContextMenu menu);

	private boolean turnFinished;

	public void finishTurn() {
		turnFinished = true;
	}

	public boolean isTurnFinished() {
		return turnFinished;
	}

	@Override
	public void onManagedUpdate() {
		Cell cells[][] = Game.level.getCells();
		if (healthSystem.isDead()) {
			die(healthSystem.getDieReason());
			return;
		}
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
				Cell tcell = cells[targetI][targetJ];
				Mob enemy = tcell.getMob();
				if (weapon == null || weapon.isMelee()) {
					enemy.hurtBy(this);
				} else {
					assert (healthSystem.getCombatArm().isFunction());// TODO:
					RangedWeapon ranged = (RangedWeapon) weapon;
					int shots = ranged.getShots();
					ranged.setAmmo(ranged.getAmmo() - shots);
					for (int i = 0; i < shots; i++) {
						float sightAc = healthSystem.getSight();
						int dist = Utils.manhattanDist(getI(), getJ(), enemy.getI(), enemy
								.getJ());

						Log.d("sight " + sightAc);

						if (dist > 4) {
							sightAc = Math.max(0, sightAc - 0.2f * (dist - 4));
						}

						// ���������� �� ����, ������ � �����������
						float accuracy = sightAc;

						Log.d("sA " + sightAc + ", dist: " + dist + ", ac: " + accuracy);

						if (Utils.rand.nextFloat() <= accuracy) {
							enemy.hurtBy(this);
						} else {
							GameLog.add("�� ��������");
						}
					}
				}
			}
			action = Action.NOTHING;
		}
	}

	protected void die(DieReason dieReason) {
		GameLog.add(getName() + " ���� �� " + healthSystem.getDieReason().getText());
		Floor cell = (Floor) Game.level.getCells()[getI()][getJ()];
		cell.setMob(null);
		for (Item i : inventory.getItems()) {
			cell.addItem(i);
		}
		cell.addItem(new Corpse(this));
		Game.engine.detachOnUIThread(this);
		Game.engine.detachOnUIThread(getView());
	}

	boolean hasEnoughAPToMove() {
		return spentMovingAP + 1 <= getMaximumMovingAP();
	}

	/**
	 * @see refreshLeftActionPoints() and getMaximumMovingAP()
	 */
	private float prev = 0;

	private float getMaximumMovingAP() {
		float realMovingAP = healthSystem.getMovingAP();
		float res = prev + realMovingAP;
		if ((int) res > (int) realMovingAP) {
			return (int) res;
		}
		return res;
	}

	private float getMaximumManipulationAP() {
		float ap;
		if (weapon == null) {
			ap = healthSystem.getManipulationAP(false);
		} else {
			ap = healthSystem.getManipulationAP(weapon.isTwoHanded());
		}
		return ap;
	}

	public boolean hasEnoughAPToManipulate(float cost) {
		return spentManipulationAP + cost <= getMaximumManipulationAP();
	}

	public void moveToPos(int targetI, int targetJ) {
		if (getAction() != Action.NOTHING) {
			Log.d("Hero is busy now!");
			return;
		}
		if (hasEnoughAPToMove() && Game.getGameMode() == Mode.FIGHT) {
			spentMovingAP++;
		}

		if (targetI == posI && targetJ == posJ) {
			return;// passing turn
		}

		action = Action.MOVING;
		this.targetI = targetI;
		this.targetJ = targetJ;
		actionProgress = 0;
		Game.level.getCells()[targetI][targetJ].setDenyTravelling(true);

		// ��� ��������� �����, ����� mobView ��������� ��������� ��� cellView
		Game.engine.runOnUIThread(new Runnable() {
			public void run() {
				int targetI = Mob.this.targetI;
				int targetJ = Mob.this.targetJ;
				Cell cells[][] = Game.level.getCells();
				CellView fcv = cells[posI][posJ].getView();
				CellView tcv = cells[targetI][targetJ].getView();

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

	public float getAttackAPCost() {
		float apCost;
		if (weapon == null) {
			apCost = healthSystem.getCombatArm().getAttackAP();
		} else {
			apCost = weapon.getAttackAP();
		}
		return apCost;
	}

	public void attack(Mob m) {
		if (getAction() != Action.NOTHING) {
			Log.d("Hero is busy now!");
			return;
		}
		if (Game.getGameMode() == Mode.FIGHT) {
			spentManipulationAP += getAttackAPCost();
		}
		action = Action.ATTACKING;
		this.targetI = m.getI();
		this.targetJ = m.getJ();
		actionProgress = 0;
	}

	protected void hurtBy(Mob initiator) {
		// forcing random hurt
		InjuryHelper injuryHelper;
		if (initiator.weapon == null) {// means fighting with arm
			Arm arm = initiator.healthSystem.getCombatArm();
			assert (arm.isFunction());
			injuryHelper = arm.getInjuryHelper();
		} else {
			injuryHelper = initiator.weapon.getInjuryHelper();
		}
		Injury injury = injuryHelper.getRandomInjury();
		Collection<Part> parts = healthSystem.getParts();
		// random part
		Part p = (Part) (parts.toArray()[Utils.rand.nextInt(parts.size())]);
		p.hurt(injury);
		
		healthSystem.updatePainCommonBuff();

		// message
		String partDescription = p.getDescription();
		String injuryDescription = injury.getDescription();
		if (initiator == Game.player) {
			GameLog.add("�� ����� " + injuryDescription + " " + partDescription
					+ " ����������");
		} else {
			GameLog.add(initiator.getName() + " ����� " + injuryDescription + " ������ "
					+ partDescription);
		}
	}

	public boolean isSeeMob(Mob mob) {
		// npc's see player only if he see them
		if (this == Game.player && mob == Game.player) {
			return true;
		}
		if (mob == Game.player) {
			return Game.player.isSeeMob(this);
		}
		if (this == Game.player) {
			return Game.level.getCells()[mob.posI][mob.posJ].isVisibleForPlayer();
		}
		return isSeeCell(mob.getI(), mob.getJ());
	}

	public boolean isSeeCell(int targetI, int targetJ) {
		int d = (int) Utils.dist(1f * getI(), 1f * getJ(), 1f * targetI, 1f * targetJ);
		if (d > getHealthSystem().getSight() * 10) {
			return false;
		}
		BresenhamLine bl = new BresenhamLine(new IntPair(getI(), getJ()), new IntPair(targetI,
				targetJ));
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
		if (Game.getGameMode() == Mode.EXPLORE) {
			return true;
		}
		return dist <= healthSystem.getMovingAP() - spentMovingAP;
	}

	public boolean makeStepCloserToTarget(int targetI, int targetJ) {
		Cell[][] cells = Game.level.getCells();

		BiFunction<Integer, Integer, Boolean> validator = (i1, j1) -> {
			Cell c = cells[i1][j1];
			return c.isPassable() && !c.hasMob() && !c.isDenyTravelling();
		};

		IntPair intPair = Game.level.getStepTowardsTarget(posI, posJ, targetI, targetJ,
				validator);
		int fi = intPair.first;
		int fj = intPair.second;

		if (fi != -1) {
			moveToPos(fi, fj);
			return false;
		}
		return true;
		/*���������, ���� �� ����������
		Log.e("makeCloserToLongTermTarget works bad for mob " + this + ", target("
				+ targetI + ", " + targetJ + ")");
		Log.e("level snapshot:\n" + Game.level.takeSnapshot(this) + "stacktrace:");
		Log.printStackTrace();*/
	}

	public HealthSystem getHealthSystem() {
		return healthSystem;
	}

	/**
	 * Not actually steps, but some magic. @see refreshLeftActionPointsAndTurn()
	 */
	private int stepsInExploreMode = 1;

	public float getPrevForDebug() {
		return prev;
	}

	public int getStepsInExploreModeForDebug() {
		return stepsInExploreMode;
	}

	public void refreshLeftActionPointsAndTurn() {
		// accumulating leftLegAP
		prev = Math.min(1, healthSystem.getMovingAP() - spentMovingAP);
		stepsInExploreMode++;
		if (Game.getGameMode() == Mode.EXPLORE) {
			stepsInExploreMode = 0;
		}
		if (stepsInExploreMode <= 1) {
			// means that we are in explore mode or it was on previous step
			prev = 0;
		}
		spentMovingAP = 0;
		spentManipulationAP = 0;

		turnFinished = false;
	}

	public void spendManipulationAP(float cost) {
		if (Game.getGameMode() == Mode.EXPLORE) {
			return;
		}
		if (spentManipulationAP + cost > getMaximumManipulationAP()) {
			Log.e("Trying to spend more AP than left");
			Log.printStackTrace();
		} else {
			spentManipulationAP += cost;
		}
	}

	public float getLeftManipulationAP() {
		return getMaximumManipulationAP() - spentManipulationAP;
	}

	// ;(((((
	public float getFloatLeftMovingAP() {
		return getMaximumMovingAP() - spentMovingAP;
	}

	public int getLeftMovingAP() {
		return (int) (getMaximumMovingAP() - spentMovingAP);
	}

	public MobView getView() {
		if (view == null) {
			view = new MobView(this);
		}
		return view;
	}

	private void updateView() {
		if (action == Action.MOVING) {
			Cell[][] cells = Game.level.getCells();
			CellView c1 = cells[posI][posJ].getView(), c2 = cells[targetI][targetJ].getView(),
					par = (CellView) getView().getParent();
			float x1 = c1.getGlobalX() + 21, y1 = c1.getGlobalY() - 47, x2 = c2.getGlobalX()
					+ 21, y2 = c2.getGlobalY() - 47, x = x2 - x1, y = y2 - y1;
			// v = (v2 - v1);
			// v * progress
			getView().setPosition(x1 + x * actionProgress - par.getGlobalX(), y1 + y
					* actionProgress - par.getGlobalY());
		}
		if (action == Action.ATTACKING) {
			// TODO:
		}
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return weapon if holding weapon and null if barehanded
	 */
	public Weapon getWeapon() {
		return weapon;
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
	 * �� ���� ��������� ������ Cell
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

	@Override
	public String toString() {
		return name + "(" + posI + ", " + posJ + ")";
	}
}