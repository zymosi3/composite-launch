package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWTException;

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
        ILaunchConfigurationType[] allTypes = launchManager.getLaunchConfigurationTypes();
        List<ILaunchConfigurationType> withChildren = new ArrayList<>();
        for (ILaunchConfigurationType type : allTypes) {
            if (hasChildren(type)) {
                withChildren.add(type);
            }
        }
        return withChildren.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ILaunchConfiguration) {
            return new Object[0];
        } else if (parentElement instanceof ILaunchConfigurationType) {
            ILaunchConfigurationType type = (ILaunchConfigurationType) parentElement;
            try {
                return launchManager.getLaunchConfigurations(type);
            } catch (CoreException e) {
                throw new SWTException("Filed to get launch configurations with type " + type.getName() +
                        ". Message: \"" + e.getMessage() + "\"");
            }
        } else {
            return launchManager.getLaunchConfigurationTypes();
        }
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ILaunchConfiguration) {
            ILaunchConfiguration launchConfiguration = (ILaunchConfiguration) element;
            if (! launchConfiguration.exists()) {
                return null;
            }
            try {
                return launchConfiguration.getType();
            } catch (CoreException e) {
                throw new SWTException("Filed to get type of launch configuration with name" + launchConfiguration.getName() +
                        ". Message: \"" + e.getMessage() + "\""); 
            }
        } else if (element instanceof ILaunchConfigurationType) {
            return ResourcesPlugin.getWorkspace().getRoot();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

}
