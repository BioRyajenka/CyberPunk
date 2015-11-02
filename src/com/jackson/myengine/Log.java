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

	public static void printStackTrace() {
		System.out.println("trace");
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : stackTraceElements) {
			String className = element.getClassName();
			String methodName = element.getMethodName();
			System.out.println(className + ":" + methodName);
		}
	}
}