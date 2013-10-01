package com.zymosi3.eclipse.plugins.claunch.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;
import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationHelper;

/**
 * The main tab of Composite launch UI.
 */
public class CompositeLaunchMainTab extends AbstractLaunchConfigurationTab {
    
    private TreeViewer launchConfViewer;
    private CheckboxTreeViewer chosenConfViewer;
    private Button addButton;
    private Button addAllButton;
    private Button upButton;
    private Button downButton;
    private Button editButton;
    private Button removeButton;
    private Button removeAllButton;
    
    private String mode;
    
    private List<CLaunchConfigurationElement> chosenConfInput = new ArrayList<>();

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
        
        initChosenConfViewer(mainComposite);
        
        initBottomButtons(mainComposite);
        
        setButtonsEnabled();
        setChosenInput();
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
        nameColumn.setText(Messages.CompositeLaunchMainTab_0);
        nameColumn.setWidth(200);
        TreeColumn typeColumn = new TreeColumn(launchConfViewerTree, SWT.LEFT);
        typeColumn.setText(Messages.CompositeLaunchMainTab_1);
        typeColumn.setWidth(150);
        TreeColumn modesColumn = new TreeColumn(launchConfViewerTree, SWT.LEFT);
        modesColumn.setText(Messages.CompositeLaunchMainTab_2);
        modesColumn.setWidth(150);
        
        launchConfViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                addToChosenInput(selection.getFirstElement());
                setChosenInput();
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
        
        GridData buttonGridData = new GridData(GridData.CENTER, GridData.BEGINNING, false, false);
        buttonGridData.heightHint = 26;
        buttonGridData.minimumHeight = 26;
        buttonGridData.widthHint = 74;
        buttonGridData.minimumHeight = 74;
        addButton = new Button(topButtonsComposite, SWT.PUSH);
        addButton.setText(Messages.CompositeLaunchMainTab_3);
        addButton.setLayoutData(buttonGridData);
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (launchConfViewer != null) {
                    TreeSelection selection = (TreeSelection) launchConfViewer.getSelection();
                    Iterator<?> iterator = selection.iterator();
                    while (iterator.hasNext()) {
                        addToChosenInput(iterator.next());
                    }
                    setChosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        addAllButton = new Button(topButtonsComposite, SWT.PUSH);
        addAllButton.setText(Messages.CompositeLaunchMainTab_4);
        addAllButton.setLayoutData(buttonGridData);
        addAllButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (launchConfViewer != null) {
                    TreeItem[] items = launchConfViewer.getTree().getItems();
                    for (TreeItem item : items) {
                        addToChosenInput(item.getData());
                    }
                    setChosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    private void initChosenConfViewer(Composite parent) {
        GridData fillBothGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
        chosenConfViewer = new CheckboxTreeViewer(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        chosenConfViewer.setContentProvider(new ChosenConfigurationContentProvider());
        chosenConfViewer.setLabelProvider(new ChosenConfigurationLabelProvider());
        
        Tree chosenConfViewerTree = chosenConfViewer.getTree();
        chosenConfViewerTree.setLayoutData(fillBothGridData);
        chosenConfViewerTree.setHeaderVisible(true);
        chosenConfViewerTree.setFont(parent.getFont());
        TreeColumn nameColumn = new TreeColumn(chosenConfViewerTree, SWT.LEFT);
        nameColumn.setText(Messages.CompositeLaunchMainTab_5);
        nameColumn.setWidth(200);
        TreeColumn delayBeforeColumn = new TreeColumn(chosenConfViewerTree, SWT.LEFT);
        delayBeforeColumn.setText(Messages.CompositeLaunchMainTab_6);
        delayBeforeColumn.setWidth(150);
        TreeColumn waitPreviousColumn = new TreeColumn(chosenConfViewerTree, SWT.LEFT);
        waitPreviousColumn.setText(Messages.CompositeLaunchMainTab_7);
        waitPreviousColumn.setWidth(150);
        
        chosenConfViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof CLaunchConfigurationElement) {
                    CLaunchConfigurationElement element = (CLaunchConfigurationElement) event.getElement(); 
                    System.out.println(element.getConfiguration().getName());
                    element.setEnabled(event.getChecked());
                    setChosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
        });
        chosenConfViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                openEditDialog();
            }
        });
        chosenConfViewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
        
        GridData buttonGridData = new GridData(GridData.CENTER, GridData.BEGINNING, false, false);
        buttonGridData.heightHint = 26;
        buttonGridData.minimumHeight = 26;
        buttonGridData.widthHint = 74;
        buttonGridData.minimumHeight = 74;
        upButton = new Button(bottomButtonsComposite, SWT.PUSH);
        upButton.setText(Messages.CompositeLaunchMainTab_8);
        upButton.setLayoutData(buttonGridData);
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<CLaunchConfigurationElement> selection = getTreeSelection(chosenConfViewer);
                for (CLaunchConfigurationElement selected : selection) {
                    int index = chosenConfInput.indexOf(selected);
                    if (index >= 1) {
                        CLaunchConfigurationElement prev = chosenConfInput.get(index - 1);
                        if (! selection.contains(prev)) {
                            chosenConfInput.set(index - 1, selected);
                            chosenConfInput.set(index, prev);
                        }
                    }
                }
                setChosenInput();
                updateLaunchConfigurationDialog();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        downButton = new Button(bottomButtonsComposite, SWT.PUSH);
        downButton.setText(Messages.CompositeLaunchMainTab_9);
        downButton.setLayoutData(buttonGridData);
        downButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<CLaunchConfigurationElement> selection = getTreeSelection(chosenConfViewer);
                if (! selection.isEmpty()) {
                    for (int i = selection.size() - 1; i >= 0; i--) {
                        CLaunchConfigurationElement selected = selection.get(i);
                        int index = chosenConfInput.indexOf(selected);
                        if (index <= chosenConfInput.size() - 2) {
                            CLaunchConfigurationElement next = chosenConfInput.get(index + 1);
                            if (! selection.contains(next)) {
                                chosenConfInput.set(index + 1, selected);
                                chosenConfInput.set(index, next);
                            }
                        }
                    }
                }
                setChosenInput();
                updateLaunchConfigurationDialog();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        editButton = new Button(bottomButtonsComposite, SWT.PUSH);
        editButton.setText(Messages.CompositeLaunchMainTab_10);
        editButton.setLayoutData(buttonGridData);
        editButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openEditDialog();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Label separator = new Label(bottomButtonsComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false));
        
        removeButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeButton.setText(Messages.CompositeLaunchMainTab_11);
        removeButton.setLayoutData(buttonGridData);
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (chosenConfViewer != null) {
                    TreeSelection selection = (TreeSelection) chosenConfViewer.getSelection();
                    Iterator<?> iterator = selection.iterator();
                    while (iterator.hasNext()) {
                        removeFromChosenInput(iterator.next());
                    }
                    setChosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        removeAllButton = new Button(bottomButtonsComposite, SWT.PUSH);
        removeAllButton.setText(Messages.CompositeLaunchMainTab_12);
        removeAllButton.setLayoutData(buttonGridData);
        removeAllButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (chosenConfViewer != null) {
                    TreeItem[] items = chosenConfViewer.getTree().getItems();
                    for (TreeItem item : items) {
                        removeFromChosenInput(item.getData());
                    }
                    setChosenInput();
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
            throw new SWTException(String.format(Messages.CompositeLaunchMainTab_13, e.getLocalizedMessage()));
        }
        if (launchConfViewer != null) {
            launchConfViewer.setInput(launchConfViewerInput);
        }
        try {
            chosenConfInput = CLaunchConfigurationHelper.readElements(configuration);
        } catch (CoreException e) {
            throw new SWTException(String.format(
                    Messages.CompositeLaunchMainTab_14,
                    String.valueOf(configuration),
                    e.getLocalizedMessage()
            ));
        }
        setChosenInput();
        setButtonsEnabled();
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        try {
            CLaunchConfigurationHelper.removeElements(configuration);
        } catch (CoreException e) {
            throw new SWTException(String.format(
                    Messages.CompositeLaunchMainTab_15,
                    String.valueOf(configuration),
                    e.getLocalizedMessage()
            ));
        }
        CLaunchConfigurationHelper.writeElements(chosenConfInput, configuration);
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);
        setMessage(null);
        boolean isValid = true;
        try {
            if (CLaunchConfigurationHelper.containsLoop(launchConfig)) {
                setErrorMessage(String.format(Messages.CompositeLaunchMainTab_16, String.valueOf(launchConfig)));
                isValid = false;
            }
        } catch (CoreException e) {
            setErrorMessage(e.getLocalizedMessage());
            isValid = false;
        }
        try {
            List<CLaunchConfigurationElement> elements = CLaunchConfigurationHelper.readElements(launchConfig);
            if (! elements.isEmpty()) {
                for (CLaunchConfigurationElement element : elements) {
                    if (! element.getType().supportsMode(mode)) {
                        setMessage(String.format(
                                Messages.CompositeLaunchMainTab_17, 
                                String.valueOf(element.getConfiguration()),
                                mode
                        ));
                    }
                }
            } else {
                setErrorMessage(String.format(Messages.CompositeLaunchMainTab_18, String.valueOf(launchConfig)));
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
        return Messages.CompositeLaunchMainTab_19;
    }
    
    private void openEditDialog() {
        TreeSelection selection = (TreeSelection) chosenConfViewer.getSelection();
        if (selection.size() == 1) {
            CLaunchConfigurationElement element = (CLaunchConfigurationElement) selection.getFirstElement();
            CLaunchConfigurationElement elementCopy = new CLaunchConfigurationElement(element);
            EditCLaunchElementDialog dialog = new EditCLaunchElementDialog(getShell(), elementCopy);
            if (dialog.open() == Dialog.OK) {
                boolean changed = false;
                if (element.isEnabled() != elementCopy.isEnabled()) {
                    element.setEnabled(elementCopy.isEnabled());
                    changed = true;
                }
                if (element.getDelay() != elementCopy.getDelay()) {
                    element.setDelay(elementCopy.getDelay());
                    changed = true;
                }
                if (element.isWaitPrevious() != elementCopy.isWaitPrevious()) {
                    element.setWaitPrevious(elementCopy.isWaitPrevious());
                    changed = true;
                }
                if (changed) {
                    setChosenInput();
                    updateLaunchConfigurationDialog();
                }
            }
        }
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
        if (chosenConfViewer != null) {
            if (upButton != null) {
                upButton.setEnabled(! ((TreeSelection) chosenConfViewer.getSelection()).isEmpty());
            }
            if (downButton != null) {
                downButton.setEnabled(! ((TreeSelection) chosenConfViewer.getSelection()).isEmpty());
            }
            if (editButton != null) {
                editButton.setEnabled(((TreeSelection) chosenConfViewer.getSelection()).size() == 1);
            }
            if (removeButton != null) {
                removeButton.setEnabled(! ((TreeSelection) chosenConfViewer.getSelection()).isEmpty());
            }
            if (removeAllButton != null) {
                removeAllButton.setEnabled(chosenConfViewer.getTree().getItems().length > 0);
            }
        }
    }
    
    private void addToChosenInput(Object o) {
        if (chosenConfInput != null && o != null && o instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement element = (CLaunchConfigurationElement) o;
            CLaunchConfigurationElement chosenElement = new CLaunchConfigurationElement(element);
            chosenElement.setEnabled(true);
            chosenConfInput.add(chosenElement);
        }
    }
    
    private void removeFromChosenInput(Object o) {
        if (chosenConfInput != null && o != null && o instanceof CLaunchConfigurationElement) {
            CLaunchConfigurationElement element = (CLaunchConfigurationElement) o;
            chosenConfInput.remove(element);
        }
    }
    
    private void setChosenInput() {
        if (chosenConfViewer != null && chosenConfInput != null) {
            chosenConfViewer.setInput(chosenConfInput);
            for (CLaunchConfigurationElement element : chosenConfInput) {
                chosenConfViewer.setChecked(element, element.isEnabled());
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
