package com.zymosi3.eclipse.plugins.claunch.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.zymosi3.eclipse.plugins.claunch.model.CLaunchConfigurationElement;

public class EditChosenConfigurationDialog extends TitleAreaDialog {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 280;

    private CLaunchConfigurationElement element;
    private Text delayBeforeText;
    private Text delayAfterText;

    private Button enabledCheckbox;
    private Button waitPreviousCheckbox;

    public EditChosenConfigurationDialog(Shell parentShell, CLaunchConfigurationElement element) {
        this(parentShell);
        this.element = element;
    }

    protected EditChosenConfigurationDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Edit Composite Launch Element");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        Composite mainComposite = new Composite(container, SWT.BORDER);
        GridData mainGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false);
        mainGridData.horizontalIndent = 2;
        mainGridData.verticalIndent = 2;
        mainGridData.widthHint = WIDTH - 14;
        mainGridData.minimumWidth = WIDTH - 14;
        mainComposite.setLayoutData(mainGridData);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 1;
        mainComposite.setLayout(mainLayout);
        mainComposite.setFont(parent.getFont());

        Composite delayBeforeComposite = new Composite(mainComposite, SWT.NONE);
        delayBeforeComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        GridLayout delayBeforeLayout = new GridLayout();
        delayBeforeLayout.numColumns = 2;
        delayBeforeComposite.setLayout(delayBeforeLayout);
        delayBeforeComposite.setFont(parent.getFont());

        GridData labelGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);

        Label delayBeforeLabel = new Label(delayBeforeComposite, SWT.NONE);
        delayBeforeLabel.setText("Delay before (ms)");
        delayBeforeLabel.setLayoutData(labelGridData);

        GridData textGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
        textGridData.widthHint = 64;

        delayBeforeText = new Text(delayBeforeComposite, SWT.SINGLE | SWT.BORDER);
        delayBeforeText.setLayoutData(textGridData);
        delayBeforeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();
                if (text == null || text.equals("")) {
                    text = "0";
                }
                int delayBefore = element.getDelayBefore();
                try {
                    int newValue = Integer.parseInt(text);
                    if (newValue < 0 || newValue > Integer.MAX_VALUE) {
                        throw new NumberFormatException();
                    }
                    if (newValue != delayBefore) {
                        element.setDelayBefore(newValue);
                        setControlValues();
                    }
                } catch (NumberFormatException exc) {
                    element.setDelayBefore(delayBefore);
                    setControlValues();
                }
            }
        });

        Label delayAfterLabel = new Label(delayBeforeComposite, SWT.NONE);
        delayAfterLabel.setText("Delay after (ms)");
        delayAfterLabel.setLayoutData(labelGridData);

        delayAfterText = new Text(delayBeforeComposite, SWT.SINGLE | SWT.BORDER);
        delayAfterText.setLayoutData(textGridData);
        delayAfterText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();
                if (text == null || text.equals("")) {
                    text = "0";
                }
                int delayAfter = element.getDelayAfter();
                try {
                    int newValue = Integer.parseInt(text);
                    if (newValue < 0 || newValue > Integer.MAX_VALUE) {
                        throw new NumberFormatException();
                    }
                    if (newValue != delayAfter) {
                        element.setDelayAfter(newValue);
                        setControlValues();
                    }
                } catch (NumberFormatException exc) {
                    element.setDelayAfter(delayAfter);
                    setControlValues();
                }
            }
        });

        GridData buttonGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false);
        buttonGridData.horizontalIndent = 4;

        enabledCheckbox = new Button(mainComposite, SWT.CHECK);
        enabledCheckbox.setText("Enabled");
        enabledCheckbox.setLayoutData(buttonGridData);
        enabledCheckbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                element.setEnabled(enabledCheckbox.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        waitPreviousCheckbox = new Button(mainComposite, SWT.CHECK);
        waitPreviousCheckbox.setText("Wait previous");
        waitPreviousCheckbox.setLayoutData(buttonGridData);
        waitPreviousCheckbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                element.setWaitPrevious(waitPreviousCheckbox.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        setControlValues();

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Composite Launch");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(WIDTH, HEIGHT);
    }

    private void setControlValues() {
        if (enabledCheckbox != null) {
            enabledCheckbox.setSelection(element.isEnabled());
        }
        if (waitPreviousCheckbox != null) {
            waitPreviousCheckbox.setSelection(element.isWaitPrevious());
        }
        if (delayBeforeText != null) {
            int caretPosition = delayBeforeText.getCaretPosition();
            delayBeforeText.setText(String.valueOf(element.getDelayBefore()));
            delayBeforeText.setSelection(caretPosition, caretPosition);
        }
        if (delayAfterText != null) {
            int caretPosition = delayAfterText.getCaretPosition();
            delayAfterText.setText(String.valueOf(element.getDelayAfter()));
            delayAfterText.setSelection(caretPosition, caretPosition);
        }
    }
}
