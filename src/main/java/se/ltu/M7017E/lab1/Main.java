package se.ltu.M7017E.lab1;

import javax.swing.SwingUtilities;

import org.gstreamer.Gst;

import se.ltu.M7017E.lab1.ui.Gui;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome");

		args = Gst.init("Audio recorder", args);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui();
				gui.setVisible(true);
			}
		});
	}
}
