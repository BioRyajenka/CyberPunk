package com.jackson.cyberpunk.mob;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.mob.behavior.PassiveBehavior;

public class Dealer extends NPC {
	public Dealer() {
		super("dealer", "Посредник", 6, PassiveBehavior.class);
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		Player pl = Game.player;
		for (Item i : pl.getInventory().getItems()) {
			if (i instanceof Part && !((Part) i).isOrganic()) {
				menu.add(Type.MOB_DEALER_INSTALL_IMPLANT, i);
			}
		}
		return menu;
	}
}
