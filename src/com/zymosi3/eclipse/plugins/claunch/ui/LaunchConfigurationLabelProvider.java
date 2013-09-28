package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public abstract class LaunchConfigurationLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof LaunchConfigurationElement) {
            LaunchConfigurationElement configurationElement = (LaunchConfigurationElement) element;
            if (columnIndex == 0) {
                return DebugPluginImages.getImage(configurationElement.getType().getIdentifier());
            }
        }
        return null;
    }
}
