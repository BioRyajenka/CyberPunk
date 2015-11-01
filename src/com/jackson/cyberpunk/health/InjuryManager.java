package com.jackson.cyberpunk.health;

import java.util.LinkedList;

import com.jackson.myengine.Log;

public class InjuryManager {
    // TODO: см. линал
    // царапина, порез, пулевое ранение, трещина, раздробленность, удар, синяк
    public static enum InjuryType {
	SCRATCH, CUT, SHOT, CRACK, CRUSH, BRUISE, BURN, BITE
    };

    private static LinkedList<InjuryDesc> injuryDescs;

    public static void init() {
	injuryDescs = new LinkedList<InjuryDesc>();
	injuryDescs.add(new InjuryDesc(InjuryType.SCRATCH, "царапина",
		"перевязанная", 5, 3, 100, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CUT, "порез", "обработанный",
		20, 6, 200, true, "отрублено"));
	injuryDescs.add(new InjuryDesc(InjuryType.SHOT, "пулевое ранение",
		"обработанное", 35, 5, 400, true, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CRACK, "трещина",
		"перебинтованная", 60, .3f, 3000, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CRUSH, "раздробленность",
		"залеченная", 80, .3f, 6000, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.BRUISE, "синяк",
		"перевязанная", 10, .3f, 70, true, null));
	injuryDescs.add(new InjuryDesc(InjuryType.BURN, "ожог", "обработанный",
		10, .3f, 300, true, "сожжено"));
	injuryDescs.add(new InjuryDesc(InjuryType.BITE, "укус", "обработанный",
		20, .3f, 300, false, "откушено"));
    }

    public static InjuryDesc get(InjuryType type) {
	for (InjuryDesc id : injuryDescs)
	    if (id.getType() == type)
		return id;
	Log.e("InjuryManager.java: " + type + " is not find");
	return null;
    }

    public static class InjuryDesc {
	protected InjuryType type;
	protected String name, treatedDesc, fatalDesc;
	protected float pain, bleeding;
	protected boolean isCombines;
	protected int timeToNaturallyHeals;

	public InjuryDesc(InjuryType type, String name, String treatedDesc,
		float pain, float bleeding, int timeToNaturallyHeals,
		boolean isCombines, String fatalDesc) {
	    this.type = type;
	    this.name = name;
	    this.treatedDesc = treatedDesc;
	    this.pain = pain;
	    this.bleeding = bleeding;
	    this.isCombines = isCombines;
	    this.fatalDesc = fatalDesc;
	    this.timeToNaturallyHeals = timeToNaturallyHeals;
	}

	public InjuryType getType() {
	    return type;
	}
	
	public String getName() {
	    return name;
	}

	public boolean isFatal() {
	    return fatalDesc != null;
	}
    }
}