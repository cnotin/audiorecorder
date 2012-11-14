package se.ltu.M7017E.lab1;

import java.awt.Dimension;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lombok.Getter;

import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;

public class App {
	@Getter
	private Recorder recorder = new Recorder();
	@Getter
	private PlayBin2 player = new PlayBin2("player");
	private String filename;
	private JList list; 
	

	/**
	 * Date formatter for recording filenames
	 */
	private SimpleDateFormat filenameFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	/**
	 * Starts the recording, automatically generating a new filename
	 * 
	 * @return the generated filename, including extension
	 */
	public String startRecording() {
		System.out.println("start recording");

		this.filename = genNewFileName();
		recorder.setOutputFilename(filename);

		recorder.play();

		return filename;
	}

	/**
	 * Stops the recording
	 */
	public void stopRecording() {
		System.out.println("stop recording");
	
		System.out.println(this.list);
		NameChangeJDialog d = new NameChangeJDialog(this.filename,this.list);
		
		recorder.stop();
	}

	/**
	 * Play audio file from filename
	 * i
	 * @param file
	 *            filename
	 */
	public void startPlayer(String file, Boolean alreadyStarted) {
		System.out.println("playing " + file);

		if (isPlaying()) {
			player.stop();
		}
		//if the file is already started (ie in pause) we do not need it
		if(alreadyStarted==false)
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
	
	public void setList(JList list) {
		this.list=list;
	}
}
