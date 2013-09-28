package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class CompositeLaunchMainTab extends AbstractLaunchConfigurationTab {

    private TreeViewer launchConfViewer;
    private CheckboxTreeViewer choosenConfViewer;
    private Button addButton;
    private Button addAllButton;
    private Button upButton;
    private Button downButton;
    private Button removeButton;
    private Button removeAllButton;
    
    private List<LaunchConfigurationElement> choosenConfInput = new ArrayList<>();

    @Override
    public void createControl(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 2;
        mainComposite.setLayout(mainLayout);
        mainComposite.setFont(parent.getFont());
        setControl(mainComposite);

        initLaunchConfViewer(mainComposite);
        
        initTopButtons(mainComposite);
        
        initChoosenConfViewer(mainComposite);
        
        initBottomButtons(mainComposite);
    }

    private void initLaunchConfViewer(Composite parent) {
        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
        launchConfViewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        launchConfViewer.setContentProvider(new LaunchConfigurationContentProvider());
        launchConfViewer.setLabelProvider(new ViewConfigurationLabelProvider());
        launchConfViewer.setComparator(new LaunchConfigurationViewComparator());
        launchConfViewer.setInput(Collections.emptyList());
        
        Tree launchConfViewerTree = launchConfViewer.getTree();
        launchConfViewerTree.setLayoutData(fillBothGridData);
        launchConfViewerTree.setHeaderVisible(true);
        launchConfViewerTree.setFont(parent.getFont());
        TreeColumn nameColumn = new TreeColumn(launchConfViewerTree, SWT.LEFT);
        nameColumn.setText("Name");
        nameColumn.setWidth(200);
        TreeColumn typeColumn = new TreeColumn(launchConfViewerTree, SWT.LEFT);
        typeColumn.setText("Type");
        typeColumn.setWidth(150);
        TreeColumn modesColumn = new TreeColumn(launchConfViewerTree, SWT.LEFT);
        modesColumn.setText("Modes");
        modesColumn.setWidth(150);
        
        launchConfViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                if (! selection.isEmpty()) {
                    Object selectedObject = selection.getFirstElement();
                    if (selectedObject instanceof LaunchConfigurationElement) {
                        choosenConfInput.add((LaunchConfigurationElement) selectedObject);
                        choosenConfViewer.setInput(choosenConfInput);
                    }
                }
            }
        });
    }
    
    private void initTopButtons(Composite parent) {
        Composite topButtonsComposite = new Composite(parent, SWT.NONE);
        GridData buttonsGridData = new GridData(GridData.END, GridData.BEGINNING, false, false);
        topButtonsComposite.setLayoutData(buttonsGridData);
        GridLayout topButtonsLayout = new GridLayout();
        topButtonsLayout.numColumns = 1;
        topButtonsComposite.setLayout(topButtonsLayout);
        
        GridData buttonGridData = new GridData(GridData.END, GridData.BEGINNING, false, false);
        buttonGridData.heightHint = 26;
        buttonGridData.minimumHeight = 26;
        buttonGridData.widthHint = 74;
        buttonGridData.minimumHeight = 74;
        addButton = new Button(topButtonsComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.setLayoutData(buttonGridData);
        addButton.setEnabled(false);
        addAllButton = new Button(topButtonsComposite, SWT.PUSH);
        addAllButton.setText("AddAll");
        addAllButton.setLayoutData(buttonGridData);
        addAllButton.setEnabled(false);
    }
    
    private void initBottomButtons(Composite parent) {
        Composite bottomButtonsComposite = new Composite(parent, SWT.NONE);
        GridData buttonsGridData = new GridData(GridData.END, GridData.BEGINNING, false, false);
        bottomButtonsComposite.setLayoutData(buttonsGridData);
        GridLayout bottomButtonsLayout = new GridLayout();
        bottomButtonsLayout.numColumns = 1;
        bottomButtonsComposite.setLayout(bottomButtonsLayout);
        
        GridData buttonGridData = new GridData(GridData.END, GridData.BEGINNING, false, false);
        upButton = new Button(bottomButtonsComposite, SWT.PUSH);
        upButton.setText("Up");
        upButton.setLayoutData(buttonGridData);
        downButton = new Button(bottomButtonsComposite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(buttonGridData);
        removeButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(buttonGridData);
        removeAllButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeAllButton.setText("RemoveAll");
        removeAllButton.setLayoutData(buttonGridData);
    }
    
    private void initChoosenConfViewer(Composite parent) {
        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
        choosenConfViewer = new CheckboxTreeViewer(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        choosenConfViewer.setContentProvider(new ChoosenConfigurationContentProvider());
        choosenConfViewer.setLabelProvider(new ChoosenConfigurationLabelProvider());
        choosenConfViewer.setInput(choosenConfInput);
        
        Tree choosenConfViewerTree = choosenConfViewer.getTree();
        choosenConfViewerTree.setLayoutData(fillBothGridData);
        choosenConfViewerTree.setHeaderVisible(false);
        choosenConfViewerTree.setFont(parent.getFont());
        TreeColumn nameColumn = new TreeColumn(choosenConfViewerTree, SWT.LEFT);
        nameColumn.setText("Name");
        nameColumn.setWidth(200);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {}

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        List<ILaunchConfigurationType> allTypes = Arrays.asList(launchManager.getLaunchConfigurationTypes());
        if (launchConfViewer != null) {
            launchConfViewer.setInput(allTypes);
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
