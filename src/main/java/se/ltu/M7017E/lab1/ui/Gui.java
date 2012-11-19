package se.ltu.M7017E.lab1.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gstreamer.Bus;
import org.gstreamer.Format;
import org.gstreamer.GstObject;
import org.gstreamer.Pipeline;
import org.gstreamer.swing.PipelinePositionModel;

import se.ltu.M7017E.lab1.App;

/**
 * Main window for the app.
 */
public class Gui extends JFrame {
	private static final long serialVersionUID = 4170395611124108634L;

	private App app;

	// UI elements for easy access (in event handlers e.g.)
	private JMenuBar menu;
	private JSlider slider;
	private JLabel timeLbl;
	private JList filesLst;
	private JButton recBtn;
	private JButton playBtn; // is also the pause button
	private String selection; // file selected for play
	private DefaultListModel filesLstModel;
	private PipelinePositionModel playerPositionModel;
	private PipelinePositionModel recorderPositionModel;

	// icons
	private ImageIcon recordIcon = new ImageIcon(getClass().getResource(
			"/icons/record.png"));
	private ImageIcon recordDisabledIcon = new ImageIcon(getClass()
			.getResource("/icons/record_disabled.png"));
	private ImageIcon playIcon = new ImageIcon(getClass().getResource(
			"/icons/play.png"));
	private ImageIcon pauseIcon = new ImageIcon(getClass().getResource(
			"/icons/pause.png"));
	private ImageIcon franceIcon = new ImageIcon(getClass().getResource(
			"/icons/appIcon.png"));

	public Gui(final App app) {
		// app holds the business logic of the app
		this.app = app;

		this.setTitle("Audio Recorder");
		// app icon
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/icons/appIcon.png")));

		this.setSize(300, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// use OS' native look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Slider.paintValue", false);
		} catch (ClassNotFoundException e) {
			System.err
					.println("Got a problem while trying to use OS' native look'n'feel. Reverting to Swing default.");
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err
					.println("Got a problem while trying to use OS' native look'n'feel. Reverting to Swing default.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err
					.println("Got a problem while trying to use OS' native look'n'feel. Reverting to Swing default.");
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			System.err
					.println("Got a problem while trying to use OS' native look'n'feel. Reverting to Swing default.");
			e.printStackTrace();
		}

		this.setJMenuBar(createMenu());

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(createTime());
		this.add(createControls());
		this.add(createSlider());
		this.add(createFilesList());

		// End Of Stream => change back the button to "play" action
		app.getPlayer().getBus().connect(new Bus.EOS() {
			public void endOfStream(GstObject source) {
				playBtn.setIcon(playIcon);
			}
		});
	}

	/**
	 * Create and fill the menus
	 * 
	 * @return the menu bar
	 */
	private JMenuBar createMenu() {
		this.menu = new JMenuBar();

		JMenu edit = new JMenu("Edit");
		// preferences window
		JMenuItem preferences = new JMenuItem("Preferences");
		preferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PreferencesDialog(Gui.this, app.getSettings())
						.setVisible(true);
			}
		});
		edit.add(preferences);
		menu.add(edit);

		JMenu help = new JMenu("?");
		// about box
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(Gui.this,
						"This is a wonderful AudioRecorder, really.\nMade by: THE FRENCHIES!\n\n"
								+ "· Flore Diallo\n" + "· Hervé Loeffel\n"
								+ "· Clément Notin", null,
						JOptionPane.INFORMATION_MESSAGE, franceIcon);
			}
		});
		help.add(about);
		menu.add(help);

		return menu;
	}

	/**
	 * Update the time label according to the current position in stream.
	 * Formats it to a nice HH:MM:SS string.
	 */
	private void updateTimeLbl() {
		/*
		 * it links the slider to the recording or the playing pipeline
		 * depending on the current state of the app
		 */
		Pipeline playslider = new Pipeline();
		if (app.isRecording()) {
			playslider = app.getRecorder();
		} else if (app.isPlaying()) {
			playslider = app.getPlayer();
		}

		long position = playslider.queryPosition(Format.TIME);
		// 10^9, because queryPosition gives nanoseconds
		position = position / 1000000000L;
		timeLbl.setText(String.format("%d:%02d:%02d", position / 3600,
				(position % 3600) / 60, position % 60));
	}

	/**
	 * Create and setup the slider to view and seek a position in the file.
	 * 
	 * @return the JPanel containing the slider
	 */
	private JPanel createSlider() {
		JPanel panel = new JPanel();

		this.slider = new JSlider();
		/*
		 * PipelinePositionModel: useful class from java-gstreamer, helps to
		 * keep the boundaries and the cursor in sync with the stream
		 */
		playerPositionModel = new PipelinePositionModel(app.getPlayer());
		recorderPositionModel = new PipelinePositionModel(app.getRecorder());

		/*
		 * the PipelinePositionModel automatically updates the slider (every
		 * second). We hook on this change to also update our time label
		 */
		this.slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateTimeLbl();
			}
		});

		panel.add(slider);

		return panel;
	}

	/**
	 * Create the file list (with the old recordings) in a scrollable panel, and
	 * fill it with the existing files.
	 * 
	 * @return the scrollable JPanel with the filled list
	 */
	private JPanel createFilesList() {
		JPanel panel = new JPanel();

		// with this layout the JList takes all available space
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		this.filesLst = new JList();
		JScrollPane scrollPane = new JScrollPane(this.filesLst);

		panel.add(scrollPane);
		panel.setPreferredSize(new Dimension(300, 400));
		// disable multi selection
		this.filesLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.filesLst.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// "click" event
				if (!e.getValueIsAdjusting()) { // a single click generates
												// several calls, filter them
					selection = (String) filesLst.getSelectedValue();
					app.stopPlayer();
					playBtn.setIcon(playIcon);
				}
			}
		});

		// listener for playing a doubleclicked file in the JList
		this.filesLst.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					app.stopPlayer();

					selection = (String) filesLst.getSelectedValue();
					slider.setModel(playerPositionModel);
					app.startPlayer(selection + ".ogg");
					playBtn.setIcon(pauseIcon);
				}
			}
		});

		fillFileList();

		return panel;
	}

	/**
	 * Fill JList's model with all the files in the folder. Can be conveniently
	 * called when needed to refresh it also!
	 */
	public void fillFileList() {
		this.filesLstModel = new DefaultListModel();

		// look in recording directory for recording files
		File currDir = new File(app.getSettings().getRecordingFolder());
		// only ogg files
		File[] oggFiles = currDir.listFiles(new OggFilter());

		for (File file : oggFiles) {
			String name = file.getName();
			// remove files' extension, user doesn't want to see them
			this.filesLstModel.addElement(name.substring(0, name.length() - 4));

		}
		this.filesLst.setModel(filesLstModel);
	}

	/**
	 * Create the timelabel to show the current position in the file/recording.
	 * 
	 * @return its JPanel container
	 */
	private JPanel createTime() {
		JPanel panel = new JPanel();
		this.timeLbl = new JLabel("HH:mm:ss");
		panel.add(this.timeLbl);

		return panel;
	}

	/**
	 * Create control buttons (play/pause, record...)
	 * 
	 * @return their JPanel container
	 */
	private JPanel createControls() {
		JPanel buttons = new JPanel();
		// align them next to each other horizontally
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		recBtn = new JButton(recordIcon);
		recBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// click on REC
				slider.setModel(recorderPositionModel);

				if (app.isRecording()) {
					// stop recording
					app.stopRecording();

					String defaultFilename = app.genNewFileName();

					String filename = JOptionPane.showInputDialog(Gui.this,
							"Please name your file", defaultFilename);

					if (filename == null) {
						// user pressed the cancel button
						app.discardRecording();
					} else {
						if (filename.equals("")) {
							// filename is blank but user clicked OK? Let's use
							// the default filename
							filename = defaultFilename;
						}
						app.renameLastRecording(filename);
						filesLstModel.addElement(filename);
					}

					recBtn.setIcon(recordIcon);
				} else {
					// start recording and add filename to list of files
					recBtn.setIcon(recordDisabledIcon);
					app.startRecording();
				}
			}
		});
		buttons.add(recBtn);

		playBtn = new JButton(playIcon);
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// click on play/pause

				/*
				 * "selection" is null if file list is empty and/or nothing
				 * selected then nothing to do.
				 */
				if (selection == null) {
					return;
				}

				if (app.isPlaying()) {
					app.pausePlayer();
					playBtn.setIcon(playIcon);
				} else {
					slider.setModel(playerPositionModel);
					app.startPlayer(selection + ".ogg");
					playBtn.setIcon(pauseIcon);
				}
			}
		});

		buttons.add(playBtn);

		return buttons;
	}

	/**
	 * Filename filter for OGG files
	 */
	public class OggFilter implements FilenameFilter {
		@Override
		public boolean accept(File file, String name) {
			return name.endsWith(".ogg");
		}
	}
}
