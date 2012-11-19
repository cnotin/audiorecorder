package se.ltu.M7017E.lab1;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Utility class to put/get the settings using Java standardized Preferences
 * API.
 */
public class Settings {
	/**
	 * The Java API
	 */
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
		return prefs.getFloat("quality", 0.6f);
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
	 * @return the path if set or the default one
	 */
	public String getRecordingFolder() {
		String path = prefs.get("recordingFolder",
				new File("").getAbsolutePath());
		/*
		 * when the configured folder (from a previous run for example) has been
		 * deleted in the mean time, we should revert to the default instead
		 */
		if (!new File(path).isDirectory()) {
			System.err
					.println("The configured recording folder has been deleted, reverting to default.");
			path = new File("").getAbsolutePath();
		}
		return path;
	}

	/**
	 * Cf {@link #getRecordingFolder()}
	 */
	public void setRecordingFolder(String path) {
		prefs.put("recordingFolder", path);
	}

	/**
	 * For test purpose
	 * 
	 * @return username or default
	 */
	public String getUsername() {
		return prefs.get("username", "clem");
	}

	/**
	 * For test purpose
	 */
	public void setUsername(String username) {
		prefs.put("username", username);
	}

	/**
	 * Clear all settings {@link Preferences#clear()}
	 */
	public void clear() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			System.err.println("Got a problem while retrieving preferences");
			e.printStackTrace();
		}
	}

	/**
	 * Flush settings to storage {@link Preferences#flush()}
	 */
	public void flush() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			System.err.println("Got a problem while flushing preferences");
			e.printStackTrace();
		}
	}
}
