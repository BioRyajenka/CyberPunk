package com.jackson.myengine;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Text extends Entity {
	public static enum Align {
		CENTER, NORMAL
	};

	private String text;
	private UnicodeFont font;
	private Align align;

	public Text(float pX, float pY, String text) {
		this(pX, pY, getDefaultFont(), text);
	}

	public Text(float pX, float pY, UnicodeFont font, String text) {
		super(pX, pY);
		this.text = text;
		setFont(font);
		setAlign(Align.NORMAL);
		setColor(0f, 0f, 0f);// black
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public void setFont(String name, int size) {
		setFont(new Font(name, Font.ROMAN_BASELINE, size));
	}

	public void setText(String newText) {
		String prevText = this.text;
		this.text = newText;
		if (align == Align.CENTER) {
			setPosition(getX() + font.getWidth(prevText) / 2 - font.getWidth(newText)
					/ 2, getY());
		}
	}

	public String getText() {
		return text;
	}

	public float getWidth() {
		// for some reason font.getWidth(s) works with only last line
		// it's slick2d bug
		float result = 0;
		for (String s : text.split("\n")) {
			result = Math.max(result, font.getWidth(s));
		}
		return result;
	}

	public float getHeight() {
		return font.getHeight(text);
	}

	public void setFont(Font font) {
		this.font = fontToUnicodeFont(font);
	}

	public void setFont(UnicodeFont font) {
		this.font = font;
	}

	public UnicodeFont getFont() {
		return font;
	}

	public static UnicodeFont getDefaultFont() {
		return fontToUnicodeFont(new Font("Verdana", Font.ROMAN_BASELINE, 14));
	}

	public static UnicodeFont getDefaultFont(int pt) {
		return fontToUnicodeFont(new Font("Verdana", Font.ROMAN_BASELINE, pt));
	}

	@SuppressWarnings("unchecked")
	public static UnicodeFont fontToUnicodeFont(Font font) {
		UnicodeFont res = new UnicodeFont(font);
		res.addAsciiGlyphs();
		res.addGlyphs(32, 1200);
		res.getEffects().add(new ColorEffect(java.awt.Color.WHITE)); // Create a
		// default
		// white
		// color
		// effect
		try {
			res.loadGlyphs();
		} catch (SlickException e) {
			Log.e("Text.java: " + e.toString());
		}
		return res;
	}

	@Override
	public void draw() {
		// font.drawString(10, 10, "Test");
		font.drawString(getGlobalX(), getGlobalY(), text, new Color(mRed, mGreen, mBlue,
				mAlpha));
		super.draw();
	}
}