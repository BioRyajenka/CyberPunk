package com.jackson.cyberpunk.item;

public class KnapsackFactory{
	public static enum Type {SIMPLE, INFINITY};
	
	public static Knapsack create(Type type){
		switch (type){
		case SIMPLE:
			return new Knapsack("simple_knapsack", "Простой дешевый рюкзак", 1.7f, 
					2, 2, 80, 4 * 5);
		case INFINITY:
			return new Knapsack("simple_knapsack", "Без описания", 0, 
					0, 0, 0, 8 * 5);
		default:
			return null;
		}
	}
}