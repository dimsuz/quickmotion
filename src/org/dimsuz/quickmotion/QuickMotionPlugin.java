package org.dimsuz.quickmotion;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class QuickMotionPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "quickmotion"; //$NON-NLS-1$

	// The shared instance
	private static QuickMotionPlugin plugin;

	private static  boolean isInjectionEnabled;
	
	/**
	 * The constructor
	 */
	public QuickMotionPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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
		System.out.println("QuickMotion "+(isInjectionEnabled ? "injected" : "disinjected"));
	}

	public static boolean isInjectionEnabled() {
		return isInjectionEnabled;
	}

}
