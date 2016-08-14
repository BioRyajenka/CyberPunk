package com.jackson.cyberpunk.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.jackson.cyberpunk.Game;
import com.jackson.myengine.Entity;

public class HealthSystemView extends Entity {
	private HealthSystem healthSystem;

	public HealthSystemView(HealthSystem healthSystem) {
		this.healthSystem = healthSystem;
	}

	public void onManagedUpdate() {
		// adding parts views and satiety view
		List<Entity> toDetach = new ArrayList<>();
		for (Entity e : getChildren()) {
			final Part p = ((PartStateView) e).getPart();
			if (!healthSystem.getParts().stream().anyMatch(n -> p == n)) {
				toDetach.add(e);
			}
		}

		toDetach.forEach(e -> detachChild(e));

		for (Part n : healthSystem.getParts()) {
			if (!getChildren().stream().anyMatch(p -> p == n.getPartStateView())) {
				attachChild(n.getPartStateView());
			}
		}

		updatePositions();

		super.onManagedUpdate();
	}

	private void updatePositions() {
		Entity arr[] = new Entity[getChildren().size()];
		for (int i = 0; i < getChildren().size(); i++) {
			arr[i] = getChildren().get(i);
		}
		Arrays.sort(arr, new Comparator<Entity>() {
			int getOrder(Entity e) {
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
			float h = ((PartStateView)getChildren().get(0)).getHeight() + 2;
			e.setPosition(5, (Game.SCREEN_HEIGHT - h * arr.length) / 2 + h * i);
		}
	}
}