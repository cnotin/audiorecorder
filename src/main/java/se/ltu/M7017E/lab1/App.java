package se.ltu.M7017E.lab1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

public class App {
	@Getter
	private Pipeline recorder;
	@Getter
	private PlayBin2 player;

	/**
	 * Date formatter for recording filenames
	 */
	private SimpleDateFormat filenameFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public static enum Action {
		START, STOP
	};

	public App() {
		createRecorder();
		createPlayer();
	}

	/**
	 * Create all GStreamer stuff for recording
	 */
	private void createRecorder() {
		recorder = new Pipeline("recorder");
		final Element autoaudiosrc = ElementFactory.make("autoaudiosrc",
				"autoaudiosrc");
		final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
		vorbisenc.set("bitrate", 64000L);
		final Element oggmux = ElementFactory.make("oggmux", "oggmux");
		final Element filesink = ElementFactory.make("filesink", "filesink");

		recorder.addMany(autoaudiosrc, vorbisenc, oggmux, filesink);
		Pipeline.linkMany(autoaudiosrc, vorbisenc, oggmux, filesink);
	}

	/**
	 * Create all GStreamer stuff for playing
	 */
	private void createPlayer() {
		player = new PlayBin2("player");
	}

	public String recording(Action action) {
		String filename = null;

		if (action == Action.STOP) {
			System.out.println("stop recording");

			recorder.setState(State.NULL);
		} else if (action == Action.START) {
			System.out.println("start recording");

			filename = genNewFileName();
			recorder.getElementByName("filesink").set("location", filename);

			recorder.setState(State.PLAYING);
		}

		return filename;
	}

	/**
	 * Play audio file from filename
	 * 
	 * @param file
	 *            filename
	 */
	public void play(String file) {
		System.out.println("playing " + file);

		if (isPlaying()) {
			player.stop();
		}

		this.player.setInputFile(new File(file));
		this.player.play();
	}

	/**
	 * Generate a filename for a new recording based on the date.
	 * 
	 * @return the filename, including extension
	 */
	public String genNewFileName() {
		return filenameFormatter.format(new Date()) + ".ogg";
	}

	/**
	 * Are we currently recording?
	 */
	public boolean isRecording() {
		return (recorder.getState() == State.PLAYING);
	}

	/**
	 * Are we currently recording?
	 */
	public boolean isPlaying() {
		return (player.getState() == State.PLAYING);
	}
}
