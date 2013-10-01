package com.zymosi3.eclipse.plugins.claunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;
import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationHelper;

public class CompositeLaunchDelegate implements ILaunchConfigurationDelegate {

    private static final int WAIT_GRANULARITY = 100;
    
    private static IPreferenceStore preferenceStore;

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        try {
            List<CLaunchConfigurationElement> elements = CLaunchConfigurationHelper.readElements(configuration);
            monitor.beginTask(String.format(Messages.CompositeLaunchDelegate_0, configuration.getName()), elements.size());
            List<ILaunch> subLaunches = new ArrayList<>();
            ILaunch previousSubLaunch = null; 
            for (CLaunchConfigurationElement element : elements) {
                if (checkCanceled(monitor, launch, subLaunches)) {
                    break;
                }
                ILaunchConfiguration subConfiguration = element.getConfiguration();
                if (element.isEnabled() && checkConfiguration(subConfiguration, mode)) {
                    delay(element, previousSubLaunch, monitor);
                    boolean doBuild = getPreferenceStore().getBoolean(IDebugUIConstants.PREF_BUILD_BEFORE_LAUNCH);
                    ILaunch subLaunch = element.getConfiguration().launch(mode, new SubProgressMonitor(monitor, 1), doBuild);
                    previousSubLaunch = subLaunch;
                    subLaunches.add(subLaunch);
                }
            }
        } finally {
            monitor.done();
        }
    }
    
    /**
     * Checks monitor.isCanceled() and terminates launch and all started sublaunches.
     */
    private boolean checkCanceled(IProgressMonitor monitor, ILaunch launch, List<ILaunch> subLaunches) {
        if (monitor.isCanceled() && launch.canTerminate()) {
            for (ILaunch sublaunch : subLaunches) {
                if (sublaunch.canTerminate()) {
                    try {
                        sublaunch.terminate();
                    } catch (DebugException e) {
                        showError(String.format(Messages.CompositeLaunchDelegate_1, sublaunch.getLaunchConfiguration().getName()));
                    }
                }
            }
            try {
                launch.terminate();
            } catch (DebugException e) {
                showError(String.format(Messages.CompositeLaunchDelegate_2, launch.getLaunchConfiguration().getName()));
            }
            return true;
        }
        return false;
    }

    /**
     * Waits for previous launch or does delay if needed. 
     */
    private void delay(CLaunchConfigurationElement element, ILaunch previousSubLaunch, IProgressMonitor monitor) {
        if (element.isWaitPrevious() && previousSubLaunch != null) {
            monitor.subTask(String.format(Messages.CompositeLaunchDelegate_3, previousSubLaunch.getLaunchConfiguration().getName()));
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
            monitor.subTask(String.format(Messages.CompositeLaunchDelegate_4, element.getDelay()));
            try {
                Thread.sleep(element.getDelay());
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Checks if configuration exists and supports current mode.
     */
    private static boolean checkConfiguration(ILaunchConfiguration configuration, String mode) throws CoreException {
        boolean isValid = true;
        if (! configuration.supportsMode(mode)) {
            isValid = false;
            showError(String.format(
                    Messages.CompositeLaunchDelegate_5, 
                    String.valueOf(configuration),
                    mode
            ));
        }
        if (! CLaunchConfigurationHelper.configurationExists(configuration)) {
            isValid = false;
            showError(String.format(
                    Messages.CompositeLaunchDelegate_6, 
                    String.valueOf(configuration)
            ));
        }
        return isValid;
    }
    
    /**
     * Shows error message in dialog box.
     */
    private static void showError(final String message) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.CompositeLaunchDelegate_7,  
                        message
                );
                
            }
        });
    }
    
    private static IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(
                    InstanceScope.INSTANCE, 
                    Activator.getDefault().getBundle().getSymbolicName()
            );
        }
        return preferenceStore;
    }
}
