package com.zymosi3.eclipse.plugins.claunch.ui;

public class ViewConfigurationLabelProvider extends LaunchConfigurationLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof LaunchConfigurationElement) {
            LaunchConfigurationElement configurationElement = (LaunchConfigurationElement) element;
            if (columnIndex == 0) {
                return configurationElement.getConfiguration().getName();
            }
            if (columnIndex == 1) {
                return configurationElement.getType().getName();
            }
            if (columnIndex == 2) {
                StringBuilder sb = new StringBuilder();
                String delimiter = ", ";
                for (String mode : configurationElement.getModes()) {
                    sb.append(mode).append(delimiter);
                }
                if (sb.length() > 0) {
                    sb.delete(sb.length() - delimiter.length(), sb.length());
                }
                return sb.toString();
            }
        }
        return String.valueOf(element);
    }

}
