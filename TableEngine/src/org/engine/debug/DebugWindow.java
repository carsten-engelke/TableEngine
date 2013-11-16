package org.engine.debug;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.engine.TableEngine;
import org.engine.property.Information;
import org.engine.property.InformationArrayStringException;
import org.engine.property.Property.Flag;
import org.engine.resource.BasicResource;
import org.engine.utils.Array;

public class DebugWindow {

	protected Shell shlDebug;
	private TableEngine engine;
	private Text textSettings;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;
	private Text textContent;
	private Text text;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DebugWindow window = new DebugWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DebugWindow() {

		super();
		this.engine = null;
	}

	public DebugWindow(TableEngine engine) {

		super();
		this.engine = engine;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlDebug.open();
		shlDebug.layout();
		while (!shlDebug.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlDebug = new Shell();
		shlDebug.setSize(640, 480);
		shlDebug.setText("Debug");
		shlDebug.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		new TestClass().execute();

		Menu menu = new Menu(shlDebug, SWT.BAR);
		shlDebug.setMenuBar(menu);

		MenuItem mntmTableengine_1 = new MenuItem(menu, SWT.CASCADE);
		mntmTableengine_1.setText("TableEngine");

		Menu menu_1 = new Menu(mntmTableengine_1);
		mntmTableengine_1.setMenu(menu_1);

		MenuItem mntmSkin_1 = new MenuItem(menu_1, SWT.CASCADE);
		mntmSkin_1.setText("Skin");

		Menu menu_2 = new Menu(mntmSkin_1);
		mntmSkin_1.setMenu(menu_2);

		MenuItem mntmPackSkins = new MenuItem(menu_2, SWT.NONE);
		mntmPackSkins.setText("Pack Skins");
		mntmPackSkins.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				new Thread() {

					public void run() {
						try {
							BasicResource.packAllSkins();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		MenuItem mntmDarkMdpi = new MenuItem(menu_2, SWT.NONE);
		mntmDarkMdpi.setText("Dark mdpi");
		mntmDarkMdpi.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				engine.debugOrderList.add("Skin:test/ui-dark-mdpi.json");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		MenuItem mntmDarkXhdpi = new MenuItem(menu_2, SWT.NONE);
		mntmDarkXhdpi.setText("Dark xhdpi");
		mntmDarkXhdpi.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				engine.debugOrderList.add("Skin:test/ui-dark-xhdpi.json");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		MenuItem mntmLightMdpi = new MenuItem(menu_2, SWT.NONE);
		mntmLightMdpi.setText("Light mdpi");

		MenuItem mntmLightXhdpi = new MenuItem(menu_2, SWT.NONE);
		mntmLightXhdpi.setText("Light xhdpi");

		MenuItem mntmNetwork = new MenuItem(menu_1, SWT.CASCADE);
		mntmNetwork.setText("Network");

		Menu menu_3 = new Menu(mntmNetwork);
		mntmNetwork.setMenu(menu_3);

		MenuItem mntmOnlinemode = new MenuItem(menu_3, SWT.RADIO);
		mntmOnlinemode.setText("ONLINE_MODE");

		MenuItem mntmOfflinemode = new MenuItem(menu_3, SWT.RADIO);
		mntmOfflinemode.setText("OFFLINE_MODE");

		MenuItem mntmManualmode = new MenuItem(menu_3, SWT.RADIO);
		mntmManualmode.setText("MANUAL_MODE");
		
		final MenuItem mntmDebugMode = new MenuItem(menu_1, SWT.CHECK);
		mntmDebugMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (engine != null) {
					engine.preferences.putBoolean("debug", mntmDebugMode.getSelection());
				}
			}
		});
		mntmDebugMode.setSelection(true);
		mntmDebugMode.setText("Debug Mode");

		TabFolder tabFolder = new TabFolder(shlDebug, SWT.NONE);

		TabItem tbtmLocalDebug = new TabItem(tabFolder, SWT.NONE);
		tbtmLocalDebug.setText("Local Debug");

		SashForm sashForm = new SashForm(tabFolder, SWT.NONE);
		tbtmLocalDebug.setControl(sashForm);

		Group grpSettings = new Group(sashForm, SWT.NONE);
		grpSettings.setText("Settings");
		grpSettings.setLayout(new GridLayout(2, false));

		Button btnRefreshSettings = new Button(grpSettings, SWT.NONE);
		btnRefreshSettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshSettings();
			}
		});
		btnRefreshSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		btnRefreshSettings.setText("Refresh Settings");
		new Label(grpSettings, SWT.NONE);

		textSettings = new Text(grpSettings, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1));
		
		Button btnAddAnimation = new Button(grpSettings, SWT.NONE);
		btnAddAnimation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAddAnimation.setText("Add Animation");
		
		Button btnAddPlayer = new Button(grpSettings, SWT.NONE);
		btnAddPlayer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnAddPlayer.setText("Add Player");

		Group grpContent = new Group(sashForm, SWT.NONE);
		grpContent.setText("Content");
		grpContent.setLayout(new GridLayout(2, false));

		Button btnShowContent = new Button(grpContent, SWT.NONE);
		btnShowContent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showContent();
			}
		});
		btnShowContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		btnShowContent.setText("Show Content");

		Button btnShowFlaggedContent = new Button(grpContent, SWT.NONE);
		btnShowFlaggedContent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showFlaggedContent();
			}
		});
		btnShowFlaggedContent.setText("Show Flagged Content");

		textContent = new Text(grpContent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1));
		sashForm.setWeights(new int[] { 1, 1 });

		TabItem tbtmCommandServer = new TabItem(tabFolder, SWT.NONE);
		tbtmCommandServer.setText("Command Server");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmCommandServer.setControl(composite);
		composite.setLayout(new GridLayout(5, false));

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText("Command");

		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setItems(new String[] { "set", "get", "table", "info",
				"timestamp", "key" });
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));
		combo.select(0);

		Button button = new Button(composite, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		button.setText("Debug");

		Button button_1 = new Button(composite, SWT.NONE);
		button_1.setText("EXECUTE");

		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText("Slot");

		Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				4, 1));
		spinner.setMaximum(32);
		spinner.setMinimum(1);
		spinner.setSelection(1);

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_2.setText("Name");
		label_2.setAlignment(SWT.RIGHT);

		text_1 = new Text(composite, SWT.BORDER);
		text_1.setText("TestPlayer");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4,
				1));

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_3.setText("Password");

		text_2 = new Text(composite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4,
				1));

		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_5.setText("ID");

		text_3 = new Text(composite, SWT.BORDER);
		text_3.setText("TEST_PLAYER1");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4,
				1));

		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_6.setText("Content");

		text_4 = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_4.setText("<object>blabla</object>");
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 2));
		new Label(composite, SWT.NONE);

		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_7.setText("Key");

		text_5 = new Text(composite, SWT.BORDER);
		text_5.setText("148357384723");
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4,
				1));

		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_9.setText("KeyID");

		Spinner spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setMaximum(999);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(234);

		Button button_2 = new Button(composite, SWT.NONE);
		button_2.setText("Create Valid Key");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Label label_10 = new Label(composite, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		label_10.setText("Result");

		text_6 = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		TabItem tbtmTest = new TabItem(tabFolder, SWT.NONE);
		tbtmTest.setText("Test");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmTest.setControl(composite_1);
		composite_1.setLayout(new GridLayout(3, false));
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Information i = new Information("ID", "TAG", Flag.ADD_CHANGE, "CONTENT");
				Information i2 = new Information("ID2", "TAG", Flag.REMOVE, "CONTENT2");
				Array<Information> ai = new Array<Information>();
				ai.add(i);
				ai.add(i2);
				text.setText(Information.InformationsToString(ai));
			}
		});
		btnNewButton.setText("Create StringFromInfos");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnCreateInfoFrom = new Button(composite_1, SWT.NONE);
		btnCreateInfoFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Array<Information> i = Information.StringToInformations(text.getText());
					System.out.println("SUCCESS: " + i.toString());
				} catch (InformationArrayStringException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnCreateInfoFrom.setText("Create Info from String");

	}

	protected void showFlaggedContent() {

		if (engine != null) {

			textContent.setText("");
			for (Information i : Information.PropertiesToInformations(engine
					.getPropertiesFlagged())) {
				textContent.append(Information.ReadableInfoString(i, 0));
			}
		}
	}

	protected void showContent() {

		if (engine != null) {

			textContent.setText("");
			for (Information i : Information.PropertiesToInformations(engine
					.getProperties())) {
				textContent.append(Information.ReadableInfoString(i, 0));
			}
		}
	}

	protected void refreshSettings() {

		if (engine != null) {

			textSettings.setText("engine.preferences:\n");
			for (String k : engine.preferences.get().keySet()) {
				textSettings.append(k + " :"
						+ String.valueOf(engine.preferences.get().get(k))
						+ "\n");
			}
		}
	}
}
