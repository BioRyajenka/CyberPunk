package com.jackson.myengine;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		return Thread.currentThread().getStackTrace()[5].getLineNumber();
	}

	private static String getCallerInfo() {
		return String.format("(%s:%s)", getCallerClassName(), getCallerLineNumber())
				.toString();
	}

	private static String getTime() {
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
		return ft.format(date);
	}

	private static void print(PrintStream out, String level, String s) {
		out.printf("%s %s %s: %s\n", getTime(), getCallerInfo(), level, s);
		out.flush();
	}

	public static void d(String s) {
		print(System.out, "D", s);
	}

	public static void e(String s) {
		print(System.err, "E", s);
	}

	public static void w(String s) {
		print(System.out, "W", s);
	}

	public static void printStackTrace() {
		printStackTrace(System.err);
	}

	public static void printStackTrace(PrintStream out) {
		print(out, "STACKTRACE", "");

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