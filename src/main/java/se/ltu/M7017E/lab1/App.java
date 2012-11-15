package se.ltu.M7017E.lab1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;

import org.gstreamer.Bus;
import org.gstreamer.GstObject;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

public class App {
	private static final String TEMP_RECORDING_FILE = "tmp.ogg";

	@Getter
	private Recorder recorder = new Recorder();
	@Getter
	private PlayBin2 player = new PlayBin2("player");
	@Getter
	private Settings settings = new Settings();

	/**
	 * Date formatter for recording filenames
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
		System.out.println("start recording");

		recorder.setOutputFilename(settings.getRecordingFolder()
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
		System.out.println("stop recording");

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
	 * Suppress a file
	 * 
	 * @param file
	 *            filename
	 */
	public void discardRecording(String filename){
		File file= new File(filename);
		file.delete();
	}

	/**
	 * Play audio file from filename
	 * 
	 * @param file
	 *            filename
	 */
	public void startPlayer(String file) {
		System.out.println("playing " + file);

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
