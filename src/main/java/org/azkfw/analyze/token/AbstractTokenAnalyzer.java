package org.azkfw.analyze.token;

import java.util.ArrayList;
import java.util.List;

import org.azkfw.lang.LoggingObject;

/**
 * このクラスは、
 * 
 * @author Kawakicchi
 */
public abstract class AbstractTokenAnalyzer extends LoggingObject implements TokenAnalyzer {

	private List<Token> tokenList;

	public AbstractTokenAnalyzer() {
		tokenList = new ArrayList<Token>();
	}

	@Override
	public final void analyze(final String string) {
		doAnalyzeBefore();
		doAnalyze(string);
		doAnalyzeAfter(tokenList);
	}

	@Override
	public final List<Token> getTokenList() {
		return tokenList;
	}

	protected void doAnalyzeBefore() {

	}

	protected void doAnalyzeAfter(final List<Token> tokens) {

	}

	/**
	 * トークンを追加する。
	 * 
	 * @param token トークン
	 */
	protected final void addToken(final Token token) {
		tokenList.add(token);
	}

	/**
	 * 解析を行う。
	 * 
	 * @param string 文字列
	 */
	protected abstract void doAnalyze(final String string);

}
