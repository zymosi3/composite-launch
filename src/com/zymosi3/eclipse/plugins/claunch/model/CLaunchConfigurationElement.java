package com.zymosi3.eclipse.plugins.claunch.model;

import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;

/**
 * Element of Launch Configuration Viewer.
 */
public class CLaunchConfigurationElement {
    
    private ILaunchConfigurationType type;
    private ILaunchConfiguration configuration;
    private List<String> modes;
    private boolean enabled;
    private int delayBefore;
    private int delayAfter;
    private boolean waitPrevious;
    
    public CLaunchConfigurationElement(ILaunchConfigurationType type, ILaunchConfiguration configuration, List<String> modes) {
        this.type = type;
        this.configuration = configuration;
        this.modes = modes;
    }
    
    public CLaunchConfigurationElement(CLaunchConfigurationElement element) {
        this(element.type, element.configuration, element.modes);
        this.enabled = element.enabled;
        this.delayBefore = element.delayBefore;
        this.delayAfter = element.delayAfter;
        this.waitPrevious = element.waitPrevious;
    }
    
    public ILaunchConfigurationType getType() {
        return type;
    }
    
    public ILaunchConfiguration getConfiguration() {
        return configuration;
    }
    
    public List<String> getModes() {
        return modes;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDelayBefore() {
        return delayBefore;
    }

    public void setDelayBefore(int delayBefore) {
        this.delayBefore = delayBefore;
    }

    public int getDelayAfter() {
        return delayAfter;
    }

    public void setDelayAfter(int delayAfter) {
        this.delayAfter = delayAfter;
    }

    public boolean isWaitPrevious() {
        return waitPrevious;
    }

    public void setWaitPrevious(boolean wait) {
        this.waitPrevious = wait;
    }
}
