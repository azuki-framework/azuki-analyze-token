package org.azkfw.analyze.token.sql;

import org.azkfw.analyze.token.BasicTokenAnalyzer;

public class SQLTokenAnalyzer extends BasicTokenAnalyzer {

	protected int isSpace(final int index, final String string) {
		final String[] strings = { "\r", "\n", " ", "\t" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	protected int isReserved(final int index, final String string) {
		final String[] strings = { ".", ",", "(", ")", "=", "<", ">", "+", "-", "*", "/", "|", ":" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	protected int isLineCommentPrefix(final int index, final String string) {
		return isIndexMatch(string, index, "--");
	}

	protected int isStringPrefix(final int index, final String string) {
		final String[] strings = { "'", "\"" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	protected int isStringSuffix(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isIndexMatch(string, index, "'");
		} else if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\"");
		}
		return 0;
	}

	protected int isStringEscape(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isIndexMatch(string, index, "''");
		} else if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\"\"");
		}
		return 0;
	}

}
