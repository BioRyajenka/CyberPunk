package com.jackson.myengine;

import com.jackson.cyberpunk.MyScene;

public class IlluminatedSprite extends Sprite {
    public enum IlluminationMode {
	SIMPLE, IMPOSITION
    };

    private IlluminationMode mode;
    private Sprite blackoutSprite;
    private float blackout;
    private boolean negativeMode;

    public IlluminatedSprite(float pX, float pY, String path,
	    IlluminationMode mode) {
	super(pX, pY, path);
	this.mode = mode;
	blackout = .3f;
	negativeMode = false;
	if (mode == IlluminationMode.IMPOSITION) {
	    blackoutSprite = new Sprite(0, 0, "gui/white_pixel");
	    blackoutSprite.setSize(getWidth(), getHeight());
	    blackoutSprite.setColor(0f, 0f, 0f, blackout);
	    blackoutSprite.hide();
	    attachChild(blackoutSprite);
	} else
	    blackoutSprite = null;
    }

    @Override
    public void onManagedUpdate() {
	float x = MyScene.mx, y = MyScene.my;

	if (isSelected(x, y))
	    blackIn();
	else
	    blackOut();

	super.onManagedUpdate();
    }

    public void setNegativeMode(boolean f) {
	negativeMode = f;
    }

    @Override
    public void setSize(float pWidth, float pHeight) {
	super.setSize(pWidth, pHeight);
	if (blackoutSprite != null) {
	    blackoutSprite.setSize(pWidth, pHeight);
	}
    }

    @Override
    public void setImage(String path) {
	super.setImage(path);
	if (blackoutSprite != null) { 
	    blackoutSprite.setSize(getWidth(), getHeight());
	}
    }

    public void blackIn() {
	black(!negativeMode);
    }

    public void blackOut() {
	black(negativeMode);
    }

    private void black(boolean f) {
	if (f)
	    if (mode == IlluminationMode.SIMPLE)
		setColor(blackout, blackout, blackout);
	    else
		blackoutSprite.show();
	else if (mode == IlluminationMode.SIMPLE)
	    setColor(1f, 1f, 1f);
	else
	    blackoutSprite.hide();
    }

    public void setBlackout(float blackout) {
	this.blackout = blackout;
	if (blackoutSprite != null)
	    blackoutSprite.setColor(0f, 0f, 0f, blackout);
    }

    public boolean isSelected(float x, float y) {
	return Utils.inBounds(x, getGlobalX(), getGlobalX() + getWidth() - 1)
		&& Utils.inBounds(y, getGlobalY(), getGlobalY() + getHeight()
			- 1);
    }
}