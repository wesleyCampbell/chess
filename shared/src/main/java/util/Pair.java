package util;

/**
 * A class that stores two related objects together
 */
public class Pair<typeFirst, typeSecond> {
	//
	// ================= MEMBER METHODS ===============
	//
	
	typeFirst first;
	typeSecond second;
	
	//
	// ================= CONSTRUCTORS ===============
	//
	
	/**
	 * Constructor that takes two objects and will link them together
	 */
	public Pair(typeFirst first, typeSecond second) {
		this.first = first;
		this.second = second;
	}

	//
	// ================= MEMBER METHODS ===============
	//
	
	public typeFirst getFirst() {
		return this.first;
	}

	public typeSecond getSecond() {
		return this.second;
	}
}
