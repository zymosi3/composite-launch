package com.zymosi3.eclipse.plugins.claunch.ui;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

/**
 * Label provider launch configurations tree.
 */
public class ViewConfigurationLabelProvider extends LaunchConfigurationLabelProvider {
    
    private static final int NAME_COLUMN_IDX = 0;
    private static final int TYPE_COLUMN_IDX = 1;
    private static final int MODES_COLUMN_IDX = 2;

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement configurationElement = (CLaunchConfigurationElement) element;
            if (columnIndex == NAME_COLUMN_IDX) {
                return configurationElement.getConfiguration().getName();
            }
            if (columnIndex == TYPE_COLUMN_IDX) {
                return configurationElement.getType().getName();
            }
            if (columnIndex == MODES_COLUMN_IDX) {
                StringBuilder sb = new StringBuilder();
                String delimiter = ", "; //$NON-NLS-1$
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

    @Override
    protected int nameColumnIndex() {
        return NAME_COLUMN_IDX;
    }

}
