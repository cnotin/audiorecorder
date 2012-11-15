package se.ltu.M7017E.lab1;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
	private Preferences prefs;

	/**
	 * Default constructor, the preferences are linked to this class
	 */
	public Settings() {
		prefs = Preferences.userNodeForPackage(Settings.class);
	}

	/**
	 * Constructor to link the preferences to a specific class (useful to
	 * separate production and test preferences files)
	 * 
	 * @param c
	 *            Class to use as a key to preferences file
	 */
	public Settings(Class<?> c) {
		prefs = Preferences.userNodeForPackage(c);
	}

	/**
	 * Get preferred quality for recording (between 0.1 and 1.0)
	 * 
	 * @return the quality if set or the default
	 */
	public float getQuality() {
		return prefs.getFloat("quality", 0.3f);
	}

	/**
	 * Cf {@link #getQuality()}
	 */
	public void setQuality(float quality) {
		prefs.putFloat("quality", quality);
	}

	/**
	 * Get preferred folder for recording files
	 * 
	 * @return the path if set or the default
	 */
	public String getRecordingFolder() {
		return prefs.get("recordingFolder", new File("").getAbsolutePath());
	}

	/**
	 * Cf {@link #getRecordingFolder()}
	 */
	public void setRecordingFolder(String path) {
		prefs.put("recordingFolder", path);
	}

	public String getUsername() {
		return prefs.get("username", "clem");
	}

	public void setUsername(String username) {
		prefs.put("username", username);
	}

	public void clear() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			System.out.println("Got a problem while retrieving preferences");
			e.printStackTrace();
		}
	}

	public void flush() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			System.out.println("Got a problem while flushing preferences");
			e.printStackTrace();
		}
	}
}
