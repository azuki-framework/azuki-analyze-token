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
package org.azkfw.analyze.sql;

import org.azkfw.analyze.BasicTokenAnalyzer;

public class SQLTokenAnalyzer extends BasicTokenAnalyzer {

	@Override
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

	@Override
	protected int isReserved(final int index, final String string) {
		final String[] strings = { ".", ",", "(", ")", "=", "<", ">", "+", "-", "*", "/", "|", ":", "{", "}" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 < sz) {
				return sz;
			}
		}
		return 0;
	}

	@Override
	protected Integer isLineCommentPrefix(final int index, final String string) {
		int sz = isIndexMatch(string, index, "--");
		if (0 != sz) {
			return Integer.valueOf(sz);
		}
		return null;
	}

	@Override
	protected Integer isStringPrefix(final int index, final String string) {
		final String[] strings = { "'", "\"" };
		for (String match : strings) {
			int sz = isIndexMatch(string, index, match);
			if (0 != sz) {
				return Integer.valueOf(sz);
			}
		}
		return null;
	}

	@Override
	protected int isStringSuffix(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isIndexMatch(string, index, "'");
		} else if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\"");
		}
		return 0;
	}

	@Override
	protected int isStringEscape(final int index, final String string, final String match) {
		if ("'".equals(match)) {
			return isIndexMatch(string, index, "''");
		} else if ("\"".equals(match)) {
			return isIndexMatch(string, index, "\"\"");
		}
		return 0;
	}

}
