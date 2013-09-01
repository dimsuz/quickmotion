package org.dimsuz.quickmotion;

import java.util.List;

import org.dimsuz.quickmotion.MarksEngine.JumpPosition;
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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.AbstractTextEditor;


public class MotionMarksPainter implements IPainter, PaintListener, KeyListener, VerifyListener {
    private StyledText textWidget;
    private boolean isActive;
    private final Color fillColor;
    private final Color lineColor;
    private final Color labelColor;
    private final boolean firstPaint = true;
    private final ITextViewer textViewer;
    private List<JumpPosition> lineMarkers;
    private final AbstractTextEditor textEditor;

    public MotionMarksPainter(ITextViewer viewer, AbstractTextEditor editor) {
        textWidget = viewer.getTextWidget();
        textViewer = viewer;
        textEditor = editor;
        fillColor = new Color(textWidget.getDisplay(), 255, 127, 0);
        lineColor = new Color(textWidget.getDisplay(), 0, 0, 255);
        labelColor = new Color(textWidget.getDisplay(), 0, 100, 100);
        textWidget.addKeyListener(this);
        textWidget.addVerifyListener(this);
    }

    public void initialize() {
        textWidget.redraw();
    }

    @Override
    public void dispose() {
        textWidget.removeKeyListener(this);
        textWidget = null;
        fillColor.dispose();
        lineColor.dispose();
        labelColor.dispose();
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
        lineMarkers = null;
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
                e1.printStackTrace();
            }

            if(line != null && lineInfo != null) {
                lineMarkers = MarksEngine.getMarkPositions(line);
                int lineHeight = textWidget.getLineHeight(offset);
                e.gc.setBackground(fillColor);
                e.gc.setLineStyle(SWT.LINE_SOLID);
                e.gc.setLineWidth(2);
                for (JumpPosition cpos : lineMarkers) {
                    Point p = textWidget.getLocationAtOffset(lineInfo.getOffset() + cpos.pos);
                    //e.gc.fillRectangle(p.x, p.y, pn.x - p.x, lineHeight);
                    e.gc.setForeground(lineColor);
                    e.gc.drawLine(p.x, p.y-3, p.x, p.y+lineHeight+3);
                    e.gc.setForeground(labelColor);
                    e.gc.drawText(cpos.label, p.x, p.y);
                }
            }
        }
    }

    @Override
    public void setPositionManager(IPaintPositionManager manager) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JumpPosition marker;
        if(lineMarkers != null && (marker = findMarker(e.character, lineMarkers)) != null) {
            IRegion lineInfo = getLineInfo(getModelCaret());
            if(lineInfo != null) {
                int jumpOffset = lineInfo.getOffset() + marker.pos;
                ISelectionProvider selProvider = textEditor.getSelectionProvider();
                selProvider.setSelection(new TextSelection(jumpOffset, 0));
            }
            deactivate(true);
        } else {
            // no activator key pressed, no marker jump issued => nothing to do
            deactivate(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("pressed "+e.character);
        if (e.keyCode == SWT.CTRL) {
            // TODO activate only if pressed and released at same offset!
            activate();
        }
    }

    private static @Nullable JumpPosition findMarker(char c, List<JumpPosition> markers) {
        for (JumpPosition jumpPosition : markers) {
            if(jumpPosition.label.equals(String.valueOf(c))) {
                return jumpPosition;
            }
        }
        return null;
    }

    private static @Nullable JumpPosition findMarker(String s, List<JumpPosition> markers) {
        for (JumpPosition jumpPosition : markers) {
            if(jumpPosition.label.equals(s)) {
                return jumpPosition;
            }
        }
        return null;
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

    @Override
    public void verifyText(VerifyEvent e) {
        System.out.println("veryfing "+e.text);
        if(lineMarkers != null && (findMarker(e.text, lineMarkers)) != null) {
            e.doit = false;
        } else {
            e.doit = true;
        }
    }
}
