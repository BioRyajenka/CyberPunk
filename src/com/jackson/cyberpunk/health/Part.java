package com.jackson.cyberpunk.health;

import java.util.ArrayList;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.PartProfit;

public abstract class Part extends Item {
	public enum Type {
		ARM, LEG, EYE;
	}

	Type type;

	private ArrayList<Injury> injuries;
	protected float strength;
	protected ArrayList<PartProfit> profits;
	protected boolean organic;

	private PartStateView stateView;

	public Part(Type type, String name, String description, String pictureName,
			int sizeI, int sizeJ, float strength, int cost, boolean organic,
			ArrayList<PartProfit> profits) {
		super(name, description, pictureName, sizeI, sizeJ, cost);
		this.type = type;
		this.strength = strength;
		this.profits = profits;
		this.organic = organic;
		injuries = new ArrayList<Injury>();
	}
	
	public boolean isOrganic() {
		return organic;
	}

	public ArrayList<PartProfit> getProfits() {
		return profits;
	}

	public Type getType() {
		return type;
	}

	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		return menu;
	}

	public void update() {
		for (Injury i : injuries) {
			i.healSlightly();
		}
		updateStateView();
	}

	protected void updateStateView() {
		if (stateView != null) {
			getPartStateView().update();
		}
	}

	public void hurt(Injury injury) {
		if (!isFunction()) {
			return;
		}
		injuries.add(injury.copy());
	}

	public ArrayList<Injury> getInjuries() {
		return injuries;
	}

	public boolean isFunction() {
		return getHealth() > 0.05f;
	}

	public float getHealth() {
		float hurt = 0;
		for (Injury i : injuries) {
			hurt += i.getHurt();
		}
		return Math.max(0, 100 - hurt / strength);
	}

	public float getPain() {
		float pain = 0;
		for (Injury i : injuries) {
			pain += i.getPain();
		}
		return pain;
	}

	public PartStateView getPartStateView() {
		if (stateView == null) {
			stateView = new PartStateView(this);
		}
		return stateView;
	}
}