package org.engine.editor;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CreateEditableStrings extends JFrame {

	/**
	 * 1 = Release version
	 */
	private static final long serialVersionUID = 1L;
	static String[] titles = new String[] { "Name", "Type", "Content" };

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				try {
					final CreateEditableStrings frame = new CreateEditableStrings();
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final JTextField codeField;
	TreeMap<String, String> contentAssist = new TreeMap<String, String>();
	private final JPanel contentPane;
	private final JTextField hintField;
	private final JList list;
	private final JTextField nameField;
	private final JTable table;

	private final JTextField typeField;

	/**
	 * Create the frame.
	 */
	public CreateEditableStrings() {

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				CreateEditableStrings.class
						.getResource("/resources/basic/jb.png")));

		setTitle("Create Strings Tool");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 551, 505);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		final GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		final JLabel lblCanonicalName = new JLabel("Canonical Name");
		final GridBagConstraints gbc_lblCanonicalName = new GridBagConstraints();
		gbc_lblCanonicalName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCanonicalName.gridx = 0;
		gbc_lblCanonicalName.gridy = 0;
		contentPane.add(lblCanonicalName, gbc_lblCanonicalName);

		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridheight = 3;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 0;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);

		list = new JList();
		list.setModel(new DefaultListModel());
		scrollPane_1.setViewportView(list);

		typeField = new JTextField();
		final GridBagConstraints gbc_typeField = new GridBagConstraints();
		gbc_typeField.insets = new Insets(0, 0, 5, 5);
		gbc_typeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeField.gridx = 0;
		gbc_typeField.gridy = 1;
		contentPane.add(typeField, gbc_typeField);
		typeField.setColumns(10);

		final JButton btnCreateStrings = new JButton("List Constructors");
		btnCreateStrings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final String className = typeField.getText();
				try {
					final Class<?> c = this.getClass().getClassLoader()
							.loadClass(className);
					((DefaultListModel) list.getModel()).clear();
					for (final Constructor<?> construct : c.getConstructors()) {
						((DefaultListModel) list.getModel())
								.addElement(construct);

					}
				} catch (final ClassNotFoundException e1) {
					((DefaultListModel) list.getModel()).clear();
				}
			}
		});
		final GridBagConstraints gbc_btnCreateStrings = new GridBagConstraints();
		gbc_btnCreateStrings.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateStrings.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateStrings.gridx = 0;
		gbc_btnCreateStrings.gridy = 2;
		contentPane.add(btnCreateStrings, gbc_btnCreateStrings);

		final JButton btnFillTable = new JButton("Fill Table");
		btnFillTable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				if (!list.isSelectionEmpty()) {
					((DefaultTableModel) table.getModel()).setRowCount(0);
					int number = 0;
					boolean containsDouble = false;
					for (final Class<?> c : ((Constructor<?>) list
							.getSelectedValue()).getParameterTypes()) {
						number++;
						String type = c.getCanonicalName();
						if (type.contains(".")) {
							type = type.substring(type.lastIndexOf(".") + 1);
						}
						for (final TypeVariable<?> para : c.getTypeParameters()) {
							type = type + "<" + para.getName() + ">";
						}
						String content = "null";
						for (final String key : contentAssist.keySet()) {
							if (type.equals(key)) {
								if (key.equals("double")) {
									containsDouble = true;
								}
								content = contentAssist.get(key);
							}
						}
						String name = "Name";
						if (type.equals("Cube")) {
							name = "Size";
						}
						if ((number == 2) && type.equals("String")) {
							name = "UniqueID";
							content = typeField.getText().toLowerCase();
							if (content.contains(".")) {
								content = content.substring(content
										.lastIndexOf(".") + 1);
							}
							content = "\"" + content + "\"";
						}
						if ((number == 3) && type.equals("int")) {
							name = "Grid";
						}

						((DefaultTableModel) table.getModel())
								.addRow(new String[] { name, type, content });
					}
					if (containsDouble) {
						hintField
								.setText("0°:0.0D#90°:1.5707963267948966D#180°:3.141592653589793D#270°:4.71238898038469D");
					}
					String type = typeField.getText();
					if (type.contains(".")) {
						type = type.substring(type.lastIndexOf(".") + 1);
					}
					nameField.setText(type.toLowerCase());
				}
			}
		});
		final GridBagConstraints gbc_btnFillTable = new GridBagConstraints();
		gbc_btnFillTable.gridwidth = 2;
		gbc_btnFillTable.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnFillTable.insets = new Insets(0, 0, 5, 0);
		gbc_btnFillTable.gridx = 0;
		gbc_btnFillTable.gridy = 3;
		contentPane.add(btnFillTable, gbc_btnFillTable);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);

		table = new JTable();
		table.setModel(new DefaultTableModel(CreateEditableStrings.titles, 0));
		scrollPane.setViewportView(table);

		final JButton btnCreateOutput = new JButton("Create Output");
		btnCreateOutput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				String props = "";
				for (int i = 0; i < table.getRowCount(); i++) {
					props += "<>" + table.getValueAt(i, 0) + ";"
							+ table.getValueAt(i, 1) + ";"
							+ table.getValueAt(i, 2);
				}
				String type = typeField.getText();
				if (type.contains(".")) {
					type = type.substring(type.lastIndexOf(".") + 1);
				}
				final String name = nameField.getText();
				final String hint = hintField.getText();
				codeField.setText(new EditorObject(type + "<>" + name + "<>"
						+ hint + props, null).saveToString());
				Toolkit.getDefaultToolkit()
						.getSystemClipboard()
						.setContents(new StringSelection(codeField.getText()),
								null);
			}
		});

		final JLabel lblName = new JLabel("Standard Name in Editor");
		final GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 5;
		contentPane.add(lblName, gbc_lblName);

		final JLabel lblHintString = new JLabel("Hint String");
		final GridBagConstraints gbc_lblHintString = new GridBagConstraints();
		gbc_lblHintString.insets = new Insets(0, 0, 5, 0);
		gbc_lblHintString.gridx = 1;
		gbc_lblHintString.gridy = 5;
		contentPane.add(lblHintString, gbc_lblHintString);

		nameField = new JTextField();
		nameField.setText("<Unnamed>");
		final GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(0, 0, 5, 5);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 0;
		gbc_nameField.gridy = 6;
		contentPane.add(nameField, gbc_nameField);
		nameField.setColumns(10);

		hintField = new JTextField();
		hintField
				.setToolTipText("Use '#' for new Hint\r\nHint format: [Caption]:[Content]\r\n(Will be displayed in hintList in Editor)");
		final GridBagConstraints gbc_hintField = new GridBagConstraints();
		gbc_hintField.insets = new Insets(0, 0, 5, 0);
		gbc_hintField.fill = GridBagConstraints.HORIZONTAL;
		gbc_hintField.gridx = 1;
		gbc_hintField.gridy = 6;
		contentPane.add(hintField, gbc_hintField);
		hintField.setColumns(10);
		final GridBagConstraints gbc_btnCreateOutput = new GridBagConstraints();
		gbc_btnCreateOutput.fill = GridBagConstraints.BOTH;
		gbc_btnCreateOutput.insets = new Insets(0, 0, 0, 5);
		gbc_btnCreateOutput.gridx = 0;
		gbc_btnCreateOutput.gridy = 7;
		contentPane.add(btnCreateOutput, gbc_btnCreateOutput);

		final JScrollPane scrollPane_2 = new JScrollPane();
		final GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 7;
		contentPane.add(scrollPane_2, gbc_scrollPane_2);

		codeField = new JTextField();
		scrollPane_2.setViewportView(codeField);
		codeField.setColumns(10);

		contentAssist.put("int", "0");
		contentAssist.put("String", "\"\"");
		contentAssist.put("double", "0.0D");
		contentAssist.put("float", "0.0F");
		contentAssist.put("int[]", "new int[]{0}");
		contentAssist.put("String[]", "new String[]{\"\"}");
		contentAssist.put("Cube", "\"[0,0,0,100,100,100]\"");
		contentAssist.put("boolean", "false");

	}

}
