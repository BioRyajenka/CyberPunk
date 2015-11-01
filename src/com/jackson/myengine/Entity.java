package com.jackson.myengine;

import java.util.LinkedList;

public class Entity implements IEntity {
	private boolean mVisible = true;
	private boolean mIgnoreUpdate = false;
	private IEntity mParent;
	protected float mX, mY;
	private LinkedList<IEntity> mChildren;
	private Object mUserData;
	protected float mRed = 1f, mGreen = 1f, mBlue = 1f, mAlpha = 1f;

	public Entity() {
		this(0, 0);
	}

	public Entity(float pX, float pY) {
		mX = pX;
		mY = pY;
	}

	public void onDraw() {
		if (mChildren != null)
			for (IEntity e : mChildren)
				if (e.isVisible())
					e.onDraw();
	}

	public void onManagedUpdate() {
		if (mChildren != null)
			for (IEntity e : mChildren)
				if (!e.isIgnoreUpdate())
					e.onManagedUpdate();
	}

	public boolean isVisible() {
		return mVisible;
	}

	public boolean isGlobalVisible() {
		boolean res = isVisible();
		IEntity par = getParent();
		while (par != null) {
			res &= par.isVisible();
			par = par.getParent();
		}
		return res;
	}

	public void setVisible(boolean pVisible) {
		mVisible = pVisible;
	}

	public void hide() {
		mVisible = false;
	}

	public void show() {
		mVisible = true;
	}

	public boolean isIgnoreUpdate() {
		return mIgnoreUpdate;
	}

	public void setIgnoreUpdate(boolean pIgnoreUpdate) {
		mIgnoreUpdate = pIgnoreUpdate;
	}

	public boolean hasParent() {
		return mParent != null;
	}

	public IEntity getParent() {
		return mParent;
	}

	public void setParent(IEntity pEntity) {
		mParent = pEntity;
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public void setPosition(IEntity pOtherEntity) {
		setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}

	public void setPosition(float pX, float pY) {
		mX = pX;
		mY = pY;
	}

	public float getGlobalX() {
		IEntity par = mParent;
		float tX = mX;
		while (par != null) {
			tX += par.getX();
			par = par.getParent();
		}
		return tX;
	}

	public float getGlobalY() {
		IEntity par = mParent;
		float tY = mY;
		while (par != null) {
			tY += par.getY();
			par = par.getParent();
		}
		return tY;
	}

	private void allocateChildren() {
		mChildren = new LinkedList<IEntity>();
	}

	public int getChildCount() {
		if (mChildren == null)
			return 0;
		return mChildren.size();
	}

	public void onAttached() {

	}

	public void onDetached() {

	}

	public void attachChild(IEntity pEntity) {
		if (mChildren == null)
			allocateChildren();
		if (mChildren.contains(pEntity)) {
			Log.e("Entity.java: Entity is already containing this child!");
			return;
		}
		if (pEntity.hasParent()) {
			Log.e("Entity.java: Entity " + pEntity + " already has a parent (" + pEntity
					.getParent() + ")!");
			return;
		}
		mChildren.add(pEntity);
		pEntity.setParent(this);
		pEntity.onAttached();
	}

	public void attachChildren(IEntity... pEntity) {
		for (IEntity e : pEntity)
			attachChild(e);
	}

	public IEntity getChild(int pIndex) {
		if (mChildren == null || pIndex < 0 || pIndex >= mChildren.size())
			return null;
		return mChildren.get(pIndex);
	}

	public IEntity getFirstChild() {
		if (mChildren == null || mChildren.size() == 0)
			return null;
		return mChildren.getFirst();
	}

	public IEntity getLastChild() {
		if (mChildren == null || mChildren.size() == 0)
			return null;
		return mChildren.getLast();
	}

	public int getChildIndex(IEntity pEntity) {
		if (mChildren == null || mChildren.size() == 0)
			return -1;
		return mChildren.indexOf(pEntity);
	}

	public boolean detachSelf() {
		IEntity par = getParent();
		if (par == null) {
			Log.e("Trying to detach entity which has not parent!");
			return false;
		}
		return par.detachChild(this);
	}

	public boolean detachChild(IEntity pEntity) {
		if (mChildren == null)
			return false;
		if (mChildren.remove(pEntity)) {
			pEntity.setParent(null);
			pEntity.onDetached();
			return true;
		}
		return false;
	}

	public void detachChildren() {
		if (mChildren == null)
			return;
		for (IEntity e : mChildren) {
			e.setParent(null);
			e.onDetached();
		}
		mChildren.clear();
	}

	public void setUserData(Object pUserData) {
		mUserData = pUserData;
	}

	public Object getUserData() {
		return mUserData;
	}

	public float getRed() {
		return mRed;
	}

	public float getGreen() {
		return mGreen;
	}

	public float getBlue() {
		return mBlue;
	}

	public float getAlpha() {
		return mAlpha;
	}

	public void setAlpha(float pAlpha) {
		setColor(mRed, mGreen, mBlue, pAlpha);
	}

	public void setColor(float pRed, float pGreen, float pBlue) {
		setColor(pRed, pGreen, pBlue, getAlpha());
	}

	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		mRed = pRed;
		mGreen = pGreen;
		mBlue = pBlue;
		mAlpha = pAlpha;
		if (mChildren != null) {
			for (IEntity e : mChildren) {
				e.setColor(pRed, pGreen, pBlue, pAlpha);
			}
		}
	}
}