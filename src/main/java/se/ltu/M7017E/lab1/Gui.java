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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

import org.gstreamer.Format;
import org.gstreamer.swing.PipelinePositionModel;

public class Gui extends JFrame {
	private static final long serialVersionUID = 4170395611124108634L;

	private App app;

	private JSlider slider;
	private JLabel timeLbl;
	private JList filesLst;
	@Getter
	private DefaultListModel filesLstModel;

	public Gui() {
		this.app = new App();
		this.setTitle("Audio Recorder");

		this.setSize(300, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

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

	private void updateTimeLbl() {
		long position = app.getRecorderPipe().queryPosition(Format.TIME);
		position = position / 1000000000L;
		timeLbl.setText(String.format("%d:%02d:%02d", position / 3600,
				(position % 3600) / 60, position % 60));
	}

	private JPanel createSlider() {
		JPanel panel = new JPanel();

		this.slider = new JSlider(0, 100);
		slider.setModel(new PipelinePositionModel(app.getRecorderPipe()));
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

		File currDir = new File(".");
		File[] oggFiles = currDir.listFiles(new OggFilter());

		for (File file : oggFiles) {
			this.filesLstModel.addElement(file.getName());
		}
		this.filesLst = new JList(filesLstModel);
		JScrollPane scrollPane = new JScrollPane(this.filesLst);

		panel.add(scrollPane);
		panel.setPreferredSize(new Dimension(300, 400));

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

				if(!app.isRecording()){
					filesLstModel.addElement(app.record());
				}else{
					app.record();
				}
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

		buttons.add(recBtn);
		buttons.add(new JButton("PLAY"));

		return buttons;
	}

	private class OggFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith("ogg");
		}
	}
}
