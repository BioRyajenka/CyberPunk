package com.jackson.cyberpunk.health;

import java.util.LinkedList;

import com.jackson.cyberpunk.ContextMenu;
import com.jackson.cyberpunk.health.InjuryManager.InjuryDesc;
import com.jackson.cyberpunk.health.InjuryManager.InjuryType;
import com.jackson.cyberpunk.item.Item;
import com.jackson.cyberpunk.item.ItemView;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils;

public abstract class Part extends Item {
	public static enum Type {
		EYE, ARM, LEG, LUNGS, KIDNEYS, HEART, BRAIN
	};

	private Type type;
	private LinkedList<Injury> injuries;
	protected float specialValue, strength;

	private PartStateView stateView;
	protected boolean basePart;

	public Part(Type type, String inInventoryName, String name, float weight,
			float specialValue, float strength, int cost, boolean basePart) {
		super(inInventoryName, name, weight, typeToI(type), typeToJ(type),
				cost);
		this.type = type;
		this.specialValue = specialValue;
		this.strength = strength;
		this.basePart = basePart;
		injuries = new LinkedList<Injury>();
	}
	
	public boolean isBasePart() {
		return basePart;
	}
	
	private static int typeToI(Type type) {
		switch (type) {
		case ARM:
			return 1;
		case LEG:
			return 1;
//		case BRAIN:
//			return 1;
//		case EYE:
//			return 1;
//		case HEART:
//			return 1;
//		case KIDNEYS:
//			return 1;
//		case LUNGS:
//			return 1;
		default:
			return 1;
		}
	}

	private static int typeToJ(Type type) {
		switch (type) {
		case ARM:
			return 4;
		case LEG:
			return 4;
//		case BRAIN:
//			return 2;
//		case EYE:
//			return 2;
//		case HEART:
//			return 2;
//		case KIDNEYS:
//			return 2;
//		case LUNGS:
//			return 2;
		default:
			return 2;
		}
	}
	
	@Override
	protected ContextMenu onContextMenuCreate(ContextMenu menu) {
		if (!basePart) {
			//using this as tag in order to add it to description
			menu.add(ContextMenu.Type.INV_IMPLANT, this);
		}
		return menu;
	}

	public void update() {
		for (Injury i : injuries)
			i.healSlightly();
		updateStateView();
	}

	protected void updateStateView() {
		if (stateView != null) {
			getPartStateView().update();
		}
	}

	public void hurt(InjuryType injuryType) {
		boolean isFatal = false;
		InjuryDesc desc = InjuryManager.get(injuryType);
		if (desc.isFatal())
			isFatal = Utils.rand.nextFloat() < (100f - 5 * getHealth()) / 100;

		Log.d("Adding " + (isFatal ? "" : "non ") + "fatal injury (" + desc.name
				+ ") to injuries");

		injuries.add(new Injury(desc, isFatal));
	}

	public Type getType() {
		return type;
	}

	public LinkedList<Injury> getInjuries() {
		return injuries;
	}

	public boolean isExists() {
		for (Injury i : injuries)
			if (i.isFatal())
				return false;
		return true;
	}

	public float getHealth() {
		if (!isExists())
			return 0;
		float hurt = 0;
		for (Injury i : injuries)
			hurt += i.getHurt();
		hurt *= 1f + 0.1f * injuries.size();
		float res = Math.max(0, 100 - hurt / strength);
		res *= Math.max(0, 1 - getPain() / 100);
		return res;
	}

	public float getPain() {
		float pain = 0;
		for (Injury i : injuries)
			pain += i.getPain();
		return pain;
	}

	public float getSpecialValue() {
		if (!isExists())
			return 0;
		return getHealth() / 100 * specialValue;
	}

	public Part deepCopy() {
		String prev = inInventoryPicName;
		Part res = deepCopyRealization();
		res.inInventoryPicName = prev;
		return res;
	}

	public abstract Part deepCopyRealization();

	public PartStateView getPartStateView() {
		if (stateView == null)
			stateView = new PartStateView(this);
		return stateView;
	}

	@Override
	public ItemView getView() {
		return super.getView();
	}
}