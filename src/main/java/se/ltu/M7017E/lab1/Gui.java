package se.ltu.M7017E.lab1;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
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

import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Gui extends JFrame {
	private static final long serialVersionUID = 4170395611124108634L;

	private Pipeline pipe;

	private JSlider slider;
	private JLabel timeLbl;
	private JList<String> filesLst;

	public Gui(Pipeline pipe) {
		this.pipe = pipe;
		this.setTitle("Audio Recorder");

		this.setSize(200, 500);
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
		this.add(createTime(), BorderLayout.NORTH);
		this.add(createControls(), BorderLayout.NORTH);
		this.add(createSlider(), BorderLayout.NORTH);
		this.add(createFilesList(), BorderLayout.CENTER);
	}

	private JPanel createSlider() {
		JPanel panel = new JPanel();

		this.slider = new JSlider(0, 100);
		this.slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = (int) source.getValue();
				timeLbl.setText(String.valueOf(value));
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

		String labels[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
		this.filesLst = new JList<String>(labels);
		JScrollPane scrollPane = new JScrollPane(this.filesLst);

		panel.add(scrollPane);

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
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("record click");

				State state = pipe.getState();
				if (state == State.PLAYING) {
					System.out.println("stop recording");
					pipe.setState(State.NULL);
				} else {
					System.out.println("start recording");
					pipe.setState(State.PLAYING);
				}
			}
		});

		buttons.add(recBtn);
		buttons.add(new JButton("PLAY"));

		return buttons;
	}
}
