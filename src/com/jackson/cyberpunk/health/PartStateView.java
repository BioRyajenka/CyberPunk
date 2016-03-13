package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.gui.NamedProgressBar;

public class PartStateView extends NamedProgressBar {
	private Part part;

	public PartStateView(Part part) {
		super(0, 0, "состояние", part.getDescription(), "res/gui/progressbar", 100);
		this.part = part;
		update();
	}

	public void update() {
		super.update((int) part.getHealth());
	}

	public Part getPart() {
		return part;
	}
}
