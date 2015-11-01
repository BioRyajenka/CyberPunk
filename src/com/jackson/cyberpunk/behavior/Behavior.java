package com.jackson.cyberpunk.behavior;

import com.jackson.cyberpunk.mob.NPC;

public abstract class Behavior {
    protected Behavior() { }
    
    public abstract void doLogic(NPC handler);
    
    //public abstract static Behavior getInstance(); //can't make it abstract
}