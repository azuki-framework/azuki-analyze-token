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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Kawakicchi
 *
 */
public class FormatFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 4996992556242862226L;

	public static void main(final String[] args) {
		FormatFrame frame = new FormatFrame();
		frame.setVisible(true);
	}

	private JScrollPane srlInput;
	private JTextArea txtInput;
	private JScrollPane srlFormat;
	private SQLTextPane txtFormat;

	public FormatFrame() {
		setTitle("Formater");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		add(splitpane);

		txtInput = new JTextArea();
		txtFormat = new SQLTextPane();

		srlInput = new JScrollPane(txtInput);
		srlFormat = new JScrollPane(txtFormat);

		splitpane.setLeftComponent(srlInput);
		splitpane.setRightComponent(srlFormat);
		splitpane.setDividerLocation(500);

		txtInput.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.println("changedUpdate");
			}
		});

		setBounds(0, 0, 1024, 800);
		
		txtInput.setText(read( Paths.get("src", "test", "resources", "select01.sql").toFile() ));
	}
	
	private void change() {
		String src = txtInput.getText();
		txtFormat.setText(src);
	}

	private String read(final File file) {
		StringBuilder sql = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			char buf[] = new char[1024];
			int size = -1;
			while (-1 != (size = reader.read(buf, 0, 1024))) {
				sql.append(buf, 0, size);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sql.toString();
	}
}
