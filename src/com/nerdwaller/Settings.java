package com.nerdwaller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Class to hold settings for runtime.
 */
public class Settings {
	private Boolean applyGradient;
	private Boolean applyWatermark;
	private String savePath;

	/**
	 * Create the settings object with the default settings.
	 */
	public Settings() {
		loadDefaultSettings();
	}
	
	/**
	 * Load the settings object from a file, must be key=value format (properties file format)
	 * @param fileName Path to the file to load.
	 */
	public Settings(String fileName) {
		this();
		
		loadSettingsFile(Paths.get(fileName));
	}
	
	/**
	 * Load the settings object manually.
	 * @param outdir The output directory, where the images will be saved.
	 * @param watermark True to apply a watermark, false to not apply it.
	 * @param gradient True to apply a gradient, false to not apply it.
	 */
	public Settings(String outdir, Boolean watermark, Boolean gradient) {
		this();
		
		if (outdir != null) {
			savePath = outdir + ((outdir.charAt(outdir.length() - 1) == '/') ? "" : "/");
		}
		applyWatermark = watermark;
		applyGradient = gradient;
	}
	
	/**
	 * Get the setting for applying the gradient.
	 * @return Boolean - True to apply the gradient, false to not.
	 */
	public Boolean applyGradient() {
		return applyGradient;
	}
	
	/**
	 * Get the setting for applying the watermark.
	 * @return Boolean - True to apply the watermark, false to not.
	 */
	public Boolean applyWatermark() {
		return applyWatermark;
	}
	
	/**
	 * Get the path to save the backgrounds to.
	 * @return String - The path to where backgrounds should be saved to.
	 */
	public String savePath() {
		return savePath;
	}
	
	/**
	 * Load the settings from a file.
	 * @param fileName The file that holds the settings, key=value formatted.
	 */
	private void loadSettingsFile(Path fileName) {
		try {
			Properties settings = new Properties();
			settings.load(new FileInputStream("settings.cfg"));
			if (settings.contains("ApplyGradient")) {
				applyGradient = Boolean.parseBoolean((String) settings.get("ApplyGradient"));
			}
			if (settings.contains("ApplyWatermark")) {
				applyWatermark = Boolean.parseBoolean((String) settings.get("ApplyWatermark"));
			}
			if (settings.contains("SaveTo")) {
				savePath = (String) settings.get("SaveTo");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load the default settings for the system.
	 */
	private void loadDefaultSettings() {
		applyGradient = false;
		applyWatermark = false;
		String os = System.getProperty("os.name").toUpperCase();
		if (os.contains("WIN")) {
			savePath = System.getenv("AppData") + "\\chromecastbg\\";
		}
		// OSX
		else {
			savePath = System.getProperty("user.home") + "/Library/Application Support/chromecastbg/";
		}
		//todo linux
	}
}
