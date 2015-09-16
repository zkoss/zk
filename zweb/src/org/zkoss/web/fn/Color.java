/* Color.java

	Purpose:
		
	Description:
		
	History:
		Tue, Sep 15, 2015  6:39:51 PM, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.web.fn;

/**
 * Simple container for color components, using rgba, all in 0-255 range
 * @author Christopher
 *
 */
public class Color {

	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	/**
	 * All color channel should be in 0-255 range, alpha will default to 255 if not given
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	/**
	 * All color channel should be in 0-255 range
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	public Color(int r, int g, int b, int a) {
		checkColorRange(r, g, b, a);
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	private void checkColorRange(int r, int g, int b, int a) {
		String wrongColors = "";
		boolean gotError = false;
		if (r < 0 || r > 255) {
			gotError = true;
			wrongColors += "\"red\"";
		}
		if (g < 0 || g > 255) {
			gotError = true;
			wrongColors += " \"green\"";
		}
		if (b < 0 || b > 255) {
			gotError = true;
			wrongColors += " \"blue\"";
		}
		if (a < 0 || a > 255) {
			gotError = true;
			wrongColors += " \"alpha\"";
		}
		if(gotError) {
			throw new IllegalArgumentException(wrongColors + " color value outside of expected range 0-255");
		}
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getRGB() {
		return (alpha << 24) + (red << 16) + (green << 8) + blue;
	}
}
