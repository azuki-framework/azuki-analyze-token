package org.azkfw.analyze.token;

public class Token {

	private int index;

	private String token;

	public Token(final int index, final String token) {
		this.index = index;
		this.token = token;
	}

	public int getIndex() {
		return index;
	}

	public String getToken() {
		return token;
	}
}
