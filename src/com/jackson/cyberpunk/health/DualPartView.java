package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.item.ItemView;

public class DualPartView extends ItemView {

	public DualPartView(DualPart part) {
		super(part);
		
		if (part.isRight()) {
			flipHorizontally();
		}
	}	
}
