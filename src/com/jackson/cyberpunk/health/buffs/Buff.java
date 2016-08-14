package com.jackson.cyberpunk.health.buffs;

import com.jackson.cyberpunk.health.Part;

public abstract class Buff<Holder> {
	/**
	 * If string is null, then buff shouldn't be visualized
	 */
	private String pictureName;
	private String[] descriptionByLevel;

	protected int level = 0;
	protected Holder holder;

	public Buff(Holder holder, String pictureName, String[] descriptionByLevel) {
		this.holder = holder;
		this.pictureName = pictureName;
		this.descriptionByLevel = descriptionByLevel;
	}

	public String getPictureName() {
		return pictureName;
	}

	public String getDescription() {
		if (level - 1 < descriptionByLevel.length) {
			return descriptionByLevel[level - 1];
		}
		return null;
	}

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj && getClass() == obj.getClass();
	}

	protected abstract int checkLevel();

	public void update() {
		level = checkLevel();
		update2();
	}

	protected abstract void update2();

	public static abstract class PartBuff extends Buff<Part> {
		public PartBuff(Part part, String pictureName, String[] descriptionByLevel) {
			super(part, pictureName, descriptionByLevel);
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && holder == ((PartBuff) obj).holder;
		}
	}
}