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

/**
 * Dialog for editing Composite Launch element
 */
public class EditCLaunchElementDialog extends TitleAreaDialog {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 250;

    private CLaunchConfigurationElement element;
    private Text delayText;

    private Button enabledCheckbox;
    private Button waitPreviousCheckbox;

    public EditCLaunchElementDialog(Shell parentShell, CLaunchConfigurationElement element) {
        this(parentShell);
        this.element = element;
    }

    protected EditCLaunchElementDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle(Messages.EditCLaunchElementDialog_0);
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

        Composite delayComposite = new Composite(mainComposite, SWT.NONE);
        delayComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        GridLayout delayLayout = new GridLayout();
        delayLayout.numColumns = 2;
        delayComposite.setLayout(delayLayout);
        delayComposite.setFont(parent.getFont());

        GridData labelGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);

        Label delayLabel = new Label(delayComposite, SWT.NONE);
        delayLabel.setText(Messages.EditCLaunchElementDialog_1);
        delayLabel.setLayoutData(labelGridData);

        GridData textGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
        textGridData.widthHint = 64;

        delayText = new Text(delayComposite, SWT.SINGLE | SWT.BORDER);
        delayText.setLayoutData(textGridData);
        delayText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();
                if (text == null || text.equals("")) { //$NON-NLS-1$
                    text = "0"; //$NON-NLS-1$
                }
                int delay = element.getDelay();
                try {
                    int newValue = Integer.parseInt(text);
                    if (newValue < 0 || newValue > Integer.MAX_VALUE) {
                        throw new NumberFormatException();
                    }
                    if (newValue != delay) {
                        element.setDelay(newValue);
                        setControlValues();
                    }
                } catch (NumberFormatException exc) {
                    element.setDelay(delay);
                    setControlValues();
                }
            }
        });

        GridData buttonGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false);
        buttonGridData.horizontalIndent = 4;
        buttonGridData.verticalIndent = 4;
        buttonGridData.widthHint = 150;

        waitPreviousCheckbox = new Button(mainComposite, SWT.CHECK);
        waitPreviousCheckbox.setText(Messages.EditCLaunchElementDialog_4);
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
        
        enabledCheckbox = new Button(mainComposite, SWT.CHECK);
        enabledCheckbox.setText(Messages.EditCLaunchElementDialog_5);
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

        setControlValues();

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.EditCLaunchElementDialog_6);
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
        if (delayText != null) {
            int caretPosition = delayText.getCaretPosition();
            delayText.setText(String.valueOf(element.getDelay()));
            delayText.setSelection(caretPosition, caretPosition);
        }
    }
}
