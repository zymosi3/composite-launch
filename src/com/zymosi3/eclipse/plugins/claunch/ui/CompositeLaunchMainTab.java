package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;
import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationHelper;

public class CompositeLaunchMainTab extends AbstractLaunchConfigurationTab {
    
    private TreeViewer launchConfViewer;
    private CheckboxTreeViewer choosenConfViewer;
    private Button addButton;
    private Button addAllButton;
    private Button upButton;
    private Button downButton;
    private Button removeButton;
    private Button removeAllButton;
    
    private String mode;
    
    private List<CLaunchConfigurationElement> choosenConfInput = new ArrayList<>();

    public CompositeLaunchMainTab(String mode) {
        this.mode = mode;
    }

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
        
        setButtonsEnabled();
        setChoosenInput();
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
                addToChoosenInput(selection.getFirstElement());
                setChoosenInput();
                updateLaunchConfigurationDialog();
            }
        });
        launchConfViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setButtonsEnabled();
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
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (launchConfViewer != null) {
                    TreeSelection selection = (TreeSelection) launchConfViewer.getSelection();
                    Iterator<?> iterator = selection.iterator();
                    while (iterator.hasNext()) {
                        addToChoosenInput(iterator.next());
                    }
                    setChoosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        addAllButton = new Button(topButtonsComposite, SWT.PUSH);
        addAllButton.setText("AddAll");
        addAllButton.setLayoutData(buttonGridData);
        addAllButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (launchConfViewer != null) {
                    TreeItem[] items = launchConfViewer.getTree().getItems();
                    for (TreeItem item : items) {
                        addToChoosenInput(item.getData());
                    }
                    setChoosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    private void initChoosenConfViewer(Composite parent) {
        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
        choosenConfViewer = new CheckboxTreeViewer(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        choosenConfViewer.setContentProvider(new ChoosenConfigurationContentProvider());
        choosenConfViewer.setLabelProvider(new ChoosenConfigurationLabelProvider());
        
        Tree choosenConfViewerTree = choosenConfViewer.getTree();
        choosenConfViewerTree.setLayoutData(fillBothGridData);
        choosenConfViewerTree.setHeaderVisible(true);
        choosenConfViewerTree.setFont(parent.getFont());
        TreeColumn nameColumn = new TreeColumn(choosenConfViewerTree, SWT.LEFT);
        nameColumn.setText("Name");
        nameColumn.setWidth(200);
        
        choosenConfViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof CLaunchConfigurationElement) {
                    CLaunchConfigurationElement element = (CLaunchConfigurationElement) event.getElement(); 
                    System.out.println(element.getConfiguration().getName());
                    element.setEnabled(event.getChecked());
                    setChoosenInput();
                }
            }
        });
        choosenConfViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                removeFromChoosenInput(selection.getFirstElement());
                setChoosenInput();
                updateLaunchConfigurationDialog();
            }
        });
        choosenConfViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setButtonsEnabled();
            }
        });
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
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<CLaunchConfigurationElement> selection = getTreeSelection(choosenConfViewer);
                for (CLaunchConfigurationElement selected : selection) {
                    int index = choosenConfInput.indexOf(selected);
                    if (index >= 1) {
                        CLaunchConfigurationElement prev = choosenConfInput.get(index - 1);
                        if (! selection.contains(prev)) {
                            choosenConfInput.set(index - 1, selected);
                            choosenConfInput.set(index, prev);
                        }
                    }
                }
                setChoosenInput();
                updateLaunchConfigurationDialog();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        downButton = new Button(bottomButtonsComposite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(buttonGridData);
        downButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<CLaunchConfigurationElement> selection = getTreeSelection(choosenConfViewer);
                if (! selection.isEmpty()) {
                    for (int i = selection.size() - 1; i >= 0; i--) {
                        CLaunchConfigurationElement selected = selection.get(i);
                        int index = choosenConfInput.indexOf(selected);
                        if (index <= choosenConfInput.size() - 2) {
                            CLaunchConfigurationElement next = choosenConfInput.get(index + 1);
                            if (! selection.contains(next)) {
                                choosenConfInput.set(index + 1, selected);
                                choosenConfInput.set(index, next);
                            }
                        }
                    }
                }
                setChoosenInput();
                updateLaunchConfigurationDialog();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        removeButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(buttonGridData);
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (choosenConfViewer != null) {
                    TreeSelection selection = (TreeSelection) choosenConfViewer.getSelection();
                    Iterator<?> iterator = selection.iterator();
                    while (iterator.hasNext()) {
                        removeFromChoosenInput(iterator.next());
                    }
                    setChoosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        removeAllButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeAllButton.setText("RemoveAll");
        removeAllButton.setLayoutData(buttonGridData);
        removeAllButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (choosenConfViewer != null) {
                    TreeItem[] items = choosenConfViewer.getTree().getItems();
                    for (TreeItem item : items) {
                        removeFromChoosenInput(item.getData());
                    }
                    setChoosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {}

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        List<ILaunchConfiguration> launchConfViewerInput;
        try {
            launchConfViewerInput = CLaunchConfigurationHelper.getAllConfigurations(configuration);
        } catch (CoreException e) {
            throw new SWTException(String.format("Failed to get launch configurations. Message: \"%s\"", e.getLocalizedMessage()));
        }
        if (launchConfViewer != null) {
            launchConfViewer.setInput(launchConfViewerInput);
        }
        try {
            choosenConfInput = CLaunchConfigurationHelper.readElements(configuration);
        } catch (CoreException e) {
            throw new SWTException(String.format(
                    "Failed to read elements from configuration %s. Message: \"%s\"",
                    String.valueOf(configuration),
                    e.getLocalizedMessage()
            ));
        }
        setChoosenInput();
        setButtonsEnabled();
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
//        CLaunchConfigurationHelper.removeElements(configuration);
        CLaunchConfigurationHelper.writeElements(choosenConfInput, configuration);
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        boolean isValid = true;
        try {
            if (CLaunchConfigurationHelper.containsLoop(launchConfig)) {
                setErrorMessage(String.format("Launch configuration %s cotains loop", String.valueOf(launchConfig)));
                isValid = false;
            }
        } catch (CoreException e) {
            setErrorMessage(e.getLocalizedMessage());
            isValid = false;
        }
        return isValid && super.isValid(launchConfig);
    }

    @Override
    public String getName() {
        return "Main";
    }

    private void setButtonsEnabled() {
        if (launchConfViewer != null) {
            if (addButton != null) {
                addButton.setEnabled(! ((TreeSelection) launchConfViewer.getSelection()).isEmpty());
            }
            if (addAllButton != null) {
                addAllButton.setEnabled(launchConfViewer.getTree().getItems().length > 0);
            }
        }
        if (choosenConfViewer != null) {
            if (upButton != null) {
                upButton.setEnabled(! ((TreeSelection) choosenConfViewer.getSelection()).isEmpty());
            }
            if (downButton != null) {
                downButton.setEnabled(! ((TreeSelection) choosenConfViewer.getSelection()).isEmpty());
            }
            if (removeButton != null) {
                removeButton.setEnabled(! ((TreeSelection) choosenConfViewer.getSelection()).isEmpty());
            }
            if (removeAllButton != null) {
                removeAllButton.setEnabled(choosenConfViewer.getTree().getItems().length > 0);
            }
        }
    }
    
    private void addToChoosenInput(Object o) {
        if (choosenConfInput != null && o != null && o instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement element = (CLaunchConfigurationElement) o;
            CLaunchConfigurationElement choosenElement = new CLaunchConfigurationElement(element);
            choosenElement.setEnabled(true);
            choosenConfInput.add(choosenElement);
        }
    }
    
    private void removeFromChoosenInput(Object o) {
        if (choosenConfInput != null && o != null && o instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement element = (CLaunchConfigurationElement) o;
            choosenConfInput.remove(element);
        }
    }
    
    private void setChoosenInput() {
        if (choosenConfViewer != null && choosenConfInput != null) {
            choosenConfViewer.setInput(choosenConfInput);
            for (CLaunchConfigurationElement element : choosenConfInput) {
                choosenConfViewer.setChecked(element, element.isEnabled());
            }
        }
        setButtonsEnabled();
    }
    
    private static List<CLaunchConfigurationElement> getTreeSelection(TreeViewer treeViewer) {
        if (treeViewer != null) {
            TreeSelection selection = (TreeSelection) treeViewer.getSelection();
            List<CLaunchConfigurationElement> selected = new ArrayList<>();
            Iterator<?> iterator = selection.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (next instanceof CLaunchConfigurationElement) {
                    selected.add((CLaunchConfigurationElement) next);
                }
            }
            return selected;
        }
        return Collections.emptyList();
    }
}
