package com.jackson.cyberpunk.health;

import java.util.ArrayList;

import com.jackson.cyberpunk.health.InjuryManager.InjuryType;
import com.jackson.cyberpunk.health.Part.Type;
import com.jackson.cyberpunk.item.Weapon.InjuryHelper;
import com.jackson.myengine.Log;
import com.jackson.myengine.Utils.Pair;

public class PartsManager {
    private static ArrayList<Part> parts;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void init() {
	parts = new ArrayList<Part>();

	parts.add(new Eye("wooden_eye", "глазной протез", 0f, 3f, 30, false));
	parts.add(new Eye("eye", "глаз", 1f, 1f, 1000, true));
	parts.add(new Eye("bionic_eye", "бионический глаз", 1.7f, 1.75f, 8000, false));
	parts.add(new Eye("advanced_bionic_eye", "усиленный бионический глаз",
		3f, 2.5f, 15000, false));

	parts.add(new Arm("hook_arm", "крюк", 14f, 0f, 4f, 200,
		new InjuryHelper(new Pair(InjuryType.BRUISE, .1f), new Pair(
			InjuryType.SCRATCH, .2f),
			new Pair(InjuryType.CUT, .7f), new Pair(
				InjuryType.CRACK, 1f)), false));
	parts.add(new Arm("wooden_arm", "деревянная рука", 7f, 0.2f, 4f, 30,
		new InjuryHelper(new Pair(InjuryType.SCRATCH, .15f), new Pair(
			InjuryType.BRUISE, .95f),
			new Pair(InjuryType.CRACK, 1f)), false));
	parts.add(new Arm("arm", "рука", 11f, 1f, 1f, 800, new InjuryHelper(
		new Pair(InjuryType.SCRATCH, .1f), new Pair(InjuryType.BRUISE,
			.95f), new Pair(InjuryType.CRACK, 1f)), true));
	parts.add(new Arm("bionic_arm", "бионическая рука", 13f, 1.75f, 1.75f,
		7000, new InjuryHelper(new Pair(InjuryType.SCRATCH, .2f),
			new Pair(InjuryType.BRUISE, .8f), new Pair(
				InjuryType.CRACK, .95f), new Pair(
				InjuryType.CRUSH, 1f)), false));
	parts.add(new Arm("advanced_bionic_arm", "усиленная бионическая рука",
		15f, 2.5f, 2.5f, 13000, new InjuryHelper(new Pair(
			InjuryType.SCRATCH, .15f),
			new Pair(InjuryType.CUT, .3f), new Pair(
				InjuryType.BRUISE, .7f), new Pair(
				InjuryType.CRACK, .9f), new Pair(
				InjuryType.CRUSH, 1f)), false));

	parts.add(new Leg("wooden_eye", "деревянная нога", 15f, .7f, 2.5f, 30, false));
	parts.add(new Leg("leg", "нога", 33f, 1.25f, 1f, 800, true));
	parts.add(new Leg("bionic_leg", "бионическая нога", 25f, 1.75f, 1.75f,
		7500, false));
	parts.add(new Leg("advanced_bionic_leg", "усиленная бионическая нога",
		21f, 2.5f, 2.5f, 14000, false));

	parts.add(new Lungs("lungs", "легкие", 1f, 1f, 7000, true));
	parts.add(new Kidneys("kidneys", "почки", 1f, 1f, 5000, true));
	parts.add(new Heart("heart", "сердце", 1f, 1f, 8000, true));

	parts.add(new Brain("brain", "мозг", 1f, 1f, 14000, true));
	parts.add(new Brain("bionic_brain", "бионический мозг", 2.2f, 2.2f,
		8000, false));
	parts.add(new Brain("advanced_bionic_brain",
		"усиленный бионический мозг", 3.2f, 3.2f, 15000, false));
    }

    public static Part getSimple(Type type) {
	return get(type.name());
    }

    public static Part get(String name) {
	for (Part p : parts) {
	    String sa[] = p.getInInventoryPicName().split("/");
	    String s = sa[sa.length - 1].split("\\.")[0];
	    if (s.equalsIgnoreCase(name))
		return p.deepCopy();
	}
	Log.e("Can't find " + name);
	return null;
    }
}