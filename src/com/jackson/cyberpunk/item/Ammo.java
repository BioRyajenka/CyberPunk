package com.jackson.cyberpunk.item;

public class Ammo extends CountableItem {
    public static enum Type {GUN, SHOTGUN, SNIPER_RIFLE};
    
    private Type type;
    
    public Ammo(Type type, int amount) {
	super("ammos/" + type.name().toLowerCase(), getName(type), 0.02f, 1, 1, getCost(type), 40);
	setAmount(amount);
	this.type = type;
    }
    
    private static String getName(Type type) {
	switch (type) {
	case GUN:
	    return "������� �������";
	case SHOTGUN:
	    return "������� ��� ���������";
	case SNIPER_RIFLE:
	    return "������� ��� ����������� ��������";
	}
	return "lol";
    }
    
    private static int getCost(Type type) {
	switch (type) {
	case GUN:
	    return 3;
	case SHOTGUN:
	    return 5;
	case SNIPER_RIFLE:
	    return 7;
	}
	return 666;
    }
    
    public Type getType() {
	return type;
    }
}