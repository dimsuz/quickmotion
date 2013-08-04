package org.dimsuz.quickmotion;

import java.lang.reflect.Method;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class QuickMotionPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "quickmotion"; //$NON-NLS-1$

    // The shared instance
    private static QuickMotionPlugin plugin;

    private static boolean isInjectionEnabled;

    /**
     * The constructor
     */
    public QuickMotionPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static QuickMotionPlugin getDefault() {
        return plugin;
    }

    public static void toggleInjectionEnabled() {
        isInjectionEnabled = !isInjectionEnabled;
        System.out.println("QuickMotion "
                + (isInjectionEnabled ? "injected" : "disinjected"));
        if (isInjectionEnabled) {
            injectIntoActiveEditors();
        }
    }

    private static void injectIntoActiveEditors() {
        IWorkbenchWindow[] windows = plugin.getWorkbench()
                .getWorkbenchWindows();
        for (IWorkbenchWindow window : windows) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference ref : page.getEditorReferences()) {
                    IEditorPart part = ref.getEditor(false);
                    if (part != null) {
                        System.out.println(String.format(
                                "injecting into %s (%s)", part.getTitle(), part
                                        .getClass().getName()));
                        if (part instanceof AbstractTextEditor) {
                            AbstractTextEditor editor = (AbstractTextEditor) part;
                            interceptAbstractTextEditor(editor);
                        } else {
                            System.err
                                    .println("error, injection into this part type is not supported YET");
                        }
                    }
                }
            }
        }
    }

    // stolen from Vrapper
    private static void interceptAbstractTextEditor(AbstractTextEditor editor) {
        try {
            Method me = AbstractTextEditor.class
                    .getDeclaredMethod("getSourceViewer");
            me.setAccessible(true);
            Object viewer = me.invoke(editor);
            if (viewer != null) {
                // test for needed interfaces
                ITextViewer textViewer = (ITextViewer) viewer;
                StyledText textWidget = textViewer.getTextWidget();
                if (textWidget != null) {
                    IDocumentProvider dp = editor.getDocumentProvider();
                    IDocument doc = dp.getDocument(editor.getEditorInput());
                    textWidget.addKeyListener(new InjectedKeyListener(
                            textWidget, doc));
                }
            }
        } catch (Exception exception) {
            System.err.println("Exception while injecting AbstractTextEditor");
        }
    }

    public static boolean isInjectionEnabled() {
        return isInjectionEnabled;
    }

}
