package com.zymosi3.eclipse.plugins.claunch.ui;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

/**
 * Label provider for chosen elements.
 */
public class ChosenConfigurationLabelProvider extends LaunchConfigurationLabelProvider {
    
    private static final int NAME_COLUMN_IDX = 0;

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement configurationElement = (CLaunchConfigurationElement) element;
            if (columnIndex == NAME_COLUMN_IDX) {
                return configurationElement.getConfiguration().getName();
            }
        }
        return String.valueOf(element);
    }

    @Override
    protected int nameColumnIndex() {
        return NAME_COLUMN_IDX;
    }

}
