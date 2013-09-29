package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWTException;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

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
                                "Failed to get launch configuration type with name %s. Message: \"%s\"",
                                configuration.getName(),
                                e.getMessage()
                        ));
                    }
                    elements.add(new CLaunchConfigurationElement (
                            type,
                            configuration,
                            getModes(type)
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

    @SuppressWarnings({ "unchecked" })
    private static List<String> getModes(ILaunchConfigurationType type) {
        Set<Set<?>> modesCombinations = type.getSupportedModeCombinations();
        Set<String> modes = new HashSet<>();
        for (Set<?> modeCombination : modesCombinations) {
            for (Object mode : modeCombination) {
                modes.add(String.valueOf(mode));
            }
        }
        return new ArrayList<>(modes);
    }
}
