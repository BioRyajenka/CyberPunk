package com.jackson.cyberpunk.health.buffs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.gui.DropOutText;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.myengine.Entity;
import com.jackson.myengine.Rectangle;
import com.jackson.myengine.Sprite;

public class BuffsView extends Entity {
	private static BuffsView instance = new BuffsView();

	public static BuffsView getInstance() {
		return instance;
	}

	private final static float X_POS = 14;
	private final static float INDENT = 5;
	
	private List<BuffView> buffViews = new ArrayList<>();

	private BuffsView() {
	}

	@Override
	public void onManagedUpdate() {
		HealthSystem hs = Game.player.getHealthSystem();
		List<Buff<?>> needVisualization = hs.getAllBuffs().stream().filter(b -> b
				.getPictureName() != null && b.level != 0).collect(Collectors.toList());
		for (int i = 0; i < needVisualization.size(); i++) {
			if (i == buffViews.size()) {
				buffViews.add(new BuffView());
				attachChild(buffViews.get(i));
			}
			buffViews.get(i).setBuff(needVisualization.get(i));
			buffViews.get(i).show();
		}
		for (int i = needVisualization.size(); i < buffViews.size(); i++) {
			buffViews.get(i).hide();
		}

		repositionBuffViews();

		super.onManagedUpdate();
	}

	private void repositionBuffViews() {
		float x = X_POS;
		float y = MyScene.endTurnButton.getY() + MyScene.endTurnButton.getHeight() + X_POS;
		for (int i = 0; i < getChildren().size(); i++) {
			BuffView buffView = (BuffView) getChildren().get(i);
			buffView.setPosition(x, y);
			y += buffView.getHeight() + INDENT;
		}
	}

	private static class BuffView extends Entity implements Rectangle {
		Sprite bg;
		Sprite image;
		Sprite deadly;
		DropOutText dropOutText;

		public BuffView() {
			bg = new Sprite(0, 0, "res/gui/moderate_effect");
			image = new Sprite(0, 0, "res/gui/hunger");
			deadly = new Sprite(0, 0, "res/gui/deadly_effect");
			dropOutText = new DropOutText(bg, true);
			attachChildren(bg, image, deadly, dropOutText);
		}
		
		void setBuff(Buff<?> buff) {
			bg.setImage(buff.level == 1 ? "res/gui/moderate_effect" : "res/gui/strong_effect");
			image.setImage("res/gui/" + buff.getPictureName());
			deadly.setVisible(buff.level == 3);
			dropOutText.setText(buff.getDescription());
		}

		@Override
		public float getHeight() {
			return bg.getHeight();
		}

		@Override
		public float getWidth() {
			return bg.getWidth();
		}
	}
}