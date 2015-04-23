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
package org.azkfw.analyze.token;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Kawakicchi
 * 
 */
public class TestTokenAnalyzer {

	public static void main(final String[] args) {

		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File("src/test/resources/sample01.sql")), "UTF-8");

			TestTokenAnalyzer a = new TestTokenAnalyzer();
			a.analyze(reader);

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

	public void analyze(final Reader reader) throws IOException {
		char[] buffer = new char[1024];
		StringBuilder s = new StringBuilder();
		int readSize = -1;
		while (-1 != (readSize = reader.read(buffer, 0, 1024))) {
			s.append(buffer, 0, readSize);
		}
		test(s.toString());
	}

	private static final int PHASE_NONE = 0;
	private static final int PHASE_STRING = 1;
	private static final int PHASE_MULTI_COMMENT = 2;
	private static final int PHASE_LINE_COMMENT = 3;

	private void test(final String string) {
		// System.out.println(string);

		StringBuilder buffer = new StringBuilder();

		String matchString = null;

		int phase = PHASE_NONE;
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
					i += sz - 1;
					addToken(buffer.toString());
					phase = PHASE_NONE;
					buffer = new StringBuilder();
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
					i += sz - 1;
					addToken(buffer.toString());
					phase = PHASE_NONE;
					buffer = new StringBuilder();
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}

			case PHASE_LINE_COMMENT: {
				int sz = 0;

				sz = isLineCommentSuffix(i, string, matchString);
				if (0 != sz) {
					i += -1;
					addToken(buffer.toString());
					phase = PHASE_NONE;
					buffer = new StringBuilder();
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
						addToken(buffer.toString());
						buffer = new StringBuilder();
					}
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					i += sz - 1;
					phase = PHASE_MULTI_COMMENT;
					continue;
				}

				sz = isLineCommentPrefix(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(buffer.toString());
						buffer = new StringBuilder();
					}
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					i += sz - 1;
					phase = PHASE_LINE_COMMENT;
					continue;
				}

				sz = isStringPrefix(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(buffer.toString());
						buffer = new StringBuilder();
					}
					i += sz - 1;
					matchString = string.substring(i, i + sz);
					buffer.append(matchString);
					phase = PHASE_STRING;
					continue;
				}

				sz = isReserved(i, string);
				if (0 != sz) {
					if (0 < buffer.length()) {
						addToken(buffer.toString());
						buffer = new StringBuilder();
					}
					buffer.append(string.substring(i, i + sz));
					addToken(buffer.toString());
					buffer = new StringBuilder();
					i += sz - 1;
					continue;
				}

				buffer.append(string.charAt(i));
				break;
			}
			}

		}
		if (0 < buffer.length()) {
			addToken(buffer.toString());
		}

	}

	private void addToken(final String v) {
		System.out.println("===================================");
		System.out.println(v);
	}

	private int isReserved(final int index, final String string) {
		final String[] strings = { "\r", "\n", " ", "." ,",","(",")","=","<",">"};
		for (String match : strings) {
			int sz = isMatch(match, index, string);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	private int isLineCommentPrefix(final int index, final String string) {
		return isMatch("--", index, string);
	}

	private int isLineCommentSuffix(final int index, final String string, final String match) {
		if ("--".equals(match)) {
			return isMatch("\n", index, string);
		}
		return 0;
	}

	private int isMultiCommentPrefix(final int index, final String string) {
		return isMatch("/*", index, string);
	}

	private int isMultiCommentSuffix(final int index, final String string, final String match) {
		if ("/*".equals(match)) {
			return isMatch("*/", index, string);
		}
		return 0;
	}

	private int isStringPrefix(final int index, final String string) {
		return isMatch("'", index, string);
	}

	private int isStringSuffix(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isMatch("'", index, string);
		}
		return 0;
	}

	private int isStringEscape(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isMatch("''", index, string);
		}
		return 0;
	}

	/**
	 * 
	 * @param match
	 * @param index
	 * @param string
	 * @return ヒットしない場合は、0
	 */
	private int isMatch(final String match, final int index, final String string) {
		int result = 0;
		if (0 < match.length() && string.length() - index >= match.length()) {
			boolean m = true;
			for (int i = 0; i < match.length(); i++) {
				if (match.charAt(i) != string.charAt(index + i)) {
					m = false;
					break;
				}
			}
			if (m) {
				result = match.length();
			}
		}
		return result;
	}

}
