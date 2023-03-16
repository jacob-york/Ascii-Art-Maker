package com.example.util;

public class Timer {
	  // A simple "stopwatch" class
	
	private long startTime, endTime;
	  
	public void start() {
		startTime = System.nanoTime();
	}
	
	public void stop() {
		endTime = System.nanoTime();
	}
	public double getTime() {
		return (double) ((endTime - startTime) / 1000000);
	}
	
}
