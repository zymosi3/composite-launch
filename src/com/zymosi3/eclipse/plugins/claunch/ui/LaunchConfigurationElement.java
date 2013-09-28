package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;

/**
 * Element of Launch Configuration Viewer.
 */
public class LaunchConfigurationElement {
    
    private ILaunchConfigurationType type;
    private ILaunchConfiguration configuration;
    private String[] modes;
    
    public LaunchConfigurationElement(ILaunchConfigurationType type, ILaunchConfiguration configuration, String[] modes) {
        this.type = type;
        this.configuration = configuration;
        this.modes = modes;
    }
    
    public ILaunchConfigurationType getType() {
        return type;
    }
    
    public ILaunchConfiguration getConfiguration() {
        return configuration;
    }
    
    public String[] getModes() {
        return modes;
    }
}
