package com.jackson.myengine;

import java.util.LinkedList;

public class Entity {
	private boolean mVisible = true;
	private boolean mIgnoreUpdate = false;
	private Entity mParent;
	protected float mX, mY;
	private LinkedList<Entity> mChildren;
	protected float mRed = 1f, mGreen = 1f, mBlue = 1f, mAlpha = 1f;

	public Entity() {
		this(0, 0);
	}

	public Entity(float pX, float pY) {
		mX = pX;
		mY = pY;
	}

	public void draw() {
		if (mChildren != null)
			for (Entity e : mChildren)
				if (e.isVisible())
					e.draw();
	}

	public void onManagedUpdate() {
		if (mChildren != null)
			for (Entity e : mChildren)
				if (!e.isIgnoreUpdate())
					e.onManagedUpdate();
	}

	public boolean isVisible() {
		return mVisible;
	}

	public boolean isGlobalVisible() {
		boolean res = isVisible();
		Entity par = getParent();
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

	public Entity getParent() {
		return mParent;
	}

	public void setParent(Entity pEntity) {
		mParent = pEntity;
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public void setPosition(Entity pOtherEntity) {
		setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}

	public void setPosition(float pX, float pY) {
		mX = pX;
		mY = pY;
	}

	public float getGlobalX() {
		Entity par = mParent;
		float tX = mX;
		while (par != null) {
			tX += par.getX();
			par = par.getParent();
		}
		return tX;
	}

	public float getGlobalY() {
		Entity par = mParent;
		float tY = mY;
		while (par != null) {
			tY += par.getY();
			par = par.getParent();
		}
		return tY;
	}

	private void ensureChildren() {
		if (mChildren == null) {
			mChildren = new LinkedList<Entity>();
		}
	}

	public LinkedList<Entity> getChildren() {
		ensureChildren();
		return mChildren;
	}

	public void onAttached() {

	}

	public void onDetached() {

	}

	public void attachChild(Entity pEntity) {
		ensureChildren();
		if (mChildren.contains(pEntity)) {
			Log.e("Entity " + this + " is already containing this child! " + pEntity);
			Log.printStackTrace();
			return;
		}
		if (pEntity.hasParent()) {
			Log.e("Entity " + pEntity + " already has a parent (" + pEntity.getParent()
					+ ")!");
			return;
		}
		mChildren.add(pEntity);
		pEntity.setParent(this);
		pEntity.onAttached();
	}

	public void attachChildren(Entity... pEntity) {
		for (Entity e : pEntity)
			attachChild(e);
	}

	public Entity getFirstChild() {
		if (mChildren == null || mChildren.size() == 0)
			return null;
		return mChildren.getFirst();
	}

	public Entity getLastChild() {
		if (mChildren == null || mChildren.size() == 0)
			return null;
		return mChildren.getLast();
	}

	public boolean detachSelf() {
		Entity par = getParent();
		if (par == null) {
			Log.e("Trying to detach entity which has not parent!");
			return false;
		}
		return par.detachChild(this);
	}

	public boolean detachChild(Entity pEntity) {
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
		for (Entity e : mChildren) {
			e.setParent(null);
			e.onDetached();
		}
		mChildren.clear();
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
			for (Entity e : mChildren) {
				e.setColor(pRed, pGreen, pBlue, pAlpha);
			}
		}
	}
}