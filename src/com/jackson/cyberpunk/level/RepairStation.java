package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.health.HealthSystem;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.mob.Player;

public class RepairStation extends Wall {
	public RepairStation(int posI, int posJ, String floorPicName, String wallMaterial) {
		super(posI, posJ, floorPicName, wallMaterial, RepairStationView.class);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
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
			menu.add(Type.NOT_ACTIVE, "������ ������");
		}
		return menu;
	}
}