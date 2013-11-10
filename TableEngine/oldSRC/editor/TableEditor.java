package org.engine.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.engine.geometry.Cube;

class ClipboardHintObject {

	public String caption;
	public String content;

	public ClipboardHintObject(final String caption, final String content) {

		this.caption = caption;
		this.content = content;

	}

	@Override
	public String toString() {

		return caption;
	}
}

class EditorObjectDescription {

	public String className;
	public String paramString;

	public EditorObjectDescription(final String className,
			final String defaultConstructor) {

		this.className = className;
		paramString = defaultConstructor;

	}

	@Override
	public String toString() {

		return className;
	}
}

public class TableEditor extends JFrame {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				try {

					final TableEditor frame = new TableEditor();
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private final JList classList = new JList();
	private final JTextArea codeTextArea = new JTextArea();
	private final JPanel contentPane = new JPanel();
	private final JTextField gridField = new JTextField();
	private final JTextField heightField = new JTextField();
	private final JList hintList = new JList();
	private final JList objectList = new JList();
	ProjectionPanel projectionPanel = new ProjectionPanel();
	private final JTable propertiesTable = new JTable();
	private EditorObject selectedObject;
	private final JTextField widthField = new JTextField();

	private final JSlider zoomSlider = new JSlider();

	public TableEditor() {

		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						TableEditor.class
								.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")));

		setTitle("TableEditor");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		final JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final JFileChooser jfc = new JFileChooser(System
						.getProperty("user.dir"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(final File f) {

						if (f.getName().endsWith(".ed") || f.isDirectory()) {
							return true;
						}
						return false;
					}

					@Override
					public String getDescription() {

						return "Table Editor File *.ed";
					}
				});
				jfc.showOpenDialog(null);
				final File openFile = jfc.getSelectedFile();
				if ((openFile != null) && openFile.exists()) {
					try {
						projectionPanel.objectList.clear();
						final BufferedReader br = new BufferedReader(
								new FileReader(openFile));
						String object = br.readLine();
						while (object != null) {
							if (object.contains("<>")) {
								projectionPanel.add(new EditorObject(object,
										projectionPanel));
							}
							object = br.readLine();
						}
						br.close();
					} catch (final FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (final IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		mnFile.add(mntmOpen);

		final JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final JFileChooser jfc = new JFileChooser(System
						.getProperty("user.dir"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(final File f) {

						if (f.getName().endsWith(".ed") || f.isDirectory()) {
							return true;
						}
						return false;
					}

					@Override
					public String getDescription() {

						return "Table Editor File *.ed";
					}
				});
				jfc.showSaveDialog(null);
				File saveFile = jfc.getSelectedFile();
				if (saveFile != null) {
					try {
						if (!saveFile.getName().endsWith(".ed")) {
							saveFile = new File(saveFile.getPath() + ".ed");
						}
						final FileWriter fw = new FileWriter(saveFile);
						for (final EditorObject o : projectionPanel.objectList) {
							fw.write(o.saveToString() + "\n");
						}
						fw.close();
					} catch (final FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (final IOException e1) {
						e1.printStackTrace();
					}
					// } else {
					// System.out.println("ABORTED?");
				}
			}
		});
		mnFile.add(mntmSave);

		final JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		final JMenuItem mntmCreateEditableDefaults = new JMenuItem(
				"Create Editable Defaults");
		mntmCreateEditableDefaults.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				new CreateEditableStrings().setVisible(true);
			}
		});
		mnTools.add(mntmCreateEditableDefaults);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		final GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		final JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOneTouchExpandable(true);
		splitPane_2.setResizeWeight(0.9);
		final GridBagConstraints gbc_splitPane_2 = new GridBagConstraints();
		gbc_splitPane_2.fill = GridBagConstraints.BOTH;
		gbc_splitPane_2.gridx = 1;
		gbc_splitPane_2.gridy = 0;
		contentPane.add(splitPane_2, gbc_splitPane_2);

		final JPanel panel = new JPanel();
		splitPane_2.setRightComponent(panel);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 35, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				1.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		final JLabel label = new JLabel("Width");
		final GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel.add(label, gbc_label);

		widthField.setText("800");
		widthField.setColumns(10);
		widthField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {

				updateSize();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {

				updateSize();

			}

			@Override
			public void removeUpdate(final DocumentEvent e) {

				updateSize();

			}
		});
		final GridBagConstraints gbc_widthField = new GridBagConstraints();
		gbc_widthField.fill = GridBagConstraints.HORIZONTAL;
		gbc_widthField.insets = new Insets(0, 0, 5, 0);
		gbc_widthField.gridx = 1;
		gbc_widthField.gridy = 0;
		panel.add(widthField, gbc_widthField);

		final JLabel label_1 = new JLabel("Height");
		final GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		panel.add(label_1, gbc_label_1);

		heightField.setText("600");
		heightField.setColumns(10);
		heightField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {

				updateSize();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {

				updateSize();

			}

			@Override
			public void removeUpdate(final DocumentEvent e) {

				updateSize();

			}

		});
		final GridBagConstraints gbc_heightField = new GridBagConstraints();
		gbc_heightField.fill = GridBagConstraints.HORIZONTAL;
		gbc_heightField.insets = new Insets(0, 0, 5, 0);
		gbc_heightField.gridx = 1;
		gbc_heightField.gridy = 1;
		panel.add(heightField, gbc_heightField);

		final JLabel lblZoom = new JLabel("Zoom %");
		final GridBagConstraints gbc_lblZoom = new GridBagConstraints();
		gbc_lblZoom.anchor = GridBagConstraints.EAST;
		gbc_lblZoom.insets = new Insets(0, 0, 5, 5);
		gbc_lblZoom.gridx = 0;
		gbc_lblZoom.gridy = 2;
		panel.add(lblZoom, gbc_lblZoom);

		final GridBagConstraints gbc_zoomSlider = new GridBagConstraints();
		gbc_zoomSlider.insets = new Insets(0, 0, 5, 0);
		gbc_zoomSlider.fill = GridBagConstraints.BOTH;
		gbc_zoomSlider.gridx = 1;
		gbc_zoomSlider.gridy = 2;
		zoomSlider.setValue(100);
		zoomSlider.setMinimum(5);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMinorTickSpacing(10);
		zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {

				updateSize();
			}
		});

		panel.add(zoomSlider, gbc_zoomSlider);

		final JLabel lblGrid = new JLabel("Grid");
		final GridBagConstraints gbc_lblGrid = new GridBagConstraints();
		gbc_lblGrid.insets = new Insets(0, 0, 5, 5);
		gbc_lblGrid.anchor = GridBagConstraints.EAST;
		gbc_lblGrid.gridx = 0;
		gbc_lblGrid.gridy = 3;
		panel.add(lblGrid, gbc_lblGrid);

		gridField.setText("50");
		gridField.setColumns(10);
		gridField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {

				updateSize();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {

				updateSize();

			}

			@Override
			public void removeUpdate(final DocumentEvent e) {

				updateSize();

			}

		});
		final GridBagConstraints gbc_gridField = new GridBagConstraints();
		gbc_gridField.insets = new Insets(0, 0, 5, 0);
		gbc_gridField.fill = GridBagConstraints.HORIZONTAL;
		gbc_gridField.gridx = 1;
		gbc_gridField.gridy = 3;
		panel.add(gridField, gbc_gridField);

		final JScrollPane scrollPane_3 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_3.gridwidth = 2;
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 0;
		gbc_scrollPane_3.gridy = 4;
		panel.add(scrollPane_3, gbc_scrollPane_3);

		scrollPane_3.setViewportView(codeTextArea);

		final JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				if (classList.getSelectedValue() != null) {
					final EditorObjectDescription eod = (EditorObjectDescription) classList
							.getSelectedValue();
					projectionPanel.add(new EditorObject(eod.paramString,
							projectionPanel));
				}
			}
		});

		final JButton btnCreateCode = new JButton("Create Code");
		btnCreateCode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				createCode();

			}
		});
		final GridBagConstraints gbc_btnCreateCode = new GridBagConstraints();
		gbc_btnCreateCode.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateCode.gridwidth = 2;
		gbc_btnCreateCode.gridx = 0;
		gbc_btnCreateCode.gridy = 5;
		panel.add(btnCreateCode, gbc_btnCreateCode);

		final GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.BOTH;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 6;
		panel.add(btnAdd, gbc_btnAdd);

		final JScrollPane scrollPane_4 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_4 = new GridBagConstraints();
		gbc_scrollPane_4.gridheight = 2;
		gbc_scrollPane_4.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_4.gridx = 1;
		gbc_scrollPane_4.gridy = 6;
		panel.add(scrollPane_4, gbc_scrollPane_4);

		classList.setModel(new DefaultListModel());
		scrollPane_4.setViewportView(classList);

		final JButton btnLoad = new JButton("Add Classes");
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final JFileChooser jfc = new JFileChooser(System
						.getProperty("user.dir"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(final File f) {

						if (f.getName().endsWith(".editor") || f.isDirectory()) {
							return true;
						}
						return false;
					}

					@Override
					public String getDescription() {

						return "Editor Description Files *.editor";
					}
				});
				jfc.showOpenDialog(null);
				final File loadFile = jfc.getSelectedFile();
				loadEditorObjectClassesFromFile(loadFile);

			}
		});
		final GridBagConstraints gbc_btnLoad = new GridBagConstraints();
		gbc_btnLoad.fill = GridBagConstraints.BOTH;
		gbc_btnLoad.insets = new Insets(0, 0, 0, 5);
		gbc_btnLoad.gridx = 0;
		gbc_btnLoad.gridy = 7;
		panel.add(btnLoad, gbc_btnLoad);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		splitPane.setBackground(Color.BLACK);
		splitPane_2.setLeftComponent(splitPane);

		final JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.5);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(splitPane_1);

		final JScrollPane scrollPane = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane);
		objectList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {

				updateListAndTable();
			}
		});

		objectList.setModel(new DefaultListModel());
		scrollPane.setViewportView(objectList);

		final JPanel panel_1 = new JPanel();
		splitPane_1.setRightComponent(panel_1);
		final GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		panel_1.add(scrollPane_1, gbc_scrollPane_1);

		propertiesTable.setModel(new DefaultTableModel(new String[] { "Name",
				"Type", "Content" }, 3) {

			/**
			 * Column 0 and 1 are not editable
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(final int row, final int column) {

				if (column > 1) {
					return true;
				} else {
					return false;
				}
			}
		});
		((DefaultTableModel) propertiesTable.getModel())
				.addTableModelListener(new TableModelListener() {

					@Override
					public void tableChanged(final TableModelEvent e) {

						if (e.getType() == TableModelEvent.UPDATE) {
							if (selectedObject != null) {
								String bounds = "";
								String angle = null;
								String name = null;
								final DefaultTableModel model = (DefaultTableModel) propertiesTable
										.getModel();
								for (int i = 0; i < model.getRowCount(); i++) {
									selectedObject.setProperty(model
											.getValueAt(i, 0).toString(), model
											.getValueAt(i, 2).toString());
									if (model.getValueAt(i, 0).toString()
											.equals("Size")) {
										bounds = model.getValueAt(i, 2)
												.toString();
										bounds = bounds.substring(
												bounds.indexOf("\"") + 1,
												bounds.lastIndexOf("\""));
									}
									if (model.getValueAt(i, 0).toString()
											.equals("Angle")) {
										angle = model.getValueAt(i, 2)
												.toString();
									}
									if (model.getValueAt(i, 0).toString()
											.equals("UniqueID")) {
										name = model.getValueAt(i, 2)
												.toString();
										name = name.substring(
												name.indexOf("\"") + 1,
												name.lastIndexOf("\""));
									}
								}
								selectedObject.setBounds(new Cube(bounds));
								if (angle != null) {
									selectedObject.angle = Integer
											.valueOf(angle);
								}
								if (name != null) {
									selectedObject.name = name;
								}
								updateSize();
							}
						}

					}
				});
		scrollPane_1.setViewportView(propertiesTable);

		final JButton btnRemove = new JButton("Remove Object");
		final GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.gridx = 0;
		gbc_btnRemove.gridy = 1;
		panel_1.add(btnRemove, gbc_btnRemove);

		final JScrollPane scrollPane_5 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_5 = new GridBagConstraints();
		gbc_scrollPane_5.gridheight = 2;
		gbc_scrollPane_5.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_5.gridx = 1;
		gbc_scrollPane_5.gridy = 0;
		panel_1.add(scrollPane_5, gbc_scrollPane_5);

		hintList.setModel(new DefaultListModel());
		hintList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {

				if (hintList.getSelectedValue() != null) {
					final StringSelection clip = new StringSelection(
							((ClipboardHintObject) hintList.getSelectedValue()).content);
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(clip, null);
				}
			}
		});
		scrollPane_5.setViewportView(hintList);

		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				if (objectList.getSelectedValue() != null) {
					projectionPanel.remove((EditorObject) objectList
							.getSelectedValue());
				}
			}
		});

		final JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);
		projectionPanel.setBackground(Color.BLACK);

		projectionPanel.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("ObjectList")) {

					for (final EditorObject o : projectionPanel.objectList) {
						if (!((DefaultListModel) objectList.getModel())
								.contains(o)) {

							((DefaultListModel) objectList.getModel())
									.addElement(o);

						}
					}
					updateListAndTable();
				}
			}
		});
		projectionPanel.descriptionList = objectList;
		scrollPane_2.setViewportView(projectionPanel);
		updateSize();

	}

	public TableEditor(final Collection<File> loadEditorObjectClasses) {

		this();
		for (final File load : loadEditorObjectClasses) {
			loadEditorObjectClassesFromFile(load);
		}
	}

	public void createCode() {

		String s = "public void loadObjectsToUniverse(Universe parentUniverse) {\n\n";
		int i = 0;
		for (final EditorObject o : projectionPanel.getAllObjects()) {
			i++;
			s += o.toCode(i) + "\n";
		}
		s += "\n}\n";
		codeTextArea.setText(s);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(s), null);

	}

	public boolean loadEditorObjectClassesFromFile(final File loadFile) {

		if ((loadFile != null) && loadFile.exists()) {
			try {
				final BufferedReader br = new BufferedReader(new FileReader(
						loadFile));
				String object = br.readLine();
				while (object != null) {
					if (object.contains("<>")) {
						final String[] s = object.split("<>");
						final String className = s[0];
						((DefaultListModel) classList.getModel())
								.addElement(new EditorObjectDescription(
										className, object));
					}
					object = br.readLine();
				}
				br.close();
				return true;
			} catch (final FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	private void updateListAndTable() {

		// Update List

		final DefaultListModel dlm = ((DefaultListModel) objectList.getModel());
		final ArrayList<EditorObject> removeList = new ArrayList<EditorObject>();
		for (int i = 0; i < dlm.getSize(); i++) {
			boolean found = false;
			for (final EditorObject o : projectionPanel.getAllObjects()) {
				if (o.equals(dlm.get(i))) {
					found = true;
				}
			}
			if (!found) {
				removeList.add((EditorObject) dlm.get(i));
			}
		}
		for (final EditorObject removeMe : removeList) {
			dlm.removeElement(removeMe);
		}

		// Update Table -> ONLY DELETE AND ADD ALL ROWS! THE UPDATE METHOD HAS A
		// LISTENER THAT RESULTS IN A STACK OVERFLOW!!!
		boolean found = false;
		for (final EditorObject o : projectionPanel.getAllObjects()) {
			if (o.equals(selectedObject)) {
				found = true;
			}
		}
		if (!found) {

			((DefaultTableModel) propertiesTable.getModel()).setRowCount(0);
			selectedObject = null;

		}
		if (objectList.getSelectedValue() != null) {

			// Load the new selected object
			((DefaultTableModel) propertiesTable.getModel()).setRowCount(0);
			final EditorObject object = ((EditorObject) objectList
					.getSelectedValue());
			for (final String[] newRow : object.getPropertyTable()) {
				((DefaultTableModel) propertiesTable.getModel()).addRow(newRow);
			}
			((DefaultListModel) hintList.getModel()).clear();
			for (final String hint : object.hint.split("#")) {
				if (hint.contains(":")) {
					((DefaultListModel) hintList.getModel())
							.addElement(new ClipboardHintObject(hint.substring(
									0, hint.indexOf(":")), hint.substring(hint
									.indexOf(":") + 1)));
				}

			}
			selectedObject = object;
		} else {
			selectedObject = null;
		}

	}

	public void updateSize() {

		try {

			projectionPanel.view_width = Integer.parseInt(widthField.getText());
			projectionPanel.view_height = Integer.parseInt(heightField
					.getText());
			projectionPanel.zoom = ((double) zoomSlider.getValue()) / 100;
			projectionPanel.gridSquare = Integer.parseInt(gridField.getText());
			final int actualWidth = (int) (projectionPanel.view_width * projectionPanel.zoom);
			final int actualHeight = (int) (projectionPanel.view_height * projectionPanel.zoom);
			projectionPanel.setPreferredSize(new Dimension(actualWidth,
					actualHeight));
			projectionPanel.setSize(new Dimension(actualWidth, actualHeight));
			projectionPanel.repaint();
		} catch (final Exception e) {
			// e.printStackTrace();
		}

	}
}
