package com.zymosi3.eclipse.plugins.claunch.ui;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

/**
 * Label provider for chosen elements.
 */
public class ChosenConfigurationLabelProvider extends LaunchConfigurationLabelProvider {
    
    private static final int NAME_COLUMN_IDX = 0;
    private static final int DELAY_COLUMN_IDX = 1;
    private static final int WAIT_PREVIOUS_COLUMN_IDX = 2;

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement configurationElement = (CLaunchConfigurationElement) element;
            if (columnIndex == NAME_COLUMN_IDX) {
                return configurationElement.getConfiguration().getName();
            }
            if (columnIndex == DELAY_COLUMN_IDX) {
                return String.valueOf(configurationElement.getDelay());
            }
            if (columnIndex == WAIT_PREVIOUS_COLUMN_IDX) {
                return configurationElement.isWaitPrevious() ? Messages.ChosenConfigurationLabelProvider_0 : Messages.ChosenConfigurationLabelProvider_1;
            }
        }
        return String.valueOf(element);
    }

    @Override
    protected int nameColumnIndex() {
        return NAME_COLUMN_IDX;
    }

}
