package com.jackson.cyberpunk.health;

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
			if (e instanceof PartView) {
				final Part p = ((PartView) e).getPart();
				boolean ok = false;
				for (Part n : healthSystem.getParts())
					ok |= p.equals(n);
				if (!ok) {
					Game.engine.runOnUIThread(new Runnable() {
						public void run() {
							p.getPartView().detachSelf();
						}
					});
				}
			}
		}

		for (final Part n : healthSystem.getParts()) {
			boolean ok = false;
			for (int i = 0; i < getChildCount(); i++) {
				IEntity e = getChild(i);
				if (e instanceof PartView) {
					Part p = ((PartView) e).getPart();
					ok |= (p.equals(n));
				}
			}
			if (!ok) {
				// Game.engine.runOnUIThread(new Runnable(){
				// public void run(){
				attachChild(n.getPartView());
				// }
				// });
			}
		}

		updatePositions();

		satietyView.update((int) healthSystem.getSatiety());
	}

	private void updatePositions() {
		for (int i = 0; i < getChildCount(); i++) {
			IEntity e = getChild(i);
			float h = satietyView.getHeight() + 2;
			e.setPosition(5, (Game.SCREEN_HEIGHT - h * getChildCount()) / 2 + h * i);
		}
	}
}