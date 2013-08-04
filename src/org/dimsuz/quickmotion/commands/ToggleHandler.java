package org.dimsuz.quickmotion.commands;

import java.util.Map;

import org.dimsuz.quickmotion.QuickMotionPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

public class ToggleHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		QuickMotionPlugin.toggleInjectionEnabled();
        ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        service.refreshElements(event.getCommand().getId(), null);
		return null;
	}

    @SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
        element.setChecked(QuickMotionPlugin.isInjectionEnabled());
	}

}
