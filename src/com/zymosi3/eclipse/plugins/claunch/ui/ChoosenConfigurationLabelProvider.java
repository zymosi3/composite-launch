package com.zymosi3.eclipse.plugins.claunch.ui;

public class ChoosenConfigurationLabelProvider extends LaunchConfigurationLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof LaunchConfigurationElement) {
            LaunchConfigurationElement configurationElement = (LaunchConfigurationElement) element;
            if (columnIndex == 0) {
                return configurationElement.getConfiguration().getName();
            }
        }
        return String.valueOf(element);
    }

}
