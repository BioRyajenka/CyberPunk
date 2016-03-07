package com.jackson.myengine;

import java.io.PrintStream;

public class Log {
	private static String getCallerClassName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(Log.class.getName()) && ste.getClassName()
					.indexOf("java.lang.Thread") != 0) {
				return ste.getFileName();
			}
		}
		return null;
	}

	private static int getCallerLineNumber() {
		return Thread.currentThread().getStackTrace()[4].getLineNumber();
	}

	private static String getCallerInfo() {
		return "(" + getCallerClassName() + ":" + getCallerLineNumber() + ")";
	}

	public static void d(String s) {
		System.out.print(getCallerInfo() + " D: " + s + "\n");
		System.out.flush();
	}

	public static void e(String s) {
		System.err.print(getCallerInfo() + ": " + s + "\n");
		System.err.flush();
	}

	public static void w(String s) {
		System.out.print(getCallerInfo() + " W: " + s + "\n");
		System.out.flush();
	}

	public static void printStackTrace() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stackTraceElements) {
			if (ste.getClassName().equals(Log.class.getName()) || ste.getClassName()
					.indexOf("java.lang.Thread") == 0) {
				continue;
			}
			System.out.println("\t " + ste);// space is really nessesary
		}
		System.out.flush();
	}
	
	public static void printStackTrace(PrintStream out) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stackTraceElements) {
			if (ste.getClassName().equals(Log.class.getName()) || ste.getClassName()
					.indexOf("java.lang.Thread") == 0) {
				continue;
			}
			out.println("\t " + ste);// space is really nessesary
		}
		out.flush();
	}
}