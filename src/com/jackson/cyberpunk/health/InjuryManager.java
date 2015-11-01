package com.jackson.cyberpunk.health;

import java.util.LinkedList;

import com.jackson.myengine.Log;

public class InjuryManager {
    // TODO: ��. �����
    // ��������, �����, ������� �������, �������, ���������������, ����, �����
    public static enum InjuryType {
	SCRATCH, CUT, SHOT, CRACK, CRUSH, BRUISE, BURN, BITE
    };

    private static LinkedList<InjuryDesc> injuryDescs;

    public static void init() {
	injuryDescs = new LinkedList<InjuryDesc>();
	injuryDescs.add(new InjuryDesc(InjuryType.SCRATCH, "��������",
		"������������", 5, 3, 100, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CUT, "�����", "������������",
		20, 6, 200, true, "���������"));
	injuryDescs.add(new InjuryDesc(InjuryType.SHOT, "������� �������",
		"������������", 35, 5, 400, true, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CRACK, "�������",
		"���������������", 60, .3f, 3000, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.CRUSH, "���������������",
		"����������", 80, .3f, 6000, false, null));
	injuryDescs.add(new InjuryDesc(InjuryType.BRUISE, "�����",
		"������������", 10, .3f, 70, true, null));
	injuryDescs.add(new InjuryDesc(InjuryType.BURN, "����", "������������",
		10, .3f, 300, true, "�������"));
	injuryDescs.add(new InjuryDesc(InjuryType.BITE, "����", "������������",
		20, .3f, 300, false, "��������"));
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