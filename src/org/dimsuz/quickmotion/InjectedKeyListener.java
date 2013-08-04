package org.dimsuz.quickmotion;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class InjectedKeyListener implements KeyListener {

	private final StyledText editorWidget;
	private final IDocument document;

	public InjectedKeyListener(@NonNull StyledText editorWidget, IDocument doc) {
		this.editorWidget = editorWidget;
		this.document = doc;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		try {
			int caretOffset = editorWidget.getCaretOffset();
			int line = document.getLineOfOffset(caretOffset);
			int charPos = caretOffset - document.getLineOffset(line);
			System.out.println("key pressed at line " + line + ", char " + charPos);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
