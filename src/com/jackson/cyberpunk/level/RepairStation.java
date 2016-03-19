package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.mob.Player;

public class RepairStation extends Station {
	private static final int BASE_REPAIR_COST = 500;
	private static final int HACKED_REPAIR_COST = 250;
	
	public RepairStation(int posI, int posJ, String floorPicName, String wallMaterial) {
		super(posI, posJ, "res/level/environment/repair_station", floorPicName,
				wallMaterial);
	}
	
	public int getRepairCost() {
		return isHacked() ? HACKED_REPAIR_COST : BASE_REPAIR_COST;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		if (!isVisibleForPlayer()) {
			return menu;
		}
		Player pl = Game.player;
		HealthSystem hs = pl.getHealthSystem();
		boolean ok = false;
		for (Part p : hs.getParts()) {
			if (!p.isOrganic()) {
				menu.add(Type.LVL_USE_REPAIR_STATION, p);
				ok = true;
			}
		}
		if (!ok) {
			menu.add(Type.NOT_ACTIVE, "Нечего чинить");
		}
		return menu;
	}
}