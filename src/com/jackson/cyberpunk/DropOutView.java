package com.jackson.cyberpunk;

import java.util.List;

import org.newdawn.slick.Input;

import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.Injury;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.RangedWeapon;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.level.Cell;
import com.jackson.cyberpunk.level.Floor;
import com.jackson.cyberpunk.mob.Mob;
import com.jackson.cyberpunk.mob.NPC;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Text;

public class DropOutView extends Sprite {
	private static DropOutView singleton = null;

	private static float DELAY1 = .5f;
	private static float DELAY2 = 1f;

	private Text text;

	private Object lastObserveObject = null;
	private float observationTime = 0;
	private int stage = 0;

	private float pMx;
	private float pMy;

	private DropOutView() {
		super(0, 0, "res/gui/dark_bg");
		setSize(150, 40);
		setAlpha(.85f);
		text = new Text(5, 5, "00");
		text.setColor(.8f, .8f, .8f, 1f);
		attachChild(text);
	}

	@Override
	public void onManagedUpdate() {
		float dt = 1f / Game.TARGET_FPS;

		if (Message.getInstance().isVisible() || ContextMenuView.getInstance()
				.isVisible()) {
			setVisible(false);
			observationTime = 0;
			lastObserveObject = null;
			return;
		}

		Input in = Game.engine.getInput();
		float mx = in.getMouseX();
		float my = in.getMouseY();

		setPosition(mx + 10, my);

		Object newObserveObject = null;

		if (InventoryWindow.getInstance().isVisible()) {
			for (Item i : InventoryWindow.getInstance().getItems()) {
				if (i.getView().isSelected(mx, my)) {
					newObserveObject = i;
				}
			}
		} else {
			Cell[][] cells = Game.level.getCells();
			for (int i = 0; i < cells.length; i++) {
				for (Cell c : cells[i]) {
					if (c.getView().isSelected(mx, my)) {
						newObserveObject = c;
					}
				}
			}
		}

		if (pMx != mx || pMy != my) {
			newObserveObject = null;
		}

		if (newObserveObject != null) {
			if (newObserveObject == lastObserveObject) {
				observationTime += dt;
			} else {
				observationTime = 0;
				lastObserveObject = newObserveObject;
				setStage(0);
			}
		} else {
			lastObserveObject = null;
			setStage(0);
			pMx = mx;
			pMy = my;
			return;
		}

		if (observationTime > DELAY1 && stage == 0) {
			setStage(1);
		}

		if (observationTime > DELAY1 + DELAY2 && stage == 1) {
			setStage(2);
		}

		pMx = mx;
		pMy = my;
	}

	private void setStage(int stage) {
		this.stage = stage;
		if (stage == 0) {
			hide();
			return;
		}
		show();
		StringBuilder resultText = new StringBuilder();
		if (lastObserveObject instanceof Cell) {
			Cell c = (Cell) lastObserveObject;
			if (!c.isVisibleForPlayer()) {
				hide();
				return;
			}
			if (c.hasMob()) {
				Mob m = c.getMob();
				HealthSystem hs = m.getHealthSystem();
				if (stage == 2) {
					resultText.append((m == Game.player ? "Вы" : m.getName()) + "\n\n");
				}
				if (stage >= 1) {
					resultText.append("Здоровье: " + hs.getHealth() + "\n");
					resultText.append("Боль: " + hs.getPain() + "\n");
					Weapon w = m.getWeapon();
					resultText.append("Оружие: " + (w == null ? m.getHealthSystem()
							.getCombatArm() : w).getDescription() + "\n");
				}
				if (stage == 2) {
					resultText.append("leftActionPoints: " + m.getLeftActionPoints()
							+ "\n");// debug
					resultText.append("isMobNear(player): " + m.isMobNear(Game.player)
							+ "\n");// debug
					if (m instanceof NPC) {
						resultText.append("behavior: " + ((NPC) m).getBehavior()
								.getClass().getSimpleName() + "\n");// debug
					}
					for (Part p : hs.getParts()) {
						resultText.append(p.getDescription());
						if (!p.getInjuries().isEmpty()) {// debug
							resultText.append('(');
							for (Injury i : p.getInjuries()) {
								resultText.append(i.getDescription());
								resultText.append(", ");
							}
							resultText.deleteCharAt(resultText.length() - 1);
							resultText.deleteCharAt(resultText.length() - 1);
							resultText.append(')');
						}
						resultText.append('\n');
					}
				}
			}
			if (c instanceof Floor) {
				Floor f = (Floor) c;
				List<Item> items = f.getLoot().getItems();
				if (stage >= 1) {
					if (!items.isEmpty()) {
						resultText.append("На полу: \n");
					}
					for (Item i : items) {
						resultText.append(i.getDescription() + "\n");
					}
				}
				if (stage == 2) {
					resultText.append("position: " + c.getI() + ", " + c.getJ() + "\n");// debug
					resultText.append("isPassable: " + c.isPassable());
				}
			}
		} else {
			// Item
			Item i = (Item) lastObserveObject;
			if (stage >= 1) {
				resultText.append(i.getDescription() + "\n");
				if (i instanceof Weapon) {
					Weapon w = (Weapon) i;
					if (w.isRanged()) {
						RangedWeapon ranged = (RangedWeapon) w;
						resultText.append(ranged.getAmmo() + "/" + ranged.getMaxAmmo()
								+ "\n");
					}
				}
			}
			if (stage == 2) {
				// resultText.append(i.getWeight() + " фунтов\n");
			}
		}
		if (resultText.length() == 0) {
			hide();
		}
		text.setText(resultText.toString());
		setSize(Math.max(150, 15 + text.getWidth()), 10 + text.getHeight());
		// setSize(150, 10 + text.getHeight());
	}

	public static DropOutView getInstance() {
		if (singleton == null) {
			singleton = new DropOutView();
		}
		return singleton;
	}
}
