package se.ltu.M7017E.lab1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;

import org.gstreamer.Bus;
import org.gstreamer.GstObject;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

/**
 * Main business class with the API used by the GUI.
 */
public class App {
	/**
	 * Name of the temp recording file.
	 */
	private static final String TEMP_RECORDING_FILE = "tmp.ogg";

	/**
	 * GStreamer stuff for recording
	 */
	@Getter
	private Recorder recorder = new Recorder();

	/**
	 * GStreamer stuff for playing
	 */
	@Getter
	private PlayBin2 player = new PlayBin2("player");

	/**
	 * Settings utility-object
	 */
	@Getter
	private Settings settings = new Settings();

	/**
	 * Date formatter for default recording filenames
	 */
	private SimpleDateFormat filenameFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public App() {
		// redirect error and warning messages to our logger
		player.getBus().connect(new Bus.ERROR() {
			@Override
			public void errorMessage(GstObject source, int code, String message) {
				logErrorMessages(code, message);
			}
		});
		player.getBus().connect(new Bus.WARNING() {
			@Override
			public void warningMessage(GstObject source, int code,
					String message) {
				logErrorMessages(code, message);
			}
		});
	}

	/**
	 * Log to the console the message from GStreamer (or another component...)
	 * 
	 * @param code
	 *            message code
	 * @param message
	 *            message string
	 */
	private void logErrorMessages(int code, String message) {
		System.err.println("Received msg code=" + code + " with message \""
				+ message + "\"");
	}

	/**
	 * Starts the recording to the temp file
	 * 
	 */
	public void startRecording() {
		recorder.setOutputFile(settings.getRecordingFolder()
				+ File.separator + TEMP_RECORDING_FILE);
		recorder.setQuality(settings.getQuality());
		recorder.play();
	}

	/**
	 * Stops the recording. Must call {@link #renameLastRecording(String)} after
	 * to change temp file name (otherwise the recording will be overwritten by
	 * next one).
	 */
	public void stopRecording() {
		recorder.stop();
	}

	/**
	 * Rename last recording file. Usually called just after
	 * {@link #stopRecording()}
	 * 
	 * @param filename
	 *            filename WITHOUT extension (ie ".ogg")
	 */
	public void renameLastRecording(String filename) {
		filename = filename + ".ogg";
		new File(settings.getRecordingFolder() + File.separator
				+ TEMP_RECORDING_FILE).renameTo(new File(settings
				.getRecordingFolder() + File.separator + filename));
	}

	/**
	 * Discard the current recording (finally the use doesn't want it...)
	 */
	public void discardRecording() {
		new File(TEMP_RECORDING_FILE).delete();
	}

	/**
	 * Play audio file from filename
	 * 
	 * @param file
	 *            filename, with extension and without the path (it's relative
	 *            to the recording folder)
	 */
	public void startPlayer(String file) {
		if (isPlaying()) {
			player.stop();
		}

		this.player.setInputFile(new File(settings.getRecordingFolder()
				+ File.separator + file));
		this.player.play();
	}

	/**
	 * Pauses the player.
	 */
	public void pausePlayer() {
		this.player.pause();
	}

	/**
	 * Stops player.
	 */
	public void stopPlayer() {
		this.player.stop();
	}

	/**
	 * Generate a filename for a new recording based on the date.
	 * 
	 * @return the filename, WITHOUT extension
	 */
	public String genNewFileName() {
		return filenameFormatter.format(new Date());
	}

	/**
	 * Is the recorder recording?
	 */
	public boolean isRecording() {
		return (recorder.getState() == State.PLAYING);
	}

	/**
	 * Is the player playing?
	 */
	public boolean isPlaying() {
		return (player.getState() == State.PLAYING);
	}

	/**
	 * Is the player paused?
	 */
	public boolean playerIsPaused() {
		return (player.getState() == State.PAUSED);
	}
}
