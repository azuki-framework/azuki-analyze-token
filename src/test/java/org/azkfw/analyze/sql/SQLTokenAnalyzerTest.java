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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

import org.azkfw.analyze.token.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Kawakicchi
 */
@RunWith(JUnit4.class)
public class SQLTokenAnalyzerTest extends TestCase {

	@Test
	public void test() {

		SQLTokenAnalyzer analyzer = new SQLTokenAnalyzer();

		analyzer.analyze(read("/select01.sql"));

		List<Token> tokens = analyzer.getTokenList();

		assertEquals("件数", 293, tokens.size());

		int i = 0;
		for (Token token : tokens) {
			System.out.println(String.format("[%3d] %s", i, token.getToken()));
			i++;
		}

		assertEquals("/*\n * Name: Select 01\n * File: select01.sql\n */", tokens.get(0).getToken());
		assertEquals("SELECT", tokens.get(2).getToken());
		assertEquals("distinct", tokens.get(8).getToken());
		assertEquals("A", tokens.get(14).getToken());
		assertEquals("ID", tokens.get(16).getToken());
		assertEquals("AS", tokens.get(23).getToken());
		assertEquals("ID", tokens.get(25).getToken());
		assertEquals("-- ID", tokens.get(31).getToken());
		assertEquals("A", tokens.get(37).getToken());
		assertEquals("NAME", tokens.get(39).getToken());
		assertEquals("AS", tokens.get(44).getToken());
		assertEquals("NAME", tokens.get(46).getToken());
		assertEquals("-- 名前", tokens.get(50).getToken());
		assertEquals("CASE", tokens.get(56).getToken());
		assertEquals("A", tokens.get(58).getToken());
		assertEquals("SEX", tokens.get(60).getToken());
		assertEquals("WHEN", tokens.get(70).getToken());
		assertEquals("'1'", tokens.get(72).getToken());
		assertEquals("THEN", tokens.get(74).getToken());
		assertEquals("'男'", tokens.get(76).getToken());
		assertEquals("WHEN", tokens.get(86).getToken());
		assertEquals("'2'", tokens.get(88).getToken());
		assertEquals("THEN", tokens.get(90).getToken());
		assertEquals("'女'", tokens.get(92).getToken());
		assertEquals("ELSE", tokens.get(102).getToken());
		assertEquals("'その他'", tokens.get(113).getToken());
	}

	private String read(final String file) {
		StringBuilder s = new StringBuilder();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(getClass().getResourceAsStream(file), "UTF-8");
			char[] buffer = new char[1024];
			int readSize = -1;
			while (-1 != (readSize = reader.read(buffer, 0, 1024))) {
				s.append(buffer, 0, readSize);
			}

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
		return s.toString();
	}
}
