package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
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
        ILaunchConfigurationType[] allTypes = launchManager.getLaunchConfigurationTypes();
        List<Object[]> elements = new ArrayList<>(); 
        for (ILaunchConfigurationType type : allTypes) {
            if (type.isPublic()) {
                ILaunchConfiguration[] configurations = getConfigurations(type);
                for (ILaunchConfiguration configuration : configurations) {
                    elements.add(new Object[] {
                            configuration,
                            type.getName(),
                            getModes(type)
                    });
                }
            }
        }
        return elements.toArray();
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
                    "Filed to get launch configurations with type %s. Message: \"%s\"",
                    type.getName(),
                    e.getMessage()
            ));
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String getModes(ILaunchConfigurationType type) {
        StringBuilder sb = new StringBuilder();
        Set<Set> modesCombinations = type.getSupportedModeCombinations();
        String delimiter = ", ";
        for (Set modeCombination : modesCombinations) {
            for (Object mode : modeCombination) {
                sb.append(mode);
                sb.append(delimiter);
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - delimiter.length(), sb.length());
        }
        return sb.toString();
    }
}
