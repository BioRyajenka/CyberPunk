package com.jackson.myengine;

public class Log {
    public static void d(String s) {
	System.out.print("D: " + s + "\n");
    }

    public static void e(String s) {
	System.out.print("E: " + s + "\n");
    }

    public static void w(String s) {
	System.out.print("W: " + s + "\n");
    }
}