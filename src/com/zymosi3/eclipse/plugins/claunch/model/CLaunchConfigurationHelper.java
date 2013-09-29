package com.zymosi3.eclipse.plugins.claunch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.SWTException;

public class CLaunchConfigurationHelper {
    
    private static ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

    private static final String LAUNCH_CONFIGURATION_COUNT = "launch_configuration_count";
    private static final String LAUNCH_CONFIGURATION_NAME = "launch_configuration_name";
    private static final String LAUNCH_CONFIGURATION_MODES = "launch_configuration_modes";
    private static final String LAUNCH_CONFIGURATION_ENABLED = "launch_configuration_enabled";
    
    /**
     * Writes elements to working copy.
     * @param elements Elements to write.
     * @param workingCopy Configuration working copy.
     */
    public static void writeElements(List<CLaunchConfigurationElement> elements, ILaunchConfigurationWorkingCopy workingCopy) {
        workingCopy.setAttribute(LAUNCH_CONFIGURATION_COUNT, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            CLaunchConfigurationElement element = elements.get(i);
            workingCopy.setAttribute(LAUNCH_CONFIGURATION_NAME + i, element.getConfiguration().getName());
            workingCopy.setAttribute(LAUNCH_CONFIGURATION_MODES + i, element.getModes());
            workingCopy.setAttribute(LAUNCH_CONFIGURATION_ENABLED + i, element.isEnabled());
        }
    }
    
    /**
     * Reads elements from configuration.
     * @param configuration Configuration to read.
     * @return Read elements.
     */
    public static List<CLaunchConfigurationElement> readElements(ILaunchConfiguration configuration) {
        List<CLaunchConfigurationElement> elements = new ArrayList<>();
        return elements;
    }
    
    public static ILaunchConfiguration[] getConfigurations(ILaunchConfigurationType type) {
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
    
    public static List<ILaunchConfiguration> getAllConfigurations(ILaunchConfiguration exclude) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        List<ILaunchConfigurationType> allTypes = Arrays.asList(launchManager.getLaunchConfigurationTypes());
        List<ILaunchConfiguration> configurations = new ArrayList<>();
        for (ILaunchConfigurationType type : allTypes) {
            if (type.isPublic()) {
                ILaunchConfiguration[] typeConfigurations = getConfigurations(type);
                for (ILaunchConfiguration configuration : typeConfigurations) {
                    if (! configuration.getName().equals(exclude.getName())) {
                        configurations.add(configuration);
                    }
                }
            }
        }
        return configurations;
    }
}
