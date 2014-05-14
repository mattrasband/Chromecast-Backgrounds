package com.nerdwaller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Search for and download Chromecast backgrounds currently visible on the Chromecast home page.
 * Backgrounds can have a gradient applied to them, closely mimicking the Chromecast's standard display.
 * Backgrounds can also have a watermark applied to the bottom right corner.
 */
public class Main {

	/**
	 * Find, download, and optionally watermark/apply gradient to the chromecast backgrounds.
	 * 
	 * @param args Are to be provided as command line switches.  The switches/params can be:
	 *   * settings - string, path a file to Load the settings from, if provided - other switches are ignored.
	 *   * outdir - string, path to the output directory (where to save the images to).
	 *   * watermark - switch, if provided a watermark will be applied to the images with the author's name.
	 *   * gradient - switch, if provided a gradient will be applied to the image, mimicking the display on a chromecast.
	 *   
	 * Example:
	 *    java -jar ChromecastBackground.jar -outdir /Users/nerdwaller/chromecastbg/ -watermark -gradient
	 */
	public static void main(String[] args) {
		final Settings settings = loadSettings(args);
		
		System.out.println("Chromecast Backgrounds");
		System.out.println("\tSaving to: " + settings.savePath());
		System.out.println("\tAdding Gradient: " + settings.applyGradient());
		System.out.println("\tApplying Watermark: " + settings.applyWatermark() + "\n");
		
		// Get a list of background currently displayed on the Chromecast's homepage (only ever
		// gives 100, but there appear to be well over 500 individual images and growing).
		System.out.print("Searching for backgrounds...");
		Chromecast cc = new Chromecast();
		List<Background> bgs = cc.getBackgrounds();
		System.out.println(" " + bgs.size() + " found.");
		
		// Create the save path if necessary
		File savePath = new File(settings.savePath());
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
		
		Boolean foundNew = false;
		
		System.out.println("Checking for new images...");
		
		// Loop through all found backgrounds checking if the image has already been downloaded.
		// If not, thread out the download and processing (gradient/watermark if applied) of images.
		List<Thread> threads = new ArrayList<Thread>();
		for (final Background bg : bgs) {
			final File saveFile = new File(settings.savePath() + bg.getName());
			if (!saveFile.exists()) {
				foundNew = true;
				System.out.println("\tDownloading new image '" + bg.getName() + "'");
				Thread t = new Thread(
						new Runnable() {
							public void run() {
								BufferedImage image = Images.downloadImage(bg.getHref());
								if (settings.applyGradient()) {
									image = Images.overlayGradient(image);
								}
								if (settings.applyWatermark()) {
									image = Images.applyWatermark(image, bg.author);
								}
								Images.saveImage(image, saveFile.toString());
							}
						}
					);
				t.start();
				threads.add(t);
			}
		}
		
		if (!foundNew) {
			System.out.println("No new images found.");
		}
		
		// Wait for all threads to complete.
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Finished.");
	}
	
	/**
	 * Create the options for the Command Line argument parser (apache)
	 * 
	 * @return Options that can be parsed from the command line argument parser.
	 */
	private static final Options createOptions() {
		Options options = new Options();
		
		Option settingsFile = new Option("settings", true, "Load settings from a file.");
		Option outputDirectory = new Option("outdir", true, "Output directory");
		Option applyWatermark = new Option("watermark", false, "Apply the author's name as a watermark");
		Option applyGradient = new Option("gradient", false, "Overlay a gradient (attempts to match the Chromecast's display of images)");
		
		options.addOption(settingsFile);
		options.addOption(outputDirectory);
		options.addOption(applyWatermark);
		options.addOption(applyGradient);
		
		return options;
	}

	/**
	 * Load the settings based on the arguments provided by the user.  If none were provided, the default settings
	 * will be loaded.
	 * 
	 * @param args Command line arguments, all are optional: "settings" (string: path to the settings file to load, other args are ignored),
	 * "outdir" (string: the output/save directory), "watermark" (switch: include the author's name as a watermark), "gradient" (switch: 
	 * apply a gradient to the image, mimick's the chromecast theme).
	 * @return
	 */
	private static final Settings loadSettings(final String[] args) {
		Settings settings = null;
		
		if (args.length == 0) {
			settings = new Settings();
		}
		else {	
			try {
				Options options = createOptions();
				CommandLineParser parser = new BasicParser();
				CommandLine cli = parser.parse(options, args);
				
				// Load from a settings file.  If provided, other args are ignored.
				if (cli.hasOption("settings")) {
					settings = new Settings(cli.getOptionValue("settings"));
				}
				// Load from the provided switches.
				else {
					settings = new Settings(cli.getOptionValue("outdir"), cli.hasOption("watermark"), cli.hasOption("gradient"));
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return settings;
	}
}
