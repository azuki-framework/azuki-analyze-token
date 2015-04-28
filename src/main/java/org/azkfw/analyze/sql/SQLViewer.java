package org.azkfw.analyze.sql;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class SQLViewer {

	public static void main(final String[] args) {

		InputStreamReader reader = null;
		try {
			// reader = new InputStreamReader(new FileInputStream(new
			// File(args[0])), "Windows-31J");
			reader = new InputStreamReader(new FileInputStream(new File(args[0])), "UTF-8");
			char[] buffer = new char[1024];
			StringBuilder s = new StringBuilder();
			int readSize = -1;
			while (-1 != (readSize = reader.read(buffer, 0, 1024))) {
				s.append(buffer, 0, readSize);
			}

			SQLFrame frm = new SQLFrame();
			frm.set(s.toString());
			frm.setVisible(true);

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

	private static class SQLFrame extends JFrame {

		/** serialVersionUID */
		private static final long serialVersionUID = -8065958340097754665L;

		private JScrollPane scroll;
		private JTextPane text;

		public SQLFrame() {
			setTitle("");
			setLayout(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			text = new SQLTextPane();
			text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			scroll = new JScrollPane(text);

			add(scroll);

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent arg0) {
					Insets insets = getInsets();
					int width = getWidth() - (insets.left + insets.right);
					int height = getHeight() - (insets.top + insets.bottom);
					scroll.setBounds(0, 0, width, height);
				}
			});

			setBounds(10, 10, 500, 500);
		}

		public void set(final String sql) {
			text.setText(sql);
		}

	}
}
