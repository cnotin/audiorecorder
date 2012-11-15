package se.ltu.M7017E.lab1;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

/**
 * Holds all GStreamer stuff for recording
 * 
 */
public class Recorder extends Pipeline {
	final Element autoaudiosrc = ElementFactory.make("autoaudiosrc",
			"autoaudiosrc");
	final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
	final Element oggmux = ElementFactory.make("oggmux", "oggmux");
	final Element filesink = ElementFactory.make("filesink", "filesink");

	public Recorder() {
		super("recorder");

		addMany(autoaudiosrc, vorbisenc, oggmux, filesink);
		Pipeline.linkMany(autoaudiosrc, vorbisenc, oggmux, filesink);
	}

	/**
	 * Sets the recording file's name.
	 * 
	 * @param filename
	 *            file extension must be provided
	 */
	public void setOutputFilename(String filename) {
		filesink.set("location", filename);
	}

	/**
	 * Sets quality for vorbis encoder.
	 * 
	 * @param quality
	 *            from 0.1 (lowest) to 1 (highest)
	 */
	public void setQuality(float quality) {
		vorbisenc.set("quality", quality);
	}
}
