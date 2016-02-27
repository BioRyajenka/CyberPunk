package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.MyScene;
import com.jackson.myengine.Sprite;
import com.jackson.myengine.Text;
import com.jackson.myengine.Utils;

public class StateView extends Sprite {
	private Sprite progressBar, dropOut;
	private Text valText;

	public StateView(String s) {
		super(0, 0, "res/gui/progressbar_bg");

		Text text = new Text(0, 0, s);
		text.setPosition((getWidth() - text.getWidth()) / 2, (getHeight() - text
				.getHeight() - 1) / 2);
		progressBar = new Sprite(0, 0, "res/gui/progressbar");

		dropOut = new Sprite(getWidth(), 0, "res/gui/dark_bg") {
			@Override
			public void setSize(float pWidth, float pHeight) {
				super.setSize(pWidth, pHeight);
				dropOut.setPosition(StateView.this.getWidth() + 1, StateView.this
						.getHeight() - getHeight());
			}
		};
		dropOut.setSize(150, 40);

		valText = new Text(5, 5, "00");
		valText.setColor(.8f, .8f, .8f, 1f);
		dropOut.attachChild(valText);

		attachChildren(progressBar, text, dropOut);

		setAlpha(.85f);
	}

	public void update(int val) {
		progressBar.setSize(getWidth() / 100 * val, getHeight());
		valText.setText("состояние " + val + "/100");
	}

	@Override
	public void onManagedUpdate() {
		super.onManagedUpdate();
		if (!isVisible())
			return;
		float mx = MyScene.mx, my = MyScene.my;
		if (Utils.isSelected(this, mx, my))
			dropOut.show();
		if (!Utils.isSelected(this, mx, my) && !Utils.isSelected(dropOut, mx, my))
			dropOut.hide();
	}
}