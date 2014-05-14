package com.nerdwaller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;


public class Images {
	
	/**
	 * Overlay a gradient on the @bgImage.
	 * @param bgImage The image to use as the background.
	 * @return A new image with a gradient overlayed.
	 */
	public static BufferedImage overlayGradient(BufferedImage bgImage) {
		BufferedImage gradient = generateGradient(bgImage.getHeight(), bgImage.getWidth());
		
		BufferedImage newImage = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = newImage.getGraphics();
		g.drawImage(bgImage, 0, 0, null);
		g.drawImage(gradient, 0, 0, null);
		
		return newImage;
	}
	
	/**
	 * Download an image from a URL.
	 * @param url The URL of the image resource.
	 * @return Image object loaded from the URL.
	 */
	public static BufferedImage downloadImage(String url) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new URL(url));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * Save the image as a JPG type.
	 * @param image The Image object to save.
	 * @param path The path, including name and extension, to save the image as.
	 * @return True if the save was successful, false if not.
	 */
	public static Boolean saveImage(BufferedImage image, String path) {
		Boolean success = false;
		try {
			ImageIO.write(image, "JPG", new File(path));
			success = true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Apply a water mark to the provided image.
	 * @param image The image to apply the watermark to.
	 * @param author Author of the image (or other string to use as a watermark)
	 * @return Image with an added water mark.
	 */
	public static BufferedImage applyWatermark(BufferedImage image, String author) {
		author = "Photo by " + author;
		
		Font openSans = null;
		try {
			InputStream is = Images.class.getResourceAsStream("fonts/OpenSans-Regular.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			// Convert it to 24pt
			openSans = font.deriveFont(24f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(openSans);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Unable to load the specified font.  Using sans-serif font.");
			openSans = new Font("sans serif", Font.PLAIN, 24);
		}
		
		//Font openSans = new Font("OpenSans", Font.PLAIN, 30);
		
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);
		g2d.setComposite(alphaChannel);
		g2d.setColor(Color.WHITE);
		g2d.setFont(openSans);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D rect = fontMetrics.getStringBounds(author, g2d);
		
		int x = image.getWidth() - (int) rect.getWidth() - 50;
		int y = image.getHeight() - (int) rect.getHeight() - 50;
		
		g2d.drawString(author, x, y);
		g2d.dispose();
		return image;
	}
	
	/**
	 * Generate a horizontal gradient with the height/width provided.
	 * @param height Height of the gradient image.
	 * @param width Width of the gradient image.
	 * @return Generated gradient image.
	 */
	protected static BufferedImage generateGradient(int height, int width) {
		BufferedImage gradient = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D ig2 = gradient.createGraphics();
		
		Color top = new Color(0.0f, 0.0f, 0.0f, 0.098f);
		Color bottom = new Color(0.0f, 0.0f, 0.0f, 0.9f);
		
		GradientPaint paint = new GradientPaint(0.0f, 0.0f, top, 0.0f, height, bottom);
		
		ig2.setPaint(paint);
		ig2.fill(new Rectangle(0, 0, width, height));
		ig2.dispose();
		gradient.flush();
		
		return gradient;
	}
}
