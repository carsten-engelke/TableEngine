package org.engine.editor;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class EditorObjectChangeSizeAngle extends JFrame {

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
					final EditorObjectChangeSizeAngle frame = new EditorObjectChangeSizeAngle(
							new EditorObject(
									"Test<>TestName<>TestHint<>Name;Type;Content",
									new ProjectionPanel()));
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final JTextField angleField;
	private final JButton btnNewButton;
	private final JPanel contentPane;
	private final JTextField depthField;
	private final JTextField heightField;
	private final JLabel lblAngle;
	private final JLabel lblDepth;
	private final JLabel lblHeight;
	private final JLabel lblName;
	private final JLabel lblWidth;
	private final JLabel lblY;
	private final JLabel lblZ;
	private final JTextField nameField;
	private final JTextField widthField;
	private final JTextField xField;
	private final JTextField yField;

	private final JTextField zField;

	/**
	 * Create the frame.
	 */
	public EditorObjectChangeSizeAngle(final EditorObject parent) {

		setResizable(false);

		setTitle("Change Parameters");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 250, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		final GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 50, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		lblName = new JLabel("Name");
		final GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		contentPane.add(lblName, gbc_lblName);

		nameField = new JTextField();
		nameField.setText("Name");
		final GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(0, 0, 5, 0);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 1;
		gbc_nameField.gridy = 0;
		contentPane.add(nameField, gbc_nameField);
		nameField.setColumns(10);

		final JLabel lblNewLabel = new JLabel("X");
		final GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		xField = new JTextField();
		final GridBagConstraints gbc_xField = new GridBagConstraints();
		gbc_xField.insets = new Insets(0, 0, 5, 0);
		gbc_xField.fill = GridBagConstraints.HORIZONTAL;
		gbc_xField.gridx = 1;
		gbc_xField.gridy = 1;
		contentPane.add(xField, gbc_xField);
		xField.setColumns(10);

		lblY = new JLabel("Y");
		final GridBagConstraints gbc_lblY = new GridBagConstraints();
		gbc_lblY.anchor = GridBagConstraints.EAST;
		gbc_lblY.insets = new Insets(0, 0, 5, 5);
		gbc_lblY.gridx = 0;
		gbc_lblY.gridy = 2;
		contentPane.add(lblY, gbc_lblY);

		yField = new JTextField();
		final GridBagConstraints gbc_yField = new GridBagConstraints();
		gbc_yField.insets = new Insets(0, 0, 5, 0);
		gbc_yField.fill = GridBagConstraints.HORIZONTAL;
		gbc_yField.gridx = 1;
		gbc_yField.gridy = 2;
		contentPane.add(yField, gbc_yField);
		yField.setColumns(10);

		lblZ = new JLabel("Z");
		final GridBagConstraints gbc_lblZ = new GridBagConstraints();
		gbc_lblZ.insets = new Insets(0, 0, 5, 5);
		gbc_lblZ.anchor = GridBagConstraints.EAST;
		gbc_lblZ.gridx = 0;
		gbc_lblZ.gridy = 3;
		contentPane.add(lblZ, gbc_lblZ);

		zField = new JTextField();
		zField.setText("0");
		final GridBagConstraints gbc_zField = new GridBagConstraints();
		gbc_zField.insets = new Insets(0, 0, 5, 0);
		gbc_zField.fill = GridBagConstraints.HORIZONTAL;
		gbc_zField.gridx = 1;
		gbc_zField.gridy = 3;
		contentPane.add(zField, gbc_zField);
		zField.setColumns(10);

		lblWidth = new JLabel("Width");
		final GridBagConstraints gbc_lblWidth = new GridBagConstraints();
		gbc_lblWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblWidth.anchor = GridBagConstraints.EAST;
		gbc_lblWidth.gridx = 0;
		gbc_lblWidth.gridy = 4;
		contentPane.add(lblWidth, gbc_lblWidth);

		widthField = new JTextField();
		final GridBagConstraints gbc_widthField = new GridBagConstraints();
		gbc_widthField.insets = new Insets(0, 0, 5, 0);
		gbc_widthField.fill = GridBagConstraints.HORIZONTAL;
		gbc_widthField.gridx = 1;
		gbc_widthField.gridy = 4;
		contentPane.add(widthField, gbc_widthField);
		widthField.setColumns(10);

		lblHeight = new JLabel("Height");
		final GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeight.anchor = GridBagConstraints.EAST;
		gbc_lblHeight.gridx = 0;
		gbc_lblHeight.gridy = 5;
		contentPane.add(lblHeight, gbc_lblHeight);

		heightField = new JTextField();
		final GridBagConstraints gbc_heightField = new GridBagConstraints();
		gbc_heightField.insets = new Insets(0, 0, 5, 0);
		gbc_heightField.fill = GridBagConstraints.HORIZONTAL;
		gbc_heightField.gridx = 1;
		gbc_heightField.gridy = 5;
		contentPane.add(heightField, gbc_heightField);
		heightField.setColumns(10);

		lblDepth = new JLabel("Depth");
		final GridBagConstraints gbc_lblDepth = new GridBagConstraints();
		gbc_lblDepth.anchor = GridBagConstraints.EAST;
		gbc_lblDepth.insets = new Insets(0, 0, 5, 5);
		gbc_lblDepth.gridx = 0;
		gbc_lblDepth.gridy = 6;
		contentPane.add(lblDepth, gbc_lblDepth);

		depthField = new JTextField();
		depthField.setText("0");
		final GridBagConstraints gbc_depthField = new GridBagConstraints();
		gbc_depthField.insets = new Insets(0, 0, 5, 0);
		gbc_depthField.fill = GridBagConstraints.HORIZONTAL;
		gbc_depthField.gridx = 1;
		gbc_depthField.gridy = 6;
		contentPane.add(depthField, gbc_depthField);
		depthField.setColumns(10);

		lblAngle = new JLabel("Angle ( in \u00B0 )");
		final GridBagConstraints gbc_lblAngle = new GridBagConstraints();
		gbc_lblAngle.insets = new Insets(0, 0, 5, 5);
		gbc_lblAngle.anchor = GridBagConstraints.EAST;
		gbc_lblAngle.gridx = 0;
		gbc_lblAngle.gridy = 7;
		contentPane.add(lblAngle, gbc_lblAngle);

		angleField = new JTextField();
		angleField.setEditable(false);
		final GridBagConstraints gbc_angleField = new GridBagConstraints();
		gbc_angleField.insets = new Insets(0, 0, 5, 0);
		gbc_angleField.fill = GridBagConstraints.HORIZONTAL;
		gbc_angleField.gridx = 1;
		gbc_angleField.gridy = 7;
		contentPane.add(angleField, gbc_angleField);
		angleField.setColumns(10);

		btnNewButton = new JButton("Apply");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				try {
					if (parent.hasProperty("Angle")) {
						parent.setAngle(Integer.parseInt(angleField.getText()));
					}
					parent.setX(Integer.parseInt(xField.getText()));
					parent.setY(Integer.parseInt(yField.getText()));
					parent.setZ(Integer.parseInt(zField.getText()));
					parent.setWidth(Integer.parseInt(widthField.getText()));
					parent.setHeight(Integer.parseInt(heightField.getText()));
					parent.setDepth(Integer.parseInt(depthField.getText()));
					parent.setName("\"" + nameField.getText() + "\"");
					parent.parent.updateObjects(this);
					dispose();
				} catch (final Exception x) {
					x.printStackTrace();
				}

			}
		});
		final GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 8;
		contentPane.add(btnNewButton, gbc_btnNewButton);

		loadEditorObject(parent);
	}

	private void loadEditorObject(final EditorObject parent) {

		nameField.setText(parent.name);
		xField.setText(String.valueOf(parent.getX()));
		yField.setText(String.valueOf(parent.getY()));
		widthField.setText(String.valueOf(parent.getWidth()));
		heightField.setText(String.valueOf(parent.getHeight()));
		depthField.setText(String.valueOf(parent.getDepth()));
		if (parent.hasProperty("Angle")) {
			angleField.setEditable(true);
			angleField.setText(String.valueOf(Math.toDegrees(parent.angle)));
		}

	}

}
