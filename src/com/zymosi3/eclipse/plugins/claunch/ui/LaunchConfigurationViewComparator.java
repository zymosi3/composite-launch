package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class LaunchConfigurationViewComparator extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object o1, Object o2) {
        if (o1 instanceof LaunchConfigurationElement && o2 instanceof LaunchConfigurationElement) {
            LaunchConfigurationElement e1 = (LaunchConfigurationElement) o1;
            LaunchConfigurationElement e2 = (LaunchConfigurationElement) o2;
            return ! e1.getType().equals(e2.getType()) ? 
                    e1.getType().getName().compareTo(e2.getType().getName()) :
                    e1.getConfiguration().getName().compareTo(e2.getConfiguration().getName());
                    
        }
        return super.compare(viewer, o1, o2);
    }
}
