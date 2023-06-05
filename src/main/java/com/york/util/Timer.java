/**
 * A simple Stopwatch class
 * */

package com.york.util;

public final class Timer {
	
	private long startTime, endTime;
	  
	public void start() {
		startTime = System.nanoTime();
	}
	
	public void stop() {
		endTime = System.nanoTime();
	}

	public double getTime() {
		return (double) ((endTime - startTime) / 1_000_000);
	}
	
}
