package com.jackson.cyberpunk.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.gui.NamedProgressBar;
import com.jackson.cyberpunk.gui.ProgressBar;
import com.jackson.myengine.Entity;

public class HealthSystemView extends Entity {
	private HealthSystem healthSystem;
	private ProgressBar satietyView;

	public HealthSystemView(HealthSystem healthSystem) {
		this.healthSystem = healthSystem;
		satietyView = new NamedProgressBar(0, 0, "סûעמסעü: ", "cûעמסעü",
				"res/gui/progressbar", 100);
		satietyView.update((int) healthSystem.getSatiety());
		attachChild(satietyView);

		update();
	}
	
	public void update() {
		// adding parts views and satiety view
		List<Entity> toDetach = new ArrayList<>();
		for (Entity e : getChildren()) {
			if (e instanceof PartStateView) {
				final Part p = ((PartStateView) e).getPart();
				boolean ok = false;
				for (Part n : healthSystem.getParts()) {
					ok |= p == n;
				}
				if (!ok) {
					toDetach.add(e);
				}
			}
		}
		
		for (Entity e : toDetach) {
			detachChild(e);
		}

		for (final Part n : healthSystem.getParts()) {
			boolean ok = false;
			for (Entity e : getChildren()) {
				if (e instanceof PartStateView) {
					Part p = ((PartStateView) e).getPart();
					ok |= p == n;
				}
			}
			if (!ok) {
				attachChild(n.getPartStateView());
			}
		}

		updatePositions();

		satietyView.update((int) healthSystem.getSatiety());
	}

	private void updatePositions() {
		Entity arr[] = new Entity[getChildren().size()];
		for (int i = 0; i < getChildren().size(); i++) {
			arr[i] = getChildren().get(i);
		}
		Arrays.sort(arr, new Comparator<Entity>() {
			int getOrder(Entity e) {
				if (e instanceof PartStateView) {
					Part p = ((PartStateView) e).getPart();
					int res;
					switch (p.getType()) {
					case EYE:
						res = 2;
						break;
					case ARM:
						res = 4;
						break;
					case LEG:
						res = 6;
						break;
					default:
						throw new RuntimeException("can't sort this part");
					}
					if (p instanceof DualPart && ((DualPart) p).isRight()) {
						res++;
					}
					return res;
				}
				return -1;// satiety
			}

			@Override
			public int compare(Entity a, Entity b) {
				if (getOrder(a) < getOrder(b)) {
					return -1;
				}
				if (getOrder(a) == getOrder(b)) {
					return 0;
				}
				return 1;
			}
		});

		for (int i = 0; i < arr.length; i++) {
			Entity e = arr[i];
			float h = satietyView.getHeight() + 2;
			e.setPosition(5, (Game.SCREEN_HEIGHT - h * arr.length) / 2 + h * i);
		}
	}
}