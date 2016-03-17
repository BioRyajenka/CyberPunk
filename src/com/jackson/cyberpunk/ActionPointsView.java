package com.jackson.cyberpunk;

import com.jackson.cyberpunk.Game.Mode;
import com.jackson.cyberpunk.gui.CycledProgressBar;
import com.jackson.cyberpunk.health.Arm;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.Weapon;
import com.jackson.cyberpunk.mob.Player;
import com.jackson.myengine.CycledSprite;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Sprite;

public class ActionPointsView extends Entity {
	private static ActionPointsView instance;

	private APProgressBar armAP, legAP;

	private ActionPointsView() {
		attachChild(new Sprite(12, 61, "res/gui/arm_action_point"));
		attachChild(new Sprite(12, 76, "res/gui/leg_action_point"));

		Player pl = Game.player;

		armAP = new APProgressBar(26, 61, "ОД взаимодействия",
				"res/gui/progressbar_unit_green", pl.getLeftArmActionPoints());
		legAP = new APProgressBar(26, 76, "ОД передвижения",
				"res/gui/progressbar_unit_green", pl.getLeftLegActionPoints());

		attachChildren(armAP, legAP);
		//attachChild(new CycledSprite(0, 0, 0, 0, "res/gui/progressbar_unit_green", 27, 14));

	}

	private static boolean needManipulationAPCostVisualize;
	private static boolean needMovementAPCostVisualize;

	private static float manipulationAPCost;
	private static float movementAPCost;

	public static void setManipulationAPCost(float cost) {
		manipulationAPCost = cost;
		needManipulationAPCostVisualize = true;
	}

	public static void setMovementAPCost(float cost) {
		movementAPCost = cost;
		needMovementAPCostVisualize = true;
	}

	@Override
	public void onManagedUpdate() {
		Player pl = Game.player;
		float leftManipulationPoints = pl.getLeftArmActionPoints();
		float leftMovementPoints = pl.getLeftLegActionPoints();
		
		armAP.setMaxValue(pl.getHealthSystem().getManipulationAP());
		legAP.setMaxValue(pl.getHealthSystem().getMovingAP());
		
		if (Game.getGameMode() == Mode.EXPLORE) {
			armAP.update(leftManipulationPoints);
			legAP.update(leftMovementPoints);
		}

		float mx = MyScene.mx;
		float my = MyScene.my;
		if (!needManipulationAPCostVisualize) {
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

		if (needManipulationAPCostVisualize) {
			armAP.process(leftManipulationPoints, manipulationAPCost);
			needManipulationAPCostVisualize = false;
		} else {
			armAP.update(leftManipulationPoints);
			armAP.updateAdditional(0f);
		}

		if (needMovementAPCostVisualize) {
			legAP.process(leftMovementPoints, movementAPCost);
			needMovementAPCostVisualize = false;
		} else {
			legAP.update(leftMovementPoints);
			legAP.updateAdditional(0f);
		}
		super.onManagedUpdate();
	}

	public static ActionPointsView getInstance() {
		if (instance == null) {
			instance = new ActionPointsView();
		}
		return instance;
	}

	private static class APProgressBar extends CycledProgressBar {
		private CycledSprite additionalSprite;

		public APProgressBar(float pX, float pY, String dropOutTextPrefix,
				String fillMainImagePath, float maxValue) {
			super(pX, pY, dropOutTextPrefix, fillMainImagePath, maxValue);
			additionalSprite = new CycledSprite(0, 0, fillMainImagePath) {
				@Override
				public void setColor(float pRed, float pGreen, float pBlue,
						float pAlpha) {
					super.setColor(pRed, pGreen, pBlue, pAlpha);
				}
			};
			attachChild(additionalSprite);
		}

		public void updateAdditional(float val) {
			additionalSprite.setSize(calcWidth(val), progressBar.getHeight());
		}

		public void process(float leftAP, float costAP) {
			if (costAP == 0) {
				update(leftAP);
				return;
			}

			if (costAP > leftAP) {
				update(0);
				updateAdditional(costAP);
				additionalSprite.setImage("res/gui/progressbar_unit_red");
				additionalSprite.setAlpha(progressBar.getAlpha());
				additionalSprite.setPosition(0, 0);
				additionalSprite.setStartPosXY(0, 0);
			} else {
				float newVal = leftAP - costAP;

				update(newVal);
				float pbWidth = calcWidth(leftAP) - calcWidth(costAP);
				progressBar.setWidth(pbWidth);
				additionalSprite.setPosition(calcWidth(leftAP) - calcWidth(costAP), 0);
				float newWidth = calcWidth(costAP);
				additionalSprite.setWidth(newWidth);
				additionalSprite.setImage("res/gui/progressbar_unit_green");
				additionalSprite.setAlpha(0.25f);
				float newPosX = progressBar.getWidth() - ((int) newVal) * (progressBar
						.getInitialWidth() - 1);
				additionalSprite.setStartPosXY(newPosX, 0);
			}
		}
	}
}