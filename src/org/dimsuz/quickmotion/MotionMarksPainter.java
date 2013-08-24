package org.dimsuz.quickmotion;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.AbstractTextEditor;


public class MotionMarksPainter implements IPainter, PaintListener, KeyListener {
    private StyledText textWidget;
    private boolean isActive;
    private final Color color;
    private final boolean firstPaint = true;
    private final ITextViewer textViewer;
    private List<Integer> lineMarkers;
    private final AbstractTextEditor textEditor;

    public MotionMarksPainter(ITextViewer viewer, AbstractTextEditor editor) {
        textWidget = viewer.getTextWidget();
        textViewer = viewer;
        textEditor = editor;
        color = new Color(textWidget.getDisplay(), 255, 127, 0);
        textWidget.addKeyListener(this);
    }

    public void initialize() {
        textWidget.redraw();
    }

    @Override
    public void dispose() {
        textWidget.removeKeyListener(this);
        textWidget = null;
        color.dispose();
    }

    @Override
    public void deactivate(boolean redraw) {
        if(isActive) {
            isActive = false;
            textWidget.removePaintListener(this);
            if(redraw) {
                textWidget.redraw();
            }
        }
    }

    private void activate() {
        if(!isActive) {
            isActive = true;
            textWidget.addPaintListener(this);
            textWidget.redraw();
        }
    }

    @Override
    public void paint(int reason) {
        // activate/deactivate explicitly on keyPress/Release
        // but might need this too if reason == TEXT_CHANGED
        /*
        if(!isActive && !firstPaint) {
            activate();
        } else if(reason == CONFIGURATION || reason == INTERNAL) {
            textWidget.redraw();
        }
        if(firstPaint) {
            firstPaint = false;
        }
        */
    }

    @Override
    public void paintControl(PaintEvent e) {
        if(textWidget != null) {
            int offset = getModelCaret();
            IRegion lineInfo = getLineInfo(offset);
            if(lineInfo == null) {
                return;
            }
            String line = null;
            try {
                line = textViewer.getDocument().get(lineInfo.getOffset(), lineInfo.getLength());
            } catch (BadLocationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(line != null && lineInfo != null) {
                lineMarkers = MarksEngine.getMarkPositions(line);
                int lineHeight = textWidget.getLineHeight(offset);
                e.gc.setForeground(color);
                e.gc.setLineStyle(SWT.LINE_SOLID);
                e.gc.setLineWidth(1);
                for (Integer cpos : lineMarkers) {
                    Point p = textWidget.getLocationAtOffset(lineInfo.getOffset() + cpos);
                    e.gc.drawLine(p.x, p.y, p.x, p.y+lineHeight);
                }
            }
        }
    }

    @Override
    public void setPositionManager(IPaintPositionManager manager) {
    }

    private int tmpCurMarkerIdx = 0;
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            activate();
        } else if(e.character == 'd' && lineMarkers != null) {
            IRegion lineInfo = getLineInfo(getModelCaret());
            if(lineInfo != null) {
                int jumpOffset = lineInfo.getOffset() + lineMarkers.get(tmpCurMarkerIdx);
                ISelectionProvider selProvider = textEditor.getSelectionProvider();
                selProvider.setSelection(new TextSelection(jumpOffset, 0));
                tmpCurMarkerIdx++;
                if(tmpCurMarkerIdx >= lineMarkers.size()) {
                    tmpCurMarkerIdx = 0;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            deactivate(true);
            lineMarkers = null;
        }
    }

    /**
     * Returns the location of the caret as offset in the source viewer's
     * input document.
     *
     * @return the caret location
     */
    private int getModelCaret() {
        int widgetCaret= textWidget.getCaretOffset();
        if (textViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5) textViewer;
            return extension.widgetOffset2ModelOffset(widgetCaret);
        }
        IRegion visible= textViewer.getVisibleRegion();
        return widgetCaret + visible.getOffset();
    }

    @Nullable
    private IRegion getLineInfo(int offset) {
        IDocument document = textViewer.getDocument();
        try {
            IRegion lineInfo = document.getLineInformationOfOffset(offset);
            return lineInfo;
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
