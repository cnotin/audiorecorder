package se.ltu.M7017E.lab1;

import javax.swing.SwingUtilities;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;

public class App {
	public static void main(String[] args) {
		System.out.println("Welcome");

		args = Gst.init("Audio recorder", args);
		final Pipeline pipe = new Pipeline("recorder");
		final Element autoaudiosrc = ElementFactory.make("autoaudiosrc",
				"autoaudiosrc");
		final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
		vorbisenc.set("bitrate", 64000L);
		final Element oggmux = ElementFactory.make("oggmux", "oggmux");
		final Element filesink = ElementFactory.make("filesink", "filesink");
		filesink.set("location", "test.ogg");

		pipe.addMany(autoaudiosrc, vorbisenc, oggmux, filesink);
		Pipeline.linkMany(autoaudiosrc, vorbisenc, oggmux, filesink);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui(pipe);
				gui.setVisible(true);
			}
		});
	}
}
