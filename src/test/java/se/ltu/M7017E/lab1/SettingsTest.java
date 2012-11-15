package se.ltu.M7017E.lab1;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {
	@Before
	public void clean() {
		new Settings(SettingsTest.class).clear();
	}

	@Test
	public void default_value_test() {
		Settings settings = new Settings(SettingsTest.class);

		// settings are cleared so we'll get the default value
		assertEquals("clem", settings.getUsername());

		// change username
		settings.setUsername("notin");
		// check if it's saved (at least in memory)
		assertEquals("notin", settings.getUsername());
		settings.flush();// make sure it's saved to file
		settings = null;

		// check if settings have been really saved to file
		Settings settings2 = new Settings(SettingsTest.class);
		assertEquals("notin", settings2.getUsername());
	}
}
