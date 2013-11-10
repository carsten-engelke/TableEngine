package org.engine.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class ResourcePacker {

	protected Shell shlTexturePacker;
	private Text assets;
	private Text images;
	private Text ui;
	private Text data;
	private Text output;
	private Label lblSounds;
	private Text sounds;

	String[] cols = new String[] { "dark", "light" };
	String[] sizes = new String[] { "ldpi", "mdpi", "hdpi", "xhdpi" };

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ResourcePacker window = new ResourcePacker();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlTexturePacker.open();
		shlTexturePacker.layout();
		while (!shlTexturePacker.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlTexturePacker = new Shell();
		shlTexturePacker.setSize(450, 300);
		shlTexturePacker.setText("Texture Packer");
		shlTexturePacker.setLayout(new GridLayout(2, false));

		Label lblAssetsfolder = new Label(shlTexturePacker, SWT.NONE);
		lblAssetsfolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblAssetsfolder.setText("Assets-Folder");

		assets = new Text(shlTexturePacker, SWT.BORDER);
		assets.setText("C:/Users/Carsten/EngineWorkspace/TableEngine/assets");
		assets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblUiresources = new Label(shlTexturePacker, SWT.NONE);
		lblUiresources.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblUiresources.setText("Picture-Resources");

		images = new Text(shlTexturePacker, SWT.BORDER);
		images.setText("C:/Users/Carsten/EngineWorkspace/TableEngine/images");
		images.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblPictureresources = new Label(shlTexturePacker, SWT.NONE);
		lblPictureresources.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblPictureresources.setText("UI-Resources");

		ui = new Text(shlTexturePacker, SWT.BORDER);
		ui.setText("C:/Users/Carsten/EngineWorkspace/TableEngine/ui");
		ui.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblData = new Label(shlTexturePacker, SWT.NONE);
		lblData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblData.setText("Data");

		data = new Text(shlTexturePacker, SWT.BORDER);
		data.setText("C:/Users/Carsten/EngineWorkspace/TableEngine/data");
		data.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblSounds = new Label(shlTexturePacker, SWT.NONE);
		lblSounds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSounds.setText("Sounds");

		sounds = new Text(shlTexturePacker, SWT.BORDER);
		sounds.setText("C:/Users/Carsten/EngineWorkspace/TableEngine/sounds");
		sounds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Button btnPack = new Button(shlTexturePacker, SWT.NONE);
		btnPack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean errorDetected = false;

				// CREATE ASSETS DIR
				try {
					new FileHandle(assets.getText() + "/"
							+ BasicResource.DESCRIPTION_ID + "/").mkdirs();
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}
				// REMOVE OLD FILES
				output.append("REMOVE OLD FILES...");
				FileHandle f = new FileHandle(assets.getText() + "/"
						+ BasicResource.DESCRIPTION_ID);
				try {
					for (final FileHandle removeMe : f.list()) {
						removeMe.delete();
					}
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}
				output.append("done\nPacking images...");
				// PACK IMAGES
				final Settings s = new Settings();
				try {
					s.maxWidth = 1024;
					s.maxHeight = 1024;
					TexturePacker2.process(images.getText(), assets.getText()
							+ "/" + BasicResource.DESCRIPTION_ID, "pics");
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}
				// PACK UI
				output.append("done\nPacking UI...");
				try {
					for (String col : cols) {
						for (String size : sizes) {
							s.maxWidth = 256;
							TexturePacker2.process(ui.getText() + "/" + col
									+ "/drawable-" + size, assets.getText()
									+ "/" + BasicResource.DESCRIPTION_ID, "ui-"
									+ col + "-" + size);
						}
					}
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}
				output.append("done\nCopying JSON Files...");
				try {
					for (String col : cols) {
						for (String size : sizes) {
							BufferedReader br = new BufferedReader(
									new FileReader(ui.getText() + "/ui.json"));
							FileWriter fw = new FileWriter(assets.getText()
									+ "/ui-" + col + "-" + size + ".json");
							String line = br.readLine();
							while (line != null) {

								fw.write(line.replace("<col>", col).replace(
										"<size>", size)
										+ "\n");
								line = br.readLine();
							}
							fw.close();
							br.close();
						}
					}
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}
				output.append("done\nCopying Sounds...");
				// PACK SOUNDS

				try {
					f = new FileHandle(sounds.getText());
					for (final FileHandle file : f.list()) {

						output.append(BasicResource.DESCRIPTION_ID + "/"
								+ file.name() + ", ");
						file.copyTo(new FileHandle(assets.getText() + "/"
								+ BasicResource.DESCRIPTION_ID + "/"
								+ file.name()));
					}
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}

				output.append("done\nPacking Data...");
				// PACK DATA

				try {
					f = new FileHandle(data.getText());
					for (final FileHandle file : f.list()) {

//						output.append(BasicResource.DESCRIPTION_ID + "/"
//								+ file.name());
						file.copyTo(new FileHandle(assets.getText() + "/"
								+ BasicResource.DESCRIPTION_ID + "/"
								+ file.name()));
					}
				} catch (Exception e2) {
					output.append("\nERROR: " + e2.getMessage());
					errorDetected = true;
				}

				output.append("done");
				if (!errorDetected) {
					output.append("\nPACKING COMPLETED WITHOUT ERRORS");
				} else {
					output.append("\n!!!ATTENTION: ERROR DETECTED");
				}
			}
		});
		btnPack.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		btnPack.setText("Pack");

		output = new Text(shlTexturePacker, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		output.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

	}

}
