package se.ltu.M7017E.lab1;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class App {
	@Getter
	private Pipeline recorderPipe;
	@Getter
	private Pipeline playPipe;

	private SimpleDateFormat filenameFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public static enum Action {
		START, STOP
	};

	public App() {
		createRecorder();
	}

	/**
	 * Create all GStreamer stuff for recording
	 */
	private void createRecorder() {
		recorderPipe = new Pipeline("recorder");
		playPipe = new Pipeline("player");
		final Element autoaudiosrc = ElementFactory.make("autoaudiosrc",
				"autoaudiosrc");
		final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
		vorbisenc.set("bitrate", 64000L);
		final Element oggmux = ElementFactory.make("oggmux", "oggmux");
		final Element filesink = ElementFactory.make("filesink", "filesink");

		recorderPipe.addMany(autoaudiosrc, vorbisenc, oggmux, filesink);
		Pipeline.linkMany(autoaudiosrc, vorbisenc, oggmux, filesink);
	}

	public String recording(Action action) {
		String filename = null;

		if (action == Action.STOP) {
			System.out.println("stop recording");
			
			recorderPipe.setState(State.NULL);
		} else if (action == Action.START) {
			System.out.println("start recording");
			
			filename = genNewFileName();
			recorderPipe.getElementByName("filesink").set("location", filename);

			recorderPipe.setState(State.PLAYING);
		}

		return filename;
	}

	/**
	 * for playing an audio file
	 */
	public void play(String file) {
		System.out.println("playing " + file);
	}

	public String genNewFileName() {
		return filenameFormatter.format(new Date()) + ".ogg";
	}

	public boolean isRecording() {
		return (recorderPipe.getState() == State.PLAYING);
	}

}
