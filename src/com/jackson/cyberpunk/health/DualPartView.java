package com.jackson.cyberpunk.health;

import com.jackson.cyberpunk.health.Part.Type;
import com.jackson.cyberpunk.item.ItemView;

public class DualPartView extends ItemView {

	public DualPartView(DualPart part) {
		super(part);
		
		if (part.type == Type.ARM && part.isRight()) {
			flipHorizontally();
			flipVertically();
		}
		if (part.type == Type.LEG && part.isRight()) {
			flipHorizontally();
		}
	}	
}
