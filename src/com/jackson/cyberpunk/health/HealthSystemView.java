package com.jackson.cyberpunk.health;

import java.util.Arrays;
import java.util.Comparator;

import com.jackson.cyberpunk.Game;
import com.jackson.myengine.Entity;
import com.jackson.myengine.IEntity;

public class HealthSystemView extends Entity {
	private HealthSystem healthSystem;
	private StateView satietyView;

	public HealthSystemView(HealthSystem healthSystem) {
		this.healthSystem = healthSystem;
		satietyView = new StateView("cûעמסעü");
		satietyView.update((int) healthSystem.getSatiety());
		attachChild(satietyView);

		update();
	}

	public void update() {
		// adding parts views and satiety view
		for (int i = 0; i < getChildCount(); i++) {
			IEntity e = getChild(i);
			if (e instanceof PartStateView) {
				final Part p = ((PartStateView) e).getPart();
				boolean ok = false;
				for (Part n : healthSystem.getParts())
					ok |= p.equals(n);
				if (!ok) {
					Game.engine.runOnUIThread(new Runnable() {
						public void run() {
							p.getPartStateView().detachSelf();
						}
					});
				}
			}
		}

		for (final Part n : healthSystem.getParts()) {
			boolean ok = false;
			for (int i = 0; i < getChildCount(); i++) {
				IEntity e = getChild(i);
				if (e instanceof PartStateView) {
					Part p = ((PartStateView) e).getPart();
					ok |= (p.equals(n));
				}
			}
			if (!ok) {
				// Game.engine.runOnUIThread(new Runnable(){
				// public void run(){
				attachChild(n.getPartStateView());
				// }
				// });
			}
		}

		updatePositions();

		satietyView.update((int) healthSystem.getSatiety());
	}

	private void updatePositions() {
		IEntity arr[] = new Entity[getChildCount()];
		for (int i = 0; i < getChildCount(); i++) {
			arr[i] = getChild(i);
		}
		Arrays.sort(arr, new Comparator<IEntity>() {
			int getOrder(IEntity e) {
				if (e instanceof PartStateView) {
					Part p = ((PartStateView)e).getPart();
					int res;
					switch (p.getType()) {
					case BRAIN:
						res = 0;
						break;
					case HEART:
						res = 1;
						break;
					case EYE:
						res = 2;
						break;
					case ARM:
						res = 4;
						break;
					case LEG:
						res = 6;
						break;
					case KIDNEYS:
						res = 8;
						break;
					case LUNGS:
						res = 9;
						break;
					default:
						throw new RuntimeException("can't sort this part");
					}
					if (p instanceof IDualPart) {
						res++;
					}
					return res;
				}
				return -1;//satiety
			}
			
			@Override
			public int compare(IEntity a, IEntity b) {
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
			IEntity e = arr[i];
			float h = satietyView.getHeight() + 2;
			e.setPosition(5, (Game.SCREEN_HEIGHT - h * getChildCount()) / 2 + h * i);
		}
	}
}