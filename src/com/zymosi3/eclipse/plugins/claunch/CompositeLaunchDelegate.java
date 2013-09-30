package com.zymosi3.eclipse.plugins.claunch;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;
import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationHelper;

@SuppressWarnings("restriction")
public class CompositeLaunchDelegate implements ILaunchConfigurationDelegate {
    
    private static final int MONITOR_TICKS = 100;
    private static final int WAIT_GRANULARITY = 100;

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask(String.format("Composite launch %s", configuration.getName()), MONITOR_TICKS);
        try {
            List<CLaunchConfigurationElement> elements = CLaunchConfigurationHelper.readElements(configuration);
            ILaunch previousSubLaunch = null; 
            for (CLaunchConfigurationElement element : elements) {
                ILaunchConfiguration subConfiguration = element.getConfiguration();
                if (! monitor.isCanceled() && element.isEnabled() && checkConfiguration(subConfiguration, mode)) {
                    delay(element, previousSubLaunch, monitor);
                    ILaunch subLaunch = DebugUIPlugin.buildAndLaunch(
                            element.getConfiguration(), 
                            mode, 
                            new SubProgressMonitor(monitor, MONITOR_TICKS / elements.size())
                    );
                    previousSubLaunch = subLaunch;
                }
            }
        } finally {
            monitor.done();
        }
    }
    
    private void delay(CLaunchConfigurationElement element, ILaunch previousSubLaunch, IProgressMonitor monitor) {
        if (element.isWaitPrevious() && previousSubLaunch != null) {
            while (! (monitor.isCanceled() || previousSubLaunch.isTerminated())) {
                try {
                    Thread.sleep(WAIT_GRANULARITY);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        if (monitor.isCanceled()) {
            return;
        }
        if (element.getDelay() > 0) {
            try {
                Thread.sleep(element.getDelay());
            } catch (InterruptedException ignored) {}
        }
    }

    private static boolean checkConfiguration(ILaunchConfiguration configuration, String mode) throws CoreException {
        boolean isValid = true;
        if (! configuration.supportsMode(mode)) {
            isValid = false;
            showError(String.format(
                    "Launch configuration %s doesn't support run mode %s", 
                    String.valueOf(configuration),
                    mode
            ));
        }
        if (! CLaunchConfigurationHelper.configurationExists(configuration)) {
            isValid = false;
            showError(String.format(
                    "Launch configuration %s doesn't exist", 
                    String.valueOf(configuration)
            ));
        }
        return isValid;
    }
    
    private static void showError(final String message) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Composite Launch",  
                        message
                );
                
            }
        });
    }
}
