package se.ltu.M7017E.lab1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;

import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

public class App {
	private static final String TEMP_RECORDING_FILE = "tmp.ogg";

	@Getter
	private Recorder recorder = new Recorder();
	@Getter
	private PlayBin2 player = new PlayBin2("player");

	/**
	 * Date formatter for recording filenames
	 */
	private SimpleDateFormat filenameFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public App() {
		recorder.setOutputFilename(TEMP_RECORDING_FILE);
	}

	/**
	 * Starts the recording, automatically generating a new filename
	 * 
	 * @return the generated filename, including extension
	 */
	public void startRecording() {
		System.out.println("start recording");

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
	 * {@link #stopRecording()
	 * 
	 * @param filename
	 *            filename (extension, ie ".ogg", must be included)
	 */
	public void renameLastRecording(String filename) {
		new File(TEMP_RECORDING_FILE).renameTo(new File(filename));
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

		this.player.setInputFile(new File(file));
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
	 * @return the filename, including extension
	 */
	public String genNewFileName() {
		return filenameFormatter.format(new Date()) + ".ogg";
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
