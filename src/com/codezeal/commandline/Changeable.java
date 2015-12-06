package com.codezeal.commandline;

/**
 * Created by Per Malmberg on 2015-11-26.
 */
public class Changeable<T> {
	private T value;

	public Changeable(T value) {
		this.value = value;
	}

	public String toString() {
		return value.toString();
	}

	public boolean equals(Object other) {
		if (other instanceof Changeable) {
			return value.equals(((Changeable) other).value);
		} else {
			return value.equals(other);
		}
	}

	public int hashCode() {
		return value.hashCode();
	}

	public T get() { return value; }

	public void set( T value ) { this.value = value; }
}