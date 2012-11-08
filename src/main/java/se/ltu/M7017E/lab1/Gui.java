package se.ltu.M7017E.lab1;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

import org.gstreamer.Format;
import org.gstreamer.swing.PipelinePositionModel;

import se.ltu.M7017E.lab1.App.Action;

public class Gui extends JFrame {
	private static final long serialVersionUID = 4170395611124108634L;

	private App app;

	private JSlider slider;
	private JLabel timeLbl;
	private JList filesLst;
	private String selection; // file selected for play
	private DefaultListModel filesLstModel;
	private PipelinePositionModel playerPositionModel;
	private PipelinePositionModel recorderPositionModel;

	public Gui() {
		// app holds the business logic of the app
		this.app = new App();
		this.setTitle("Audio Recorder");

		this.setSize(300, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(null);// center window on screen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// use OS' native look'n'feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(createTime());
		this.add(createControls());
		this.add(createSlider());
		this.add(createFilesList());
	}

	/**
	 * Update the time label according to the current position in stream.
	 * Formats it to a nice HH:MM:SS string.
	 */
	private void updateTimeLbl() {
		long position = app.getRecorder().queryPosition(Format.TIME);
		// 10^9, convert from nanoseconds to second
		position = position / 1000000000L;
		timeLbl.setText(String.format("%d:%02d:%02d", position / 3600,
				(position % 3600) / 60, position % 60));
	}

	private JPanel createSlider() {
		JPanel panel = new JPanel();

		this.slider = new JSlider();
		/*
		 * PipelinePositionModel: useful class from java-gstreamer, helps to
		 * keep the boundaries and the cursor in sync with the stream
		 */
		playerPositionModel = new PipelinePositionModel(
				app.getPlayer());
		recorderPositionModel = new PipelinePositionModel(
				app.getRecorder());

		slider.setModel(recorderPositionModel);

		this.slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateTimeLbl();
			}
		});
		// TODO convertir les labels en HH:mm:ss
		// Hashtable labelTable = new Hashtable();
		// labelTable.put(new Integer(0), new JLabel("Stop"));
		// labelTable.put(new Integer(FPS_MAX / 10), new JLabel("Slow"));
		// labelTable.put(new Integer(FPS_MAX), new JLabel("Fast"));

		panel.add(slider);

		return panel;
	}

	private JPanel createFilesList() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		this.filesLstModel = new DefaultListModel();

		// look in current directory for recording files
		File currDir = new File(".");
		// only ogg files
		File[] oggFiles = currDir.listFiles(new OggFilter());

		for (File file : oggFiles) {
			this.filesLstModel.addElement(file.getName());
		}
		this.filesLst = new JList(filesLstModel);
		JScrollPane scrollPane = new JScrollPane(this.filesLst);

		panel.add(scrollPane);
		panel.setPreferredSize(new Dimension(300, 400));
		this.filesLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionListener listener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					selection = (String) filesLst.getSelectedValue();
				System.out.println(selection);
				}
				// TODO Auto-generated method stub

			}
		};
		this.filesLst.addListSelectionListener(listener);
		return panel;
	}

	private JPanel createTime() {
		JPanel panel = new JPanel();
		this.timeLbl = new JLabel("HH:mm:ss");
		panel.add(this.timeLbl);

		return panel;
	}

	/**
	 * Create control buttons (play/pause, record...)
	 */
	private JPanel createControls() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		JButton recBtn = new JButton("REC");
		recBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("record click");

				slider.setModel(recorderPositionModel);

				if (app.isRecording()) {
					// stop recording
					app.recording(Action.STOP);
				} else {
					// start recording and add filename to list of files
					filesLstModel.addElement(app.recording(Action.START));
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// NOTHING
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// NOTHING
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// NOTHING
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// NOTHING
			}
		});
		buttons.add(recBtn);

		JButton play=new JButton("PLAY");
		play.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("play " + selection);

				slider.setModel(playerPositionModel);

				app.play(selection);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		buttons.add(play);

		return buttons;
	}

	/**
	 * File filter for OGG files
	 */
	private class OggFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith("ogg");
		}
	}
}
