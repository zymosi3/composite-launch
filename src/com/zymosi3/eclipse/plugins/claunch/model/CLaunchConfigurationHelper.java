package com.zymosi3.eclipse.plugins.claunch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

public class CLaunchConfigurationHelper {
    
    private static final String LAUNCH_CONFIGURATION_COUNT = "launch_configuration_count";
    private static final String LAUNCH_CONFIGURATION_NAME = "launch_configuration_name";
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
            workingCopy.setAttribute(LAUNCH_CONFIGURATION_ENABLED + i, element.isEnabled());
        }
    }
    
    /**
     * Reads elements from configuration.
     * @param configuration Configuration to read.
     * @return Read elements.
     * @throws CoreException Unexpected error.
     */
    public static List<CLaunchConfigurationElement> readElements(ILaunchConfiguration compositeConfiguration) throws CoreException {
        List<CLaunchConfigurationElement> elements = new ArrayList<>();
        int count;
        count = compositeConfiguration.getAttribute(LAUNCH_CONFIGURATION_COUNT, 0);
        if (count > 0) {
            Map<String, ILaunchConfiguration> configurationsMap = getConfigurationsMap(getAllConfigurations(compositeConfiguration));
            for (int i = 0; i < count; i++) {
                String configurationName = compositeConfiguration.getAttribute(LAUNCH_CONFIGURATION_NAME + i, (String) null);
                if (configurationName != null) {
                    ILaunchConfiguration configuration = configurationsMap.get(configurationName);
                    if (configuration != null) {
                        ILaunchConfigurationType type = configuration.getType();
                        CLaunchConfigurationElement element = new CLaunchConfigurationElement(
                                type,
                                configuration,
                                getLaunchConfigurationModes(type)
                        );
                        element.setEnabled(compositeConfiguration.getAttribute(LAUNCH_CONFIGURATION_ENABLED, false));
                        elements.add(element);
                    }
                }
            }
        }
        return elements;
    }
    
    /**
     * Gets all launch configurations.
     * @param exclude Launch configuration to exclude.
     * @return Launch configurations.
     * @throws RuntimeException Unexpected error.
     */
    public static List<ILaunchConfiguration> getAllConfigurations(ILaunchConfiguration exclude) throws CoreException {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        List<ILaunchConfigurationType> allTypes = Arrays.asList(launchManager.getLaunchConfigurationTypes());
        List<ILaunchConfiguration> configurations = new ArrayList<>();
        for (ILaunchConfigurationType type : allTypes) {
            if (type.isPublic()) {
                ILaunchConfiguration[] typeConfigurations = launchManager.getLaunchConfigurations(type);
                for (ILaunchConfiguration configuration : typeConfigurations) {
                    if (exclude == null || ! configuration.getName().equals(exclude.getName())) {
                        configurations.add(configuration);
                    }
                }
            }
        }
        return configurations;
    }
    
    /**
     * Gets launch configuration modes by configuration type.
     * @param type The configuration type.
     * @return Launch configuration modes
     */
    @SuppressWarnings({ "unchecked" })
    public static List<String> getLaunchConfigurationModes(ILaunchConfigurationType type) {
        Set<Set<?>> modesCombinations = type.getSupportedModeCombinations();
        Set<String> modes = new HashSet<>();
        for (Set<?> modeCombination : modesCombinations) {
            for (Object mode : modeCombination) {
                modes.add(String.valueOf(mode));
            }
        }
        return new ArrayList<>(modes);
    }
    
    /**
     * Checks if configuration is composite configuration and contains itself.
     * @param configuration The composite configuration.
     * @return true if composite configuration contains loop false otherwise.
     * @throws CoreException Unexpected error.
     */
    public static boolean containsLoop(ILaunchConfiguration configuration) throws CoreException {
        return contains(configuration, configuration);
    }
    
    private static boolean contains(ILaunchConfiguration configuration, ILaunchConfiguration compositeConfiguration) throws CoreException {
        if (configuration != null && compositeConfiguration != null) {
            List<CLaunchConfigurationElement> elements = readElements(configuration);
            for (CLaunchConfigurationElement element : elements) {
                if ((element != null && compositeConfiguration.getName().equals(element.getConfiguration().getName())) || 
                        contains(element.getConfiguration(), compositeConfiguration)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static Map<String, ILaunchConfiguration> getConfigurationsMap(List<ILaunchConfiguration> configurations) {
        Map<String, ILaunchConfiguration> map = new HashMap<>(configurations.size());
        for (ILaunchConfiguration configuration : configurations) {
            map.put(configuration.getName(), configuration);
        }
        return map;
    }
}
