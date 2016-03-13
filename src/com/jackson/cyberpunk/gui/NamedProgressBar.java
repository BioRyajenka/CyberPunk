package com.jackson.cyberpunk.gui;

import com.jackson.myengine.Text;

public class NamedProgressBar extends SimpleProgressBar {
	public NamedProgressBar(float pX, float pY, String dropOutTextPrefix, String text, 
			String fillImagePath, int maxVal) {
		super(pX, pY, dropOutTextPrefix, fillImagePath, maxVal);
		Text textView = new Text(0, 0, text);
		textView.setPosition((getWidth() - textView.getWidth()) / 2, (getHeight()
				- textView.getHeight() - 1) / 2);
		detachLastChild();
		attachChildren(textView, dropOut);
	}
}