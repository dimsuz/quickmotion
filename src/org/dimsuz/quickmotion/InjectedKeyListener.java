package org.dimsuz.quickmotion;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class InjectedKeyListener implements KeyListener {

    private final StyledText editorWidget;
    private final IDocument document;
    private StyleRange[] savedRanges;

    public InjectedKeyListener(@NonNull StyledText editorWidget, IDocument doc) {
        this.editorWidget = editorWidget;
        this.document = doc;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            try {
                int caretOffset = editorWidget.getCaretOffset();
                int lineLength = document.getLineInformationOfOffset(caretOffset).getLength();
                StyleRange range = new StyleRange();
                range.start = caretOffset + 4;
                range.length = 4;
                range.strikeout = true;
                savedRanges = editorWidget.getStyleRanges(range.start, range.length);
                editorWidget.setStyleRange(range);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            if(savedRanges != null) {
                editorWidget.setStyleRanges(savedRanges);
                savedRanges = null;
            }
        }
    }

}
