package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationFilteredTree;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationTreeContentProvider;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
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
        mainLayout.numColumns = 2;
        mainComposite.setLayout(mainLayout);
        // composite.setFont(parent.getFont());
        setControl(mainComposite);

        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite leftComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout leftLayout = new GridLayout();
        leftLayout.numColumns = 1;
        leftComposite.setLayout(leftLayout);
        leftComposite.setLayoutData(fillBothGridData);
        Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightComposite.setLayout(rightLayout);
        rightComposite.setLayoutData(fillBothGridData);

        IBaseLabelProvider launchLabelProvider = new DecoratingLabelProvider(
                DebugUITools.newDebugModelPresentation(), 
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()
        );
        launchConfViewer = new CheckboxTreeViewer(leftComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
        launchConfViewer.getTree().setLayoutData(fillBothGridData);
        launchConfViewer.setContentProvider(new LaunchConfigurationTreeContentProvider(null, parent.getShell()));
        launchConfViewer.setLabelProvider(launchLabelProvider);
        launchConfViewer.setComparator(new WorkbenchViewerComparator());
        launchConfViewer.setInput(launchConfInput);
        choosenConfViewer = new CheckboxTreeViewer(rightComposite);
        choosenConfViewer.getTree().setLayoutData(fillBothGridData);
        choosenConfViewer.setLabelProvider(launchLabelProvider);
        
        Composite leftButtonsComposite = new Composite(leftComposite, SWT.NONE);
        RowLayout leftButtonsLayout = new RowLayout();
        leftButtonsLayout.type = SWT.HORIZONTAL;
        leftButtonsComposite.setLayout(leftButtonsLayout);
        Composite rightButtonsComposite = new Composite(rightComposite, SWT.NONE);
        RowLayout rightButtonsLayout = new RowLayout();
        rightButtonsLayout.type = SWT.HORIZONTAL;
        rightButtonsComposite.setLayout(leftButtonsLayout);
        
        RowData buttonRowData = new RowData(74, 26);
        addButton = new Button(leftButtonsComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.setLayoutData(buttonRowData);
        addAllButton = new Button(leftButtonsComposite, SWT.PUSH);
        addAllButton.setText("AddAll");
        addAllButton.setLayoutData(buttonRowData);
        upButton = new Button(rightButtonsComposite, SWT.PUSH);
        upButton.setText("Up");
        upButton.setLayoutData(buttonRowData);
        downButton = new Button(rightButtonsComposite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(buttonRowData);
        removeButton = new Button(rightButtonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(buttonRowData);
        removeAllButton = new Button(rightButtonsComposite, SWT.PUSH);
        removeAllButton.setText("RemoveAll");
        removeAllButton.setLayoutData(buttonRowData);
        
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
