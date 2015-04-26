/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.analyze;

import org.azkfw.analyze.token.CommentToken;
import org.azkfw.analyze.token.ReservedToken;
import org.azkfw.analyze.token.SpaceToken;
import org.azkfw.analyze.token.StringToken;
import org.azkfw.analyze.token.Token;
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
					addToken(lastIndex, buffer.toString(), phase);

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
				Integer sz = isMultiCommentSuffix(i, string, matchString);
				if (null != sz) { // コメント(Multi)終了を検出
					buffer.append(string.substring(i, i + sz));
					addToken(lastIndex, buffer.toString(), phase); // コメント(Multi)をトークン格納
					buffer = new StringBuilder();

					lastIndex = i + sz; // コメント(Multi)終了インデックス格納
					i = lastIndex - 1;

					phase = PHASE_NONE;
					continue;
				}
				buffer.append(string.charAt(i));
				break;
			}
			case PHASE_LINE_COMMENT: {
				Integer sz = isLineCommentSuffix(i, string, matchString);
				if (null != sz) { // コメント(Line)終了を検出
					buffer.append(string.substring(i, i + sz));
					addToken(lastIndex, buffer.toString(), phase);
					buffer = new StringBuilder();

					lastIndex = i + sz; // コメント(Line)終了インデックス格納
					i = lastIndex - 1;

					phase = PHASE_NONE;
					continue;
				}
				buffer.append(string.charAt(i));
				break;
			}

			default: {
				Integer sz = null;

				sz = isMultiCommentPrefix(i, string);
				if (null != sz) { // コメント(Multi)開始を検出
					if (0 < buffer.length()) { // 直前のバッファをトークン格納
						addToken(lastIndex, buffer.toString(), phase);
						buffer = new StringBuilder();
					}
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);

					lastIndex = i; // コメント(Multi)開始インデックス格納
					i += sz - 1;

					phase = PHASE_MULTI_COMMENT;
					continue;
				}

				sz = isLineCommentPrefix(i, string);
				if (null != sz) { // コメント(Line)開始を検出
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString(), phase);
						buffer = new StringBuilder();
					}
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);

					lastIndex = i; // コメント(Line)開始インデックス格納
					i += sz - 1;

					phase = PHASE_LINE_COMMENT;
					continue;
				}

				sz = isStringPrefix(i, string);
				if (null != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString(), phase);
						buffer = new StringBuilder();
					}
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);

					lastIndex = i; // 文字列開始インデックス格納
					i += sz - 1;

					phase = PHASE_STRING;
					continue;
				}

				sz = isSpace(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString(), phase);
						buffer = new StringBuilder();
					}
					buffer.append(string.substring(i, i + sz));
					addToken(new SpaceToken(i, buffer.toString()));
					buffer = new StringBuilder();

					lastIndex = i + sz;
					i += sz - 1;
					continue;
				}

				sz = isReserved(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(lastIndex, buffer.toString(), phase);
						buffer = new StringBuilder();
					}
					buffer.append(string.substring(i, i + sz));
					addToken(new ReservedToken(i, buffer.toString()));
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
			addToken(lastIndex, buffer.toString(), phase);
		}

	}

	private void addToken(final int index, final String value, final int phase) {
		switch (phase) {
		case PHASE_STRING:
			addToken(new StringToken(index, value));
			break;
		case PHASE_LINE_COMMENT:
		case PHASE_MULTI_COMMENT:
			addToken(new CommentToken(index, value));
			break;
		default:
			addToken(new Token(index, value));
			break;
		}
	}

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
		final String[] strings = { ".", ",", "(", ")", "=", "<", ">" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	protected Integer isLineCommentPrefix(final int index, final String string) {
		int sz = isIndexMatch(string, index, "//");
		if (0 != sz) {
			return Integer.valueOf(sz);
		}
		return null;
	}

	/**
	 * 
	 * @param index
	 * @param string
	 * @param match
	 * @return インデックスから終了位置までのサイズ。<code>null</code>終了位置がない
	 */
	protected Integer isLineCommentSuffix(final int index, final String string, final String match) {
		Integer result = null;
		int sz = isIndexMatch(string, index, "\n");
		if (0 != sz) {
			result = Integer.valueOf(0);
		} else {
			sz = isIndexMatch(string, index, "\r");
			if (0 != sz) {
				result = Integer.valueOf(0);
			}
		}
		return result;
	}

	protected Integer isMultiCommentPrefix(final int index, final String string) {
		int sz = isIndexMatch(string, index, "/*");
		if (0 != sz) {
			return Integer.valueOf(sz);
		}
		return null;
	}

	protected Integer isMultiCommentSuffix(final int index, final String string, final String match) {
		if ("/*".equals(match)) {
			int sz = isIndexMatch(string, index, "*/");
			if (0 != sz) {
				return Integer.valueOf(sz);
			}
		}
		return null;
	}

	protected Integer isStringPrefix(final int index, final String string) {
		int sz = isIndexMatch(string, index, "\"");
		if (0 != sz) {
			return Integer.valueOf(sz);
		}
		return null;
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
