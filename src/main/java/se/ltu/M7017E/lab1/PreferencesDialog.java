package se.ltu.M7017E.lab1;

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

public class PreferencesDialog extends JDialog {
	private static final long serialVersionUID = -3904501838573517158L;

	private JLabel dirLbl = new JLabel("Recordings directory");
	private JTextField dirPath = new JTextField(15);
	private JButton browse = new JButton("Browse");
	private JLabel qualityLbl = new JLabel("Quality");
	// TODO change widget for a dropdown
	private JComboBox quality = new JComboBox();
	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	private JFileChooser filechooser = new JFileChooser();

	private final Settings settings;

	public static final QualityComboItem[] qualities = {
			new QualityComboItem(0.1f, "Lowest (~60 kbps)"),
			new QualityComboItem(0.6f, "Medium (~110 kbps)"),
			new QualityComboItem(1, "Highest (~240 kbps)") };

	public PreferencesDialog(final Gui gui, Settings settings) {
		this.settings = settings;

		this.setTitle("Preferences");
		this.setModal(true);

		// this.setSize(300, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setLayout(new SpringLayout());
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
				dispose();
			}
		});

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveSettings()) {
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
}
