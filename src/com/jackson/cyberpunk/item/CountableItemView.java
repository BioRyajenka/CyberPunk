package com.jackson.cyberpunk.item;

import org.newdawn.slick.UnicodeFont;

import com.jackson.myengine.Text;

public abstract class CountableItemView extends ItemView {
	private Text amountText;

	public CountableItemView(Item item) {
		super(item);
		UnicodeFont font = Text.getDefaultFont(11);
		amountText = new Text(0, 0, font, "");
		attachChild(amountText);
		amountText.setText("" + 666);
		amountText.setColor(1f, 1f, 1f);
	}

	@Override
	public void onManagedUpdate() {
		if (!isGlobalVisible()) {
			return;
		}
		super.onManagedUpdate();
		if (amountText.getText().equals("") || Integer.parseInt(amountText
				.getText()) != getAmount()) {
			amountText.setText("" + getAmount());
		}
		updateValuePos();
	}

	private void updateValuePos() {
		CountableItemView civ = CountableItemView.this;
		UnicodeFont font = amountText.getFont();
		amountText.setPosition(civ.getWidth() - font.getSpaceWidth() - font.getWidth(
				amountText.getText()), civ.getHeight() - font.getLineHeight());
	}
	
	protected abstract int getAmount();
}
