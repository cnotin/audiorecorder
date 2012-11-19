package se.ltu.M7017E.lab1;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

/**
 * Holds all GStreamer stuff for recording. autoaudiosrc -> vorbisenc -> oggmux
 * -> filesink
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
	 * Sets the recording filename.
	 * 
	 * @param filepath
	 *            filename with full path (or relative to app folder then),
	 *            extension must be provided too (should be .ogg normally)
	 */
	public void setOutputFile(String filepath) {
		filesink.set("location", filepath);
	}

	/**
	 * Sets quality for vorbis encoder. Refer to vorbisenc plugin.
	 * 
	 * @param quality
	 *            from 0.1 (lowest) to 1 (highest)
	 */
	public void setQuality(float quality) {
		vorbisenc.set("quality", quality);
	}
}
