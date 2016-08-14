package com.jackson.cyberpunk.item;

import java.util.ArrayList;
import java.util.List;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.ContextMenu.Type;
import com.jackson.cyberpunk.health.Part;
import com.jackson.cyberpunk.mob.Mob;

public class Corpse extends Item {
	private List<Part> parts;

	public Corpse(Mob m) {
		super("corpse", "Труп " + m.getName() + "а", "res/items/corpse", 2, 5, 500);
		parts = new ArrayList<>();
		parts.addAll(m.getHealthSystem().getParts());
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		for (Part p : parts) {
			menu.add(Type.INV_AMPUTATE, p);
		}
		// TODO: remove organs
		return menu;
	}

	@Deprecated
	public Item copy() {
		return null;
	}

	public void removePart(Part part) {
		parts.remove(part);
	}
}
