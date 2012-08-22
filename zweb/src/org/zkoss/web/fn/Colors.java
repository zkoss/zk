/* Colors.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jan 29, 2012 4:57:26 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.web.fn;

import java.awt.Color;

/**
 *
 * @author simonpai
 */
/**package*/ class Colors {
	
	public static Color parseCSS(String code) {
		int[] arr = parseCSS(code, null);
		return arr == null ? null : new Color(arr[0], arr[1], arr[2], arr[3]);
	}
	
	public static int[] parseCSS(String code, int[] arr) {
		if (code == null)
			return arr;
		if (arr == null)
			arr = new int[4];
		code = code.trim();
		if (code.startsWith("#")) {
			boolean sh = code.length() == 4; // short hand
			int r = Integer.parseInt(code.substring(         1, sh ? 2 : 3), 16);
			int g = Integer.parseInt(code.substring(sh ? 2 : 3, sh ? 3 : 5), 16);
			int b = Integer.parseInt(code.substring(sh ? 3 : 5, sh ? 4 : 7), 16);
			if (sh) {
				r *= 17;
				g *= 17;
				b *= 17;
			}
			arr[0] = r;
			arr[1] = g;
			arr[2] = b;
			arr[3] = 255;
		} else if (code.startsWith("rgba(") && code.endsWith(")")) {
			String s = code.substring(5, code.length() - 1);
			String[] bands = s.split(",");
			for (int i = 0; i < 3; i++) {
				String b = bands[i].trim();
				arr[i] = Integer.parseInt(b);
			}
			String b = bands[3].trim();
			arr[3] = (int) (255 * Double.parseDouble(b));
		}
		return arr;
	}
	
	public static String toCSS(Color color) {
		return color.getAlpha() < 255 ? getRGBAString(color) : getHexString(color);
	}
	
	public static String toCSS(int[] color) {
		if (color.length > 3)
			return toCSS(new Color(color[0], color[1], color[2], color[3]));
		return toCSS(new Color(color[0], color[1], color[2], 255));
	}
	
	public static String getRGBAString(Color c) {
		return "rgba(" + c.getRed() + ", " + c.getGreen() + ", " + 
				c.getBlue() + ", " + c.getAlpha() + ")";
	}
	
	public static String getRGBAString(int[] color) {
		return "rgba(" + color[0] + ", " + color[1] + ", " + 
				color[2] + ", " + color[3] + ")";
	}

	public static String getHexString(Color c) {
		String hex = Integer.toHexString(c.getRGB() & 0x00ffffff).toUpperCase();
		hex = repeat('0', 6 - hex.length()) + hex;
		int alpha = c.getAlpha();
		alpha = alpha == 255 ? -1 : alpha * 20 / 51;
		return '#' + hex + (alpha < 0 ? "" : (" (" + alpha + "%)"));
	}
	
	private static String repeat(char c, int i) {
		char[] cs = new char[i];
		for (int j = 0; j < i; j++)
			cs[j] = c;
		return new String(cs);
	}
	
}
