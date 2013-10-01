package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWTException;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;
import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationHelper;

/**
 * Provides launch configurations for tree viewer.
 */
public class LaunchConfigurationContentProvider implements ITreeContentProvider {
    
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

    @Override
    public void dispose() {}

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            List<?> configurations = (List<?>) inputElement;
            List<CLaunchConfigurationElement> elements = new ArrayList<>(); 
            for (Object o : configurations) {
                if (o instanceof ILaunchConfiguration) {
                    ILaunchConfiguration configuration = (ILaunchConfiguration) o;
                    ILaunchConfigurationType type;
                    try {
                        type = configuration.getType();
                    } catch (CoreException e) {
                        throw new SWTException(String.format(
                                Messages.LaunchConfigurationContentProvider_0,
                                configuration.getName(),
                                e.getLocalizedMessage()
                        ));
                    }
                    elements.add(new CLaunchConfigurationElement (
                            type,
                            configuration,
                            CLaunchConfigurationHelper.getLaunchConfigurationModes(type)
                    ));
                }
            }
            return elements.toArray();
        }
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return false;
    }
}
