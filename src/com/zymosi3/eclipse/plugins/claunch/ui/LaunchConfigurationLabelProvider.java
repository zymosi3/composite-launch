package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class LaunchConfigurationLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == 0) {
            if (element instanceof Object[]) {
                Object[] values = (Object[]) element;
                if (values.length > 0 && values[0] instanceof ILaunchConfiguration) {
                    ILaunchConfiguration configuration = (ILaunchConfiguration) values[0];
                    try {
                        return DebugPluginImages.getImage(configuration.getType().getIdentifier());
                    } catch (CoreException e) {
                        return null;
                    
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof Object[]) {
            Object[] values = (Object[]) element;
            if (columnIndex == 0) {
                if (values.length > 0 && values[0] instanceof ILaunchConfiguration) {
                    ILaunchConfiguration configuration = (ILaunchConfiguration) values[0];
                    return configuration.getName();
                }
            
            }
            if (values.length >= columnIndex) {
                return String.valueOf(values[columnIndex]);
            }
        }
        return String.valueOf(element);
    }

}
