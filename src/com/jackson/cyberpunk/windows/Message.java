package com.jackson.cyberpunk.windows;

import com.jackson.cyberpunk.Game;
import com.jackson.cyberpunk.MyScene;
import com.jackson.cyberpunk.gui.Button;
import com.jackson.cyberpunk.gui.Panel;
import com.jackson.myengine.Text;

public class Message extends Panel {
    private Text text;
    private Button ok;

    public Message(String text, float x, float y, float width, float height) {
	super(x, y, width, height);
	this.text = new Text(25, 25, text);
	ok = new Button(0, 0, "Ok");
	ok.setAction(new Runnable() {
	    @Override
	    public void run() {
		destroy();
	    }
	});
	ok.setPosition((width - ok.getWidth()) / 2,
		height - 50 - ok.getHeight());
	attachChildren(this.text, ok);
    }
    
    public void setOkAction(Runnable action) {
	this.ok.setAction(new Runnable() {
	    @Override
	    public void run() {
	        action.run();
		destroy();
	    }
	});
    }

    public void setText(String text) {
	this.text.setText(text);
    }
    
    private void destroy() {
	MyScene.isSceneBlocked = false;
	Game.engine.detachOnUIThread(Message.this);
    }
}