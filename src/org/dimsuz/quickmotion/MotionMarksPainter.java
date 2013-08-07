package org.dimsuz.quickmotion;

import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;


public class MotionMarksPainter implements IPainter, PaintListener, KeyListener {
    private StyledText textWidget;
    private boolean isActive;
    private final Color color;
    private final boolean firstPaint = true;

    public MotionMarksPainter(ITextViewer viewer) {
        textWidget = viewer.getTextWidget();
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
            Rectangle area= textWidget.getClientArea();
            e.gc.setForeground(color);
            e.gc.setLineStyle(SWT.LINE_DASH);
            e.gc.setLineWidth(2);
            e.gc.drawLine(30, 0, 30, area.height);
        }
    }

    @Override
    public void setPositionManager(IPaintPositionManager manager) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            activate();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ALT) {
            deactivate(true);
        }
    }

}
