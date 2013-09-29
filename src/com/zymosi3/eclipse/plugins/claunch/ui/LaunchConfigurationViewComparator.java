package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

/** 
 * Compares two LaunchConfigurationElements at first by types and then by configuration names.
 */
public class LaunchConfigurationViewComparator extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object o1, Object o2) {
        if (o1 instanceof CLaunchConfigurationElement && o2 instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement e1 = (CLaunchConfigurationElement) o1;
            CLaunchConfigurationElement e2 = (CLaunchConfigurationElement) o2;
            return ! e1.getType().equals(e2.getType()) ? 
                    e1.getType().getName().compareTo(e2.getType().getName()) :
                    e1.getConfiguration().getName().compareTo(e2.getConfiguration().getName());
                    
        }
        return super.compare(viewer, o1, o2);
    }
}
