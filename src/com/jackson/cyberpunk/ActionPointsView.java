package com.jackson.cyberpunk;

import com.jackson.cyberpunk.gui.ProgressBarWithAccumulator;
import com.jackson.cyberpunk.gui.ProgressBarWithCost;
import com.jackson.cyberpunk.health.Arm;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;

public class ActionPointsView extends Entity {
	private static ActionPointsView instance;

	private ProgressBarWithCost armAPView;
	private ProgressBarWithAccumulator legAPView;

	private ActionPointsView() {
		attachChild(new Sprite(12, 18, "res/gui/arm_action_point"));
		attachChild(new Sprite(12, 33, "res/gui/leg_action_point"));

		armAPView = new ProgressBarWithCost(26, 18, "ОД взаимодействия",
				"res/gui/progressbar_unit_green");
		legAPView = new ProgressBarWithAccumulator(26, 33, "ОД передвижения",
				"res/gui/progressbar_unit_green");
		armAPView.setAlpha(.85f);
		legAPView.setAlpha(.85f);

		attachChildren(armAPView, legAPView);

	}

	private static boolean needManipulationAPCostVisualize;
	private static float manipulationAPCost;

	/**
	 * @see ContextMenuView.ItemView.onManagedUpdate()
	 */
	public static void setManipulationAPCost(float cost) {
		manipulationAPCost = cost;
		needManipulationAPCostVisualize = true;
	}

	@Override
	public void onManagedUpdate() {
		Player pl = Game.player;
		float leftManipulationPoints = pl.getLeftManipulationAP();
		float leftMovingAP = pl.getFloatLeftMovingAP();

		float mx = MyScene.mx;
		float my = MyScene.my;
		if (!needManipulationAPCostVisualize && !ContextMenuView.getInstance()
				.isVisible()) {
			if (InventoryWindow.getInstance().isVisible()) {
				for (Item i : InventoryWindow.getInstance().getItems()) {
					if (i.getView().isSelected(mx, my)) {
						if (i instanceof Weapon) {
							ActionPointsView.setManipulationAPCost(((Weapon) i)
									.getAttackAP());
						}
						if (i instanceof Arm) {
							ActionPointsView.setManipulationAPCost(((Arm) i)
									.getAttackAP());
						}
					}
				}
			}
		}

		Weapon w = pl.getWeapon();
		boolean twoHanded = w != null && w.isTwoHanded();
		armAPView.setMaxValue(pl.getHealthSystem().getManipulationAP(twoHanded));
		legAPView.setMaxValue(pl.getHealthSystem().getMovingAP());

		if (needManipulationAPCostVisualize) {
			armAPView.update(leftManipulationPoints, manipulationAPCost);
			needManipulationAPCostVisualize = false;
		} else {
			armAPView.update(leftManipulationPoints, 0);
		}
		int ilma = (int)leftMovingAP;
		legAPView.update(ilma, leftMovingAP - ilma);

		super.onManagedUpdate();
	}

	public static ActionPointsView getInstance() {
		if (instance == null) {
			instance = new ActionPointsView();
		}
		return instance;
	}
}