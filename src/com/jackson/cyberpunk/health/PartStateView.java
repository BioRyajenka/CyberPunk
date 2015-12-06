package com.jackson.cyberpunk.health;

public class PartStateView extends StateView {
	private Part part;

	public PartStateView(Part part) {
		super(part.getName());
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
