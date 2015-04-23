package org.azkfw.analyze.token;

import org.azkfw.util.StringUtility;

public class BasicTokenAnalyzer extends AbstractTokenAnalyzer {

	private static final int PHASE_NONE = 0;
	private static final int PHASE_STRING = 1;
	private static final int PHASE_MULTI_COMMENT = 2;
	private static final int PHASE_LINE_COMMENT = 3;

	@Override
	protected final void doAnalyze(final String string) {
		StringBuilder buffer = new StringBuilder();

		String matchString = null;

		int phase = PHASE_NONE;
		int lastIndex = 0;
		for (int i = 0; i < string.length(); i++) {

			switch (phase) {

			case PHASE_STRING: {
				int sz = 0;

				sz = isStringEscape(i, string, matchString);
				if (0 != sz) {
					buffer.append(string.substring(i, i + sz));
					
					i += sz - 1;
					continue;
				}

				sz = isStringSuffix(i, string, matchString);
				if (0 != sz) {
					buffer.append(string.substring(i, i + sz));
					addToken(lastIndex, buffer.toString());
					
					phase = PHASE_NONE;
					
					buffer = new StringBuilder();
					lastIndex = i + sz;
					
					i += sz - 1;
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}

			case PHASE_MULTI_COMMENT: {
				int sz = 0;

				sz = isMultiCommentSuffix(i, string, matchString);
				if (0 != sz) {
					buffer.append(string.substring(i, i + sz));
					addToken(lastIndex, buffer.toString());
					
					phase = PHASE_NONE;
					
					buffer = new StringBuilder();
					lastIndex = i + sz;
					
					i += sz - 1;
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}

			case PHASE_LINE_COMMENT: {
				int sz = 0;

				sz = isLineCommentSuffix(i, string, matchString);
				if (0 != sz) {
					addToken(lastIndex, buffer.toString());
					
					phase = PHASE_NONE;

					buffer = new StringBuilder();
					lastIndex = 
					
					i += sz -1;
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}

			default: {
				int sz = 0;

				sz = isMultiCommentPrefix(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString());
						buffer = new StringBuilder();
					}
					lastIndex = i;
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					i += sz - 1;
					phase = PHASE_MULTI_COMMENT;
					continue;
				}

				sz = isLineCommentPrefix(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString());
						buffer = new StringBuilder();
					}
					lastIndex = i;
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					i += sz - 1;
					phase = PHASE_LINE_COMMENT;
					continue;
				}

				sz = isStringPrefix(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString());
						buffer = new StringBuilder();
					}
					
					lastIndex = i;
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					phase = PHASE_STRING;
					
					i += sz - 1;
					continue;
				}

				sz = isReserved(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString());
						buffer = new StringBuilder();
					}
					
					lastIndex = i;
					buffer.append(string.substring(i, i + sz));
					addToken(lastIndex, buffer.toString());
					
					buffer = new StringBuilder();
					lastIndex = i + sz;
					
					i += sz - 1;
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}
			}

		}
		if (0 < buffer.length()) {
			addToken(lastIndex, buffer.toString());
		}

	}

	private void addToken(final int index, final String token) {
		Token t = new Token(index, token);
		
		System.out.println(String.format("[%3d:%3d] %s", index, token.length(), token ));
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
		return isIndexMatch(string, index, "//");
	}

	protected int isLineCommentSuffix(final int index, final String string, final String match) {
		int sz = isIndexMatch(string, index, "\n");
		if (0 == sz) {
			sz = isIndexMatch(string, index, "\r");
		}
		return sz;
	}

	protected int isMultiCommentPrefix(final int index, final String string) {
		return isIndexMatch(string, index, "/*");
	}

	protected int isMultiCommentSuffix(final int index, final String string, final String match) {
		if ("/*".equals(match)) {
			return isIndexMatch(string, index, "*/");
		}
		return 0;
	}

	protected int isStringPrefix(final int index, final String string) {
		return isIndexMatch(string, index, "\"");
	}

	protected int isStringSuffix(final int index, final String string, final String match) {
		if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\"");
		}
		return 0;
	}

	protected int isStringEscape(final int index, final String string, final String match) {
		if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\\\"");
		}
		return 0;
	}

	/**
	 * 
	 * @param src
	 * @param index
	 * @param format
	 * @return ヒットしない場合は、0
	 */
	protected final int isIndexMatch(final String src, final int index, final String format) {
		int result = 0;
		if (StringUtility.isNotEmpty(format)) {
			int size = format.length();
			if (src.length() - index >= size) {
				boolean m = true;
				for (int i = 0; i < size; i++) {
					if (format.charAt(i) != src.charAt(index + i)) {
						m = false;
						break;
					}
				}
				if (m) {
					result = size;
				}
			}
		}
		return result;
	}

}
