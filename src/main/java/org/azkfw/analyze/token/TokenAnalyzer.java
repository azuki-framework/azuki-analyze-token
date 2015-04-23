package org.azkfw.analyze.token;

/**
 * このインターフェースは、トークン解析機能を定義するためのインターフェースです。
 * 
 * @author Kawakicchi
 */
public interface TokenAnalyzer {

	/**
	 * 解析を行う。
	 * 
	 * @param string 文字列
	 */
	public void analyze(final String string);
}
