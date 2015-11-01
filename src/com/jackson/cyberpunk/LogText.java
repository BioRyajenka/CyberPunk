package com.jackson.cyberpunk;

import java.util.LinkedList;

public class LogText{
	private static final int MAX_ROWS = 64;
	private static LinkedList<String> log = new LinkedList<String>();
	private static LogTextView view;
	
	public static void init(){
		for (int i = 0; i < 5; i++)
			log.add("");
	}
	
	public static void add(String text){
		if (log.size() > MAX_ROWS * 2){
			LinkedList<String> tlog = new LinkedList<String>();
			for (int i = log.size() / 2; i < log.size(); i++)
				tlog.add(log.get(i));
			log = tlog;
		}
		
		log.add(text);
		getView().update();
	}
	
	public static LinkedList<String> getLog(){
		return log;
	}
	
	public static LogTextView getView(){
		if (view == null) view = new LogTextView();
		return view;
	}
}