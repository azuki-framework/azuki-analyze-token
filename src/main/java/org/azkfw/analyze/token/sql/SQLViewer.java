package org.azkfw.analyze.token.sql;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.azkfw.analyze.token.CommentToken;
import org.azkfw.analyze.token.SpaceToken;
import org.azkfw.analyze.token.StringToken;
import org.azkfw.analyze.token.Token;
import org.azkfw.analyze.token.TokenAnalyzer;

public class SQLViewer {

	public static void main(final String[] args) {

		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File(args[0])), "Windows-31J");
			char[] buffer = new char[1024];
			StringBuilder s = new StringBuilder();
			int readSize = -1;
			while (-1 != (readSize = reader.read(buffer, 0, 1024))) {
				s.append(buffer, 0, readSize);
			}

			TokenAnalyzer a = new SQLTokenAnalyzer();
			a.analyze(s.toString());

			SQLFrame frm = new SQLFrame();
			frm.set(s.toString(), a.getTokenList());
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
		private DefaultStyledDocument style;

		private Set<String> keywords;
		private Set<String> functions;

		public SQLFrame() {
			setTitle("");
			setLayout(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			keywords = new HashSet<String>();
			keywords.add("SELECT");
			keywords.add("FROM");
			keywords.add("WHERE");
			keywords.add("ORDER");
			keywords.add("GROUP");
			keywords.add("BY");
			keywords.add("AS");
			keywords.add("AND");
			keywords.add("OR");
			keywords.add("IS");
			keywords.add("LIKE");
			keywords.add("SYSDATE");
			keywords.add("EXISTS");
			keywords.add("DUAL");
			keywords.add("HAVING");
			keywords.add("NULL");

			functions = new HashSet<String>();
			functions.add("MIN");
			functions.add("MAX");
			functions.add("NVL");
			functions.add("TO_CHAR");
			functions.add("TO_DATE");
			functions.add("DECODE");
			functions.add("RTRIM");
			functions.add("IN");

			text = new JTextPane() {
				/** serialVersionUID */
				private static final long serialVersionUID = -6718078218905107604L;

				@Override
				public boolean getScrollableTracksViewportWidth() {
					Object parent = getParent();
					if (parent instanceof JViewport) {
						JViewport port = (JViewport) parent;
						int w = port.getWidth();
						TextUI ui = getUI();
						Dimension sz = ui.getPreferredSize(this);
						if (sz.width < w) {
							return true;
						}
					}
					return false;
				}
			};

			style = new DefaultStyledDocument();
			text.setStyledDocument(style);
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

			setBounds(10, 10, 800, 800);
		}

		public void set(final String sql, final List<Token> tokens) {
			text.setText(sql);

			MutableAttributeSet atrKeyword = new SimpleAttributeSet();
			StyleConstants.setForeground(atrKeyword, Color.BLUE);
			StyleConstants.setBold(atrKeyword, true);

			MutableAttributeSet atrFunction = new SimpleAttributeSet();
			StyleConstants.setForeground(atrFunction, new Color(100, 0, 100));
			StyleConstants.setBold(atrFunction, true);

			MutableAttributeSet atrComment = new SimpleAttributeSet();
			StyleConstants.setForeground(atrComment, new Color(0, 100, 0));

			MutableAttributeSet atrString = new SimpleAttributeSet();
			StyleConstants.setForeground(atrString, new Color(200, 0, 0));

			for (Token token : tokens) {
				if (!(token instanceof SpaceToken)) {
					System.out.println(String.format("[%3d:%3d] %s", token.getIndex(), token.getToken().length(), token.getToken()));
				}

				if (isKeyword(token)) {
					style.setCharacterAttributes(token.getIndex(), token.getToken().length(), atrKeyword, true);
				} else if (isFunction(token)) {
					style.setCharacterAttributes(token.getIndex(), token.getToken().length(), atrFunction, true);
				} else if (isComment(token)) {
					style.setCharacterAttributes(token.getIndex(), token.getToken().length(), atrComment, true);
				} else if (isString(token)) {
					style.setCharacterAttributes(token.getIndex(), token.getToken().length(), atrString, true);
				}
			}

		}

		private boolean isString(final Token token) {
			return (token instanceof StringToken);
		}

		private boolean isComment(final Token token) {
			return (token instanceof CommentToken);
		}

		private boolean isKeyword(final Token token) {
			String value = token.getToken().toUpperCase();
			return keywords.contains(value);
		}

		private boolean isFunction(final Token token) {
			String value = token.getToken().toUpperCase();
			return functions.contains(value);
		}
	}
}
