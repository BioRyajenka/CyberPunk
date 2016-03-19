package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.level.StateManager.Status;
import com.jackson.myengine.Utils;

public abstract class Station extends Wall {
	private static final float BROKEN_GENERATION_PROBABILITY = .2f;

	private StateManager stateManager;

	private String stationPicName;

	public Station(int posI, int posJ, String stationPicName, String floorPicName,
			String wallMaterial) {
		super(posI, posJ, floorPicName, wallMaterial, StationView.class);
		this.stationPicName = stationPicName;

		Status status = Utils.rand.nextFloat() < BROKEN_GENERATION_PROBABILITY
				? Status.BROKEN : Status.WORKS;
		stateManager = new StateManager(status);
	}

	public String getStationPicName() {
		return stationPicName + (isBroken() ? "_broken" : "");
	}

	public boolean isBroken() {
		return stateManager.isBroken();
	}

	public boolean isHacked() {
		return stateManager.isHacked();
	}
	
	public boolean isWorks() {
		return stateManager.isWorks();
	}

	public boolean tryHack() {
		return stateManager.tryHack();
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		if (stateManager.status == Status.BROKEN) {
			menu.add(Type.NOT_ACTIVE, "Терминал заблокирован");
		}
		if (stateManager.status == Status.WORKS && Game.player.getInventory().contains(
				Notebook.class)) {
			menu.add(Type.LVL_HACK);
		}
		return menu;
	}
}