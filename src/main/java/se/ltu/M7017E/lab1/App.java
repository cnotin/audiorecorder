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
	final Pipeline recorderPipe;

	private boolean isRecording = false;

	public App() {
		recorderPipe = new Pipeline("recorder");
		final Element autoaudiosrc = ElementFactory.make("autoaudiosrc",
				"autoaudiosrc");
		final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
		vorbisenc.set("bitrate", 64000L);
		final Element oggmux = ElementFactory.make("oggmux", "oggmux");
		final Element filesink = ElementFactory.make("filesink", "filesink");

		recorderPipe.addMany(autoaudiosrc, vorbisenc, oggmux, filesink);
		Pipeline.linkMany(autoaudiosrc, vorbisenc, oggmux, filesink);
	}

	public String record() {
		String filename = null;

		if (isRecording) {
			System.out.println("stop recording");
			
			recorderPipe.setState(State.NULL);
			isRecording = false;
		} else {
			System.out.println("start recording");
			
			filename = genNewFileName();
			recorderPipe.getElementByName("filesink").set("location", filename);
			recorderPipe.setState(State.PLAYING);
			isRecording = true;
		}

		return filename;
	}

	public String genNewFileName() {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

		String newFile = date.format(new Date()) + ".ogg";
		return newFile;
	}

	public boolean isRecording() {
		return isRecording;
	}

}
