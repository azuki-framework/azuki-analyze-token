package org.azkfw.analyze.token;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SQLTokenAnalyzer extends BasicTokenAnalyzer {

	public static void main(final String[] args) {

		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File("src/test/resources/sample01.sql")), "UTF-8");
			char[] buffer = new char[1024];
			StringBuilder s = new StringBuilder();
			int readSize = -1;
			while (-1 != (readSize = reader.read(buffer, 0, 1024))) {
				s.append(buffer, 0, readSize);
			}

			TokenAnalyzer a = new SQLTokenAnalyzer();
			a.analyze(s.toString());

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	protected int isReserved(final int index, final String string) {
		final String[] strings = { "\r", "\n", " ", ".", ",", "(", ")", "=", "<", ">" };
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
	/*
	 * @Override protected boolean isStringCharacter(final char c) { return ((c
	 * == '\"') || (c == '\'')); }
	 * 
	 * @Override protected boolean isEscapeCharacter(final char c, final char
	 * type) { if ('\"' == type) { return (c == '\\'); } else if ('\'' == type)
	 * { return (c == '\''); } return false; }
	 * 
	 * @Override protected boolean isReserveCharacter(final char c) { return
	 * (('\r' == c) || ('\n' == c) || ('\t' == c) || (' ' == c) || (',' == c) ||
	 * ('(' == c) || (')' == c) || ('=' == c) || ('<' == c) || ('>' == c) ||
	 * ('&' == c) || ('|' == c) || (':' == c) || ('.' == c)); }
	 * 
	 * @Override protected String getLineCommentPrefix() { return "--"; }
	 */
}
