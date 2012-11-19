package se.ltu.M7017E.lab1.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import se.ltu.M7017E.lab1.Settings;

/**
 * Preferences window
 */
public class PreferencesDialog extends JDialog {
	private static final long serialVersionUID = -3904501838573517158L;

	private JLabel dirLbl = new JLabel("Recordings directory");
	// 15 = preferred width, not a limit
	private JTextField dirPath = new JTextField(15);
	private JButton browse = new JButton("Browse");
	private JLabel qualityLbl = new JLabel("Quality");
	private JComboBox quality = new JComboBox();
	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	private JFileChooser filechooser = new JFileChooser();

	/**
	 * The API for this window
	 */
	private final Settings settings;

	/**
	 * Available qualities to choose in the dropdown.
	 */
	public static final QualityComboItem[] qualities = {
			new QualityComboItem(0.1f, "Lowest (~60 kbps)"),
			new QualityComboItem(0.6f, "Medium (~110 kbps)"),
			new QualityComboItem(1, "Highest (~240 kbps)") };

	public PreferencesDialog(final Gui gui, Settings settings) {
		this.settings = settings;

		this.setTitle("Preferences");
		this.setModal(true);

		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.add(dirLbl);
		dirLbl.setLabelFor(dirPath);

		JPanel fileChooserPanel = new JPanel();
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooserPanel.add(dirPath);
		fileChooserPanel.add(browse);

		this.add(fileChooserPanel);
		this.add(qualityLbl);
		qualityLbl.setLabelFor(quality);
		this.add(quality);
		this.add(cancel);
		this.add(save);

		// button to call when 'enter' is pressed
		this.getRootPane().setDefaultButton(save);

		// good layout for forms
		this.getContentPane().setLayout(new SpringLayout());
		SpringUtilities
				.makeCompactGrid(this.getContentPane(), 3, 2, 6, 6, 6, 6);

		this.pack();

		// retrieve settings' value for quality and select it
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(qualities);
		quality.setModel(comboBoxModel);
		/*
		 * get the index in the model of the quality currently saved in the
		 * settings
		 */
		this.quality.setSelectedIndex(findComboItemIndexByValue(settings
				.getQuality()));

		// retrieve settings' value for recording folder and show it
		dirPath.setText(settings.getRecordingFolder());

		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int retVal = filechooser.showOpenDialog(PreferencesDialog.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					dirPath.setText(filechooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// destroy window, don't do anything else
				dispose();
			}
		});

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveSettings()) { // try to save (true if valid settings)
					gui.fillFileList();
					dispose();
				}
			}
		});
	}

	/**
	 * Check the settings, if they are good they are saved otherwise they aren't
	 * 
	 * @return true if settings are ok and saved (then this frame can dispose
	 *         for example), false if one of the settings is incorrect
	 */
	private boolean saveSettings() {
		if (new File(dirPath.getText()).isDirectory()) {
			/*
			 * Path is OK. Don't need to check quality because it's a combobox
			 * with only valid values
			 */
			settings.setRecordingFolder(dirPath.getText());
			settings.setQuality(((QualityComboItem) quality.getSelectedItem())
					.getValue());
			return true;
		} else {
			// user manually typed a wrong folder
			JOptionPane.showMessageDialog(this,
					"The specified folder doesn't exist. Retry",
					"Settings error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * Find the index of the previously saved quality value in the JListModel to
	 * select it.
	 * 
	 * @param value
	 *            quality setting value
	 * @return the index from 0 to (size-1) or (-1) if not found
	 */
	private int findComboItemIndexByValue(float value) {
		int index = 0;
		for (QualityComboItem item : qualities) {
			if (Float.compare(item.getValue(), value) == 0) {
				return index;
			}
			index++;
		}

		return -1;
	}

	/**
	 * Idea stolen from http://stackoverflow.com/a/5661669 because I wanted "Is
	 * it possible to set a value and a label to a JComboBox so I can show a
	 * label but get a value that is different?"
	 */
	@Getter
	@Setter
	@AllArgsConstructor
	private static class QualityComboItem {
		private float value;
		private String label;

		@Override
		public String toString() {
			return label;
		}
	}
}
