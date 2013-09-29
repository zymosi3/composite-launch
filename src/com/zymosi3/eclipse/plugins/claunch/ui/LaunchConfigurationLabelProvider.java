package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

/**
 * Abstract label provider for launch configurations.
 */
@SuppressWarnings("restriction")
public abstract class LaunchConfigurationLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement configurationElement = (CLaunchConfigurationElement) element;
            if (columnIndex == nameColumnIndex()) {
                return DebugPluginImages.getImage(configurationElement.getType().getIdentifier());
            }
        }
        return null;
    }
    
    protected abstract int nameColumnIndex();
}
