package com.nerdwaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chromecast {

	protected URL backgroundUrl;

    /**
     * Optionally define the quality of images.  Use lower quality to save space.
     * @throws MalformedURLException
     */
    public Chromecast() throws MalformedURLException {
        backgroundUrl = new URL("https://clients3.google.com/cast/chromecast/home/v/c9541b08");
    }
	
	/**
	 * Get a list of all backgrounds that are currently listed on the Chromecast page.
	 * @return A list of background objects with links and author information retained.
	 */
	public List<Background> getBackgrounds() {
		String html = getPageSource();
		List<Background> backgrounds = parseBackgrounds(html);
		return backgrounds;
	}

    /**
     * Tries to get all possible backgrounds.  This iterates over the page several times trying to find all
     * images.  It may not actually get all, but it should be fairly close.
     * @return (List<Background>)
     */
    public List<Background> getAllBackgrounds() {
        List<Background> backgrounds = new ArrayList<>();
        final int maxIters = 30;
        int timesWithoutResults = 0;

        for (int i = 0; i < maxIters && (timesWithoutResults <= 5); i++) {
            List<Background> bgs = parseBackgrounds(getPageSource());
            if (bgs.size() == 0) {
                timesWithoutResults++;
            } else {
                for (Background bg : bgs) {
                    if (!backgrounds.contains(bg)) {
                        backgrounds.add(bg);
                    }
                }
            }
        }

        return backgrounds;
    }
	
	/**
	 * Get the source of the chromecast background page.
	 * @return String containing the source of the web page (fairly small)
	 */
	protected String getPageSource() {
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(backgroundUrl.openStream()));
			String inLine;
			while ((inLine = reader.readLine()) != null) {
				builder.append(inLine);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
	/**
	 * Parse all the backgrounds from the HTML source.
	 * @param source The HTML source of the chromecast homepage.
	 * @return A list of all backgrounds from the source.
	 */
	protected List<Background> parseBackgrounds(String source) {
		source = source.replace("\\x22", "").replace("\\", "");
		Pattern regex = Pattern.compile("\\[(http.*?)\\]");
		Matcher m = regex.matcher(source);
		
		List<Background> backgroundList = new ArrayList<Background>();
		
		String[] splitString;
		String cleanedHref;
		while (m.find()) {
			splitString = m.group(1).split(",");
			cleanedHref = splitString[0].replace("s1280-w1280-c-h720", "s2560");
			backgroundList.add(new Background(cleanedHref, splitString[1]));
		}
		
		return backgroundList;
	}
}
