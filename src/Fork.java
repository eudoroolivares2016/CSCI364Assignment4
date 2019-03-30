//Eudoro Olivares 2/13/2018
//package com.google.philosophers;
public class Fork {
	private boolean inUse = false;
	
	public Fork() {
	}
	
	public boolean getUseStatus() {
		return inUse;
	}
	
	public void grab() {
		inUse = true;
	}
	
	public void drop() {
		inUse = false;
	}
}
