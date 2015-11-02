package com.jackson.cyberpunk.level;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.Inventory;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.KnapsackFactory;
import com.jackson.cyberpunk.mob.Player;

public class Floor extends Cell {
	private String floorPicPath;
	protected Inventory loot;
	
	public Floor(int posI, int posJ, String floorPicName) {
		super(posI, posJ, true, FloorView.class);
		loot = new Inventory(KnapsackFactory.create(KnapsackFactory.Type.INFINITY));
		this.floorPicPath = "level/floors/" + floorPicName;
	}
	
	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		Player p = Game.player;
		boolean isReachable = p.isReachableCell(getI(), getJ());
		if (loot.getItems().size() > 0 && isReachable)
			menu.add(Type.LVL_PICK);
		if (getMob() == null && menu.getItems().size() > 0 && isReachable)
			menu.add(Type.LVL_GO);
		return menu;
	}
	
	public void addItem(Item i) {
		loot.add(i);
	}
	
	public void remove(Item i) {
		loot.remove(i);
	}
	
	public Inventory getLoot() {
		return loot;
	}
	
	protected String getFloorPicPath() {
		return floorPicPath;
	}
}
