package com.jackson.myengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity {
	private boolean visible = true;
	private boolean ignoreUpdate = false;
	private Entity parent;
	protected float x, y;
	private List<Entity> children;
	protected float red = 1f, green = 1f, blue = 1f, alpha = 1f;

	public Entity() {
		this(0, 0);
	}

	public Entity(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void draw() {
		if (children != null) {
			for (Entity e : children) {
				if (e.isVisible()) {
					e.draw();
				}
			}
		}
	}

	public void onManagedUpdate() {
		if (children != null) {
			children.forEach(e -> {
				if (!e.isIgnoreUpdate()) {
					e.onManagedUpdate();
				}
			});
		}
	}

	public boolean isVisible() {
		return visible;
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

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void hide() {
		setVisible(false);
	}

	public void show() {
		setVisible(true);
	}

	public boolean isIgnoreUpdate() {
		return ignoreUpdate;
	}

	public void setIgnoreUpdate(boolean ignoreUpdate) {
		this.ignoreUpdate = ignoreUpdate;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity entity) {
		parent = entity;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setPosition(Entity anotherEntity) {
		setPosition(anotherEntity.getX(), anotherEntity.getY());
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getGlobalX() {
		Entity par = parent;
		float tX = x;
		while (par != null) {
			tX += par.getX();
			par = par.getParent();
		}
		return tX;
	}

	public float getGlobalY() {
		Entity par = parent;
		float tY = y;
		while (par != null) {
			tY += par.getY();
			par = par.getParent();
		}
		return tY;
	}

	private void ensureChildren() {
		if (children == null) {
			children = new ArrayList<Entity>();
		}
	}

	public List<Entity> getChildren() {
		ensureChildren();
		return children;
	}

	public void onAttached() {

	}

	public void onDetached() {

	}

	public void attachChild(Entity pEntity) {
		ensureChildren();
		if (children.contains(pEntity)) {
			Log.e("Entity " + this + " is already contains this child! " + pEntity);
			Log.printStackTrace(System.err);
			return;
		}
		if (pEntity.hasParent()) {
			Log.e("Entity " + pEntity + " already has a parent (" + pEntity.getParent()
					.pedigreeToString() + ")!");
			Log.printStackTrace(System.err);
			return;
		}
		children.add(pEntity);
		pEntity.setParent(this);
		pEntity.onAttached();
	}

	public String pedigreeToString() {
		String res = "";
		Entity par = this;
		while (par != null) {
			res += par + " ";
			par = par.parent;
		}
		return res;
	}

	public void attachChildren(Entity... entities) {
		Arrays.stream(entities).forEach(e -> attachChild(e));
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
		if (children == null) {
			return false;
		}
		if (children.remove(pEntity)) {
			pEntity.setParent(null);
			pEntity.onDetached();
			return true;
		}
		return false;
	}

	public void detachChildren() {
		if (children == null) {
			return;
		}
		children.forEach(e -> {
			e.setParent(null);
			e.onDetached();
		});
		children.clear();
	}
	
	public boolean detachLastChild() {
		return children.get(children.size() - 1).detachSelf();
	}

	public float getRed() {
		return red;
	}

	public float getGreen() {
		return green;
	}

	public float getBlue() {
		return blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float pAlpha) {
		setColor(red, green, blue, pAlpha);
	}

	public void setColor(float pRed, float pGreen, float pBlue) {
		setColor(pRed, pGreen, pBlue, getAlpha());
	}

	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		red = pRed;
		green = pGreen;
		blue = pBlue;
		alpha = pAlpha;
		if (children != null) {
			children.forEach(e -> e.setColor(pRed, pGreen, pBlue, pAlpha));
		}
	}
}