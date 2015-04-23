package org.azkfw.analyze.token;

import org.azkfw.lang.LoggingObject;

/**
 * このクラスは、
 * 
 * @author Kawakicchi
 */
public abstract class AbstractTokenAnalyzer extends LoggingObject implements TokenAnalyzer {

	@Override
	public void analyze(final String string) {
		doAnalyze(string);
	}

	protected abstract void doAnalyze(final String string);

}
