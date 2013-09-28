package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (inputElement instanceof List) {
            List<?> allTypes = (List<?>) inputElement;
            List<LaunchConfigurationElement> elements = new ArrayList<>(); 
            for (Object typeObj : allTypes) {
                if (typeObj instanceof ILaunchConfigurationType) {
                    ILaunchConfigurationType type = (ILaunchConfigurationType) typeObj;
                    if (type.isPublic()) {
                        ILaunchConfiguration[] configurations = getConfigurations(type);
                        for (ILaunchConfiguration configuration : configurations) {
                            elements.add(new LaunchConfigurationElement (
                                    type,
                                    configuration,
                                    getModes(type)
                            ));
                        }
                    }
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
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    @Override
    public boolean hasChildren(Object element) {
        return false;
    }

    private ILaunchConfiguration[] getConfigurations(ILaunchConfigurationType type) {
        try {
            return launchManager.getLaunchConfigurations(type);
        } catch (CoreException e) {
            throw new SWTException(String.format(
                    "Failed to get launch configurations with type %s. Message: \"%s\"",
                    type.getName(),
                    e.getMessage()
            ));
        }
    }
    
    @SuppressWarnings({ "unchecked" })
    private String[] getModes(ILaunchConfigurationType type) {
        Set<Set<?>> modesCombinations = type.getSupportedModeCombinations();
        Set<String> modes = new HashSet<>();
        for (Set<?> modeCombination : modesCombinations) {
            for (Object mode : modeCombination) {
                modes.add(String.valueOf(mode));
            }
        }
        return modes.toArray(new String[modes.size()]);
    }
}
