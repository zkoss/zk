/* ThemeFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 10, 2012 9:35:27 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.web.fn;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.web.servlet.Servlets;

/**
 * Providing theme relevant functions for EL.
 * 
 * @author simonpai
 * @author jumperchen
 * @since 6.1.0
 */
public class ThemeFns {

	private ThemeFns() {
	}
	private static Gradient getTemplate() {
		Double number = Servlets.getBrowser(ServletFns.getCurrentRequest(),
				"ff");
		if (number != null && number >= 3.6)
			return Gradient.Firefox;
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "ie");
		if (number != null) {
			if (number < 10)
				return Gradient.Old_IE;
			else return Gradient.IE;
		}
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "webkit");
		if (number != null) {
			Double version = Servlets.getBrowser(ServletFns.getCurrentRequest(), "chrome");
			if (version != null) {
				if (version >= 10)
					return Gradient.WebKit;
				if (version >= 1)
					return Gradient.Old_WebKit;
			}
			version = Servlets.getBrowser(ServletFns.getCurrentRequest(), "safari");
			if (version != null) {
				if (version >= 5.1)
					return Gradient.WebKit;
				if (version >= 4)
					return Gradient.Old_WebKit;
			}
		}
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "opera");
		if (number != null && number >= 11.1)
			return Gradient.Opera;
		return Gradient.Old;
	}
	
	/**
	 * Generates a specific of browser CSS color gradient
	 * rules String.
	 * 
	 * @param direction
	 *            "ver", "hor", "diag-", "diag+", "rad"
	 * @param colors
	 *            the colors, which are separated by semicolon ";"
	 * @return A specific CSS gradient rules String
	 */
	public static String gradient(String direction, String colors) {
		Gradient temp = getTemplate();
		if (temp == Gradient.Old || temp == Gradient.W3C)
			return "\tbackground:" +grad(direction, getTemplate(), colors.split(";"));
		else {
			String[] cols = colors.split(";");
			StringBuilder sb = new StringBuilder("\tbackground:")
				.append(grad(direction, Gradient.W3C, cols));
			if (temp != Gradient.Old_IE)
				sb.append("\tbackground:");
			return sb.append(grad(direction, temp, cols)).toString();	
		}
	}
	
	/**
	 * Generates a specific CSS color gradient value only.
	 * 
	 * @param direction
	 *            "ver", "hor", "diag-", "diag+", "rad"
	 * @param colors
	 *            the colors, which are separated by semicolon ";"
	 * @return A specific CSS gradient rules String without the words "background:".
	 */
	public static String gradValue(String direction, String colors) {
		return grad(direction, getTemplate(), colors.split(";")).replace(";", "");
	}

	private static String grad(String dir, Gradient template, String[] colors) {
		if ("|ver|hor|diag-|diag+|rad|".indexOf("|" + dir + "|") == -1)
			throw new IllegalArgumentException(
					"Type must be ver, hor, diag-, diag+, or rad.");
		if (colors.length < 2)
			throw new IllegalArgumentException(
					"Please specify more than two colors.");
		String color1 = toHex(colors[0]);
		String color2 = toHex(colors[1]);
		StringBuilder colorAll = new StringBuilder();
		if (template == Gradient.Old_WebKit) {
			for (String color : colors) {
				color = color.trim();
				boolean hex = color.startsWith("#");
				
				int end = hex ? color.indexOf(" ") + 1 : color.indexOf(")") + 1;
				if (end == 0)
					if (hex)
						throw new IllegalArgumentException(
								"The format of hexadecimal is wrong! [" + color + "]");
					else
						throw new IllegalArgumentException(
								"The format of RGBA is wrong! [" + color + "]");
					
				String pos = color.substring(end, color.length());
				color = color.substring(0, end);
				colorAll.append("color-stop(").append(pos).append(',').append(color).append("),");
			}
			int len = colorAll.length();
			if (len > 0)
				colorAll.delete(len - 1, len);
		} else {
			for (String color : colors) {
				colorAll.append(color).append(',');
			}
			int len = colorAll.length();
			if (len > 0)
				colorAll.delete(len - 1, len);
		}
		
		String gradType = "rad".equals(dir) ? "radial" : "linear";
		int ieGradType = "hor".equals(dir) ? 1 : 0; // IE only supports
															// ver/hor
		return String.format(template.getTemplate(dir), color1, color2, "", gradType,
				ieGradType, colorAll.toString());
	}
	
	
	
	/**
	 * Generates a set of cross-browser CSS color gradient
	 * rules String.
	 * 
	 * @param direction
	 *            "ver", "hor", "diag-", "diag+", "rad"
	 * @param colors
	 *            the colors, which are separated by semicolon ";"
	 * @return A set of cross-browser CSS gradient rules String
	 */
	public static String gradients(String direction, String colors) {
		StringBuilder sb = new StringBuilder();
		String[] cols = colors.split(";");
		for (Gradient grad : Gradient.values()) {
			if (grad != Gradient.Old_IE)
				sb.append("\tbackground:");
			sb.append(grad(direction, grad, cols));
		}
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	private static String toHex(String color) {
		color = color.trim();
		if (color.startsWith("#")) {
			int end = color.indexOf(" ");
			if (end > 0)
				return color.substring(0, end);
			return color;
		}
		int end = color.indexOf(')') + 1;
		if (end > 0)
			color = color.substring(0, end);
		
		Map<String, String> colors = (Map<String, String>)ServletFns.getCurrentRequest().getAttribute("themeFns.colors");
		if (colors == null)
			ServletFns.getCurrentRequest().setAttribute("themeFns.colors", colors = new HashMap<String, String>());
		if (!colors.containsKey(color))
			colors.put(color, toHex(toColor(color)));
		return colors.get(color);
	}
	
	private static String toHex(Color color) {
		return Colors.getHexString(color);
	}
	private static Color toColor(String color) {
		return Colors.parseCSS(color);
	}
	public enum Gradient {
		WebKit("-webkit-", "Chrome10+,Safari5.1+"),
		W3C("", "W3C"),
		Firefox("-moz-", "FF3.6+"),
		Opera("-o-", "Opera 11.10+"),
		IE("-ms-", "IE10+"),
		Old(null, null),
		Old_IE(null, "IE6-9"),
		Old_WebKit("-webkit-", "Chrome,Safari4+");
		
		private final String _template;
		private HashMap<String, String> _GRAD_TYPE = null;
		Gradient(String prefix, String browser) {
			if ("IE6-9".equals(browser)) {
				_template = new StringBuilder(
						"\tfilter: progid:DXImageTransform.Microsoft.gradient( startColorstr='%1$s',")
						.append(" endColorstr='%2$s',GradientType=%5$s ); /* IE6-9 */\n").toString();
			} else if ("Chrome,Safari4+".equals(browser)) {
				_template = new StringBuilder().append("\t").append(prefix)
						.append("gradient(%4$s, %3$s, %6$s); /* ")
						.append(browser).append(" */\n").toString();
				_GRAD_TYPE = new HashMap<String, String>();
				_GRAD_TYPE.put("ver", "left top, left bottom");
				_GRAD_TYPE.put("hor", "left top, right top");
				_GRAD_TYPE.put("diag-", "left top, right bottom");
				_GRAD_TYPE.put("diag+", "left bottom, right top");
				_GRAD_TYPE.put("rad", "center center, 0px, center center, 100%");
			} else if (browser != null) {
				_template = new StringBuilder().append("\t").append(prefix)
						.append("%4$s-gradient(%3$s, %6$s); /* ")
						.append(browser).append(" */\n").toString();
				if ("W3C".equals(browser)) {
					_GRAD_TYPE = new HashMap<String, String>();
					_GRAD_TYPE.put("ver", "to bottom");
					_GRAD_TYPE.put("hor", "to right");
					_GRAD_TYPE.put("diag-", "135deg");
					_GRAD_TYPE.put("diag+", "45deg");
					_GRAD_TYPE.put("rad", "ellipse at center");
				} else {
					_GRAD_TYPE = new HashMap<String, String>();
					_GRAD_TYPE.put("ver", "top");
					_GRAD_TYPE.put("hor", "left");
					_GRAD_TYPE.put("diag-", "-45deg");
					_GRAD_TYPE.put("diag+", "45deg");
					_GRAD_TYPE.put("rad", "center, ellipse cover");
				}
			} else
				_template = "\t%1$s; /* Old browsers */\n";
		}
		public String getTemplate(String dir) {
			return _template.replace("%3$s", getType(dir));
		}
		private String getType(String dir) {
			return _GRAD_TYPE != null ? _GRAD_TYPE.get(dir) : "";
		}
	}
	

}
