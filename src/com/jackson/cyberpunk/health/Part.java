package com.jackson.cyberpunk.health;

import java.util.ArrayList;
import java.util.List;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.health.buffs.Buff;
import com.jackson.cyberpunk.health.buffs.PainLocalBuff;
import com.jackson.cyberpunk.item.Item;

public abstract class Part extends Item {
	public enum Type {
		ARM, LEG, EYE;
	}

	Type type;

	private List<Injury> injuries = new ArrayList<Injury>();
	protected float strength;
	protected boolean organic;

	private PartStateView stateView;

	private List<Buff<Part>> buffs = new ArrayList<>();
	protected List<Effect> permanentEffects;
	private List<Effect> effects = new ArrayList<>();

	public Part(Type type, String name, String description, String pictureName, int sizeI,
			int sizeJ, float strength, int cost, boolean organic, List<Effect> effects) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.type = type;
		this.strength = strength;
		this.permanentEffects = effects;
		this.organic = organic;
		
		buffs.add(new PainLocalBuff(this));
	}

	public void addBuff(Buff<Part> buff) {
		buffs.add(buff);
	}

	public void applyEffect(Effect e) {
		effects.add(e);
	}

	public boolean isOrganic() {
		return organic;
	}
	
	public List<Buff<Part>> getBuffs() {
		return buffs;
	}

	public List<Effect> getEffects() {
		List<Effect> res = new ArrayList<>(permanentEffects);
		res.addAll(effects);
		return res;
	}

	public Type getType() {
		return type;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	public void update() {
		injuries.forEach(i -> i.healSlightly());
		effects.clear();
		buffs.forEach(b -> b.update());
	}

	public void hurt(Injury injury) {
		if (!isFunction()) {
			return;
		}
		injuries.add(injury.copy());
	}

	public List<Injury> getInjuries() {
		return injuries;
	}

	public boolean isFunction() {
		return getHealth() > 5f;
	}

	/**
	 * from 0 to 100
	 */
	public float getHealth() {
		float hurt = 0;
		for (Injury i : injuries) {
			hurt += i.getHurt();
		}
		return Math.max(0, 100 - hurt / strength);
	}

	public PartStateView getPartStateView() {
		if (stateView == null) {
			stateView = new PartStateView(this);
		}
		return stateView;
	}
}