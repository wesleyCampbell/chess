package util;

/**
 * A class that stores two related objects together
 */
public class Pair<A, B> {
	//
	// ================= MEMBER METHODS ===============
	//
	
	A first;
	B second;
	
	//
	// ================= CONSTRUCTORS ===============
	//
	
	/**
	 * Constructor that takes two objects and will link them together
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	//
	// ================= MEMBER METHODS ===============
	//
	
	public A getFirst() {
		return this.first;
	}

	public B getSecond() {
		return this.second;
	}
}
