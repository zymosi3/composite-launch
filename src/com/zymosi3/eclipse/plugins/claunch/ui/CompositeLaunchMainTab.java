package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchViewerComparator;

public class CompositeLaunchMainTab extends AbstractLaunchConfigurationTab {

    private CheckboxTreeViewer launchConfViewer;
    private CheckboxTreeViewer choosenConfViewer;
    private Button addButton;
    private Button addAllButton;
    private Button upButton;
    private Button downButton;
    private Button removeButton;
    private Button removeAllButton;
    
    private List<?> launchConfInput = new ArrayList<>();

    @Override
    public void createControl(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 3;
        mainComposite.setLayout(mainLayout);
        // composite.setFont(parent.getFont());
        setControl(mainComposite);

        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
        IBaseLabelProvider launchLabelProvider = new DecoratingLabelProvider(
                DebugUITools.newDebugModelPresentation(), 
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()
        );
        launchConfViewer = new CheckboxTreeViewer(mainComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
        launchConfViewer.getTree().setLayoutData(fillBothGridData);
        launchConfViewer.setContentProvider(new LaunchConfigurationContentProvider());
        launchConfViewer.setLabelProvider(launchLabelProvider);
        launchConfViewer.setComparator(new WorkbenchViewerComparator());
        launchConfViewer.setInput(launchConfInput);
        
        Composite buttonsComposite = new Composite(mainComposite, SWT.NONE);
        GridData buttonsGridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        buttonsComposite.setLayoutData(buttonsGridData);
        GridLayout buttonsLayout = new GridLayout();
        buttonsLayout.numColumns = 1;
        buttonsComposite.setLayout(buttonsLayout);

        choosenConfViewer = new CheckboxTreeViewer(mainComposite);
        choosenConfViewer.getTree().setLayoutData(fillBothGridData);
        choosenConfViewer.setLabelProvider(launchLabelProvider);
        
        GridData buttonGridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        buttonGridData.heightHint = 26;
        buttonGridData.minimumHeight = 26;
        buttonGridData.widthHint = 74;
        buttonGridData.minimumHeight = 74;
        addButton = new Button(buttonsComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.setLayoutData(buttonGridData);
        addAllButton = new Button(buttonsComposite, SWT.PUSH);
        addAllButton.setText("AddAll");
        addAllButton.setLayoutData(buttonGridData);
        Label separator = new Label(buttonsComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        upButton = new Button(buttonsComposite, SWT.PUSH);
        upButton.setText("Up");
        upButton.setLayoutData(buttonGridData);
        downButton = new Button(buttonsComposite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(buttonGridData);
        removeButton = new Button(buttonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(buttonGridData);
        removeAllButton = new Button(buttonsComposite, SWT.PUSH);
        removeAllButton.setText("RemoveAll");
        removeAllButton.setLayoutData(buttonGridData);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchConfInput = Arrays.asList(launchManager.getLaunchConfigurationTypes());
        if (launchConfViewer != null) {
            launchConfViewer.setInput(launchConfInput);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        return "Main";
    }

}
