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

import javax.servlet.ServletException;

import org.zkoss.lang.Strings;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locators;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ClassWebResource;

/**
 * Providing theme relevant functions for EL.
 * 
 * @author simonpai
 * @author jumperchen
 * @since 6.5.0
 */
public class ThemeFns {
	
	private ThemeFns() {
	}

	private static Browser getBrowser() {
		Double number = Servlets.getBrowser(ServletFns.getCurrentRequest(),
				"ff");
		if (number != null && number >= 3.6)
			return Browser.Firefox;
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "ie");
		if (number != null) {
			if (number < 10)
				return Browser.Old_IE;
			else return Browser.IE;
		}
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "webkit");
		if (number != null) {
			Double android = Servlets.getBrowser(ServletFns.getCurrentRequest(), "android");
			if (android != null && android < 3) {
				return Browser.Old_WebKit;
			}
			Double version = Servlets.getBrowser(
					ServletFns.getCurrentRequest(), "chrome");
			if (version != null) {
				if (version >= 10)
					return Browser.WebKit;
				if (version >= 1)
					return Browser.Old_WebKit;
			}
			version = Servlets.getBrowser(ServletFns.getCurrentRequest(),
					"safari");
			if (version != null) {
				if (version >= 5.1)
					return Browser.WebKit;
				if (version >= 4)
					return Browser.Old_WebKit;
			}
		}
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "opera");
		if (number != null && number >= 11.1)
			return Browser.Opera;
		return Browser.Old;
	}

	/**
	 * Generates a specific of browser CSS color gradient rules String.
	 * 
	 * @param direction
	 *            "ver", "hor", "diag-", "diag+", "rad"
	 * @param colors
	 *            the colors, which are separated by semicolon ";"
	 * @return A specific CSS gradient rules String
	 */
	public static String gradient(String direction, String colors) {
		Browser temp = getBrowser();
		if (temp == Browser.Old || temp == Browser.W3C)
			return "\tbackground:"
					+ grad(direction, getBrowser(), colors.split(";"));
		else {
			String[] cols = colors.split(";");
			StringBuilder sb = new StringBuilder("\tbackground:").append(grad(
					direction, Browser.W3C, cols));
			if (temp != Browser.Old_IE)
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
	 * @return A specific CSS gradient rules String without the words
	 *         "background:".
	 */
	public static String gradValue(String direction, String colors) {
		return grad(direction, getBrowser(), colors.split(";"))
				.replace(";", "");
	}

	private static String grad(String dir, Browser template, String[] colors) {
		if ("|ver|hor|diag-|diag+|rad|".indexOf("|" + dir + "|") == -1)
			throw new IllegalArgumentException(
					"Type must be ver, hor, diag-, diag+, or rad.");
		if (colors.length < 2)
			throw new IllegalArgumentException(
					"Please specify more than two colors.");
		String color1 = toHex(colors[0]);
		String color2 = toHex(colors[1]);
		StringBuilder colorAll = new StringBuilder();
		if (template == Browser.Old_WebKit) {
			for (String color : colors) {
				color = color.trim();
				boolean hex = color.startsWith("#");

				int end = hex ? color.indexOf(" ") + 1 : color.indexOf(")") + 1;
				if (end == 0 && !color.toLowerCase().contains("transparent"))
					if (hex)
						throw new IllegalArgumentException(
								"The format of hexadecimal is wrong! [" + color
										+ "]");
					else throw new IllegalArgumentException(
							"The format of RGBA is wrong! [" + color + "]");

				String pos = color.substring(end, color.length());
				color = color.substring(0, end);
				colorAll.append("color-stop(").append(pos).append(',')
						.append(color).append("),");
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
		return String.format(template.getGradient(dir), color1, color2, "",
				gradType, ieGradType, colorAll.toString());
	}

	/**
	 * Generates a set of cross-browser CSS color gradient rules String.
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
		for (Browser grad : Browser.values()) {
			if (grad != Browser.Old_IE)
				sb.append("\tbackground:");
			sb.append(grad(direction, grad, cols));
		}
		return sb.toString();
	}

	private static String CSS_TEMPLATE = "\t %1$s%2$s:\t %3$s;\n\t %2$s:\t%3$s;";

	private static String CSS_TEMPLATE_W3C = "\t %1$s:\t%2$s;";

	private static String applyCSS(String styleName, String styleValue) {
		Browser browser = getBrowser();
		if (!Strings.isEmpty(browser.getPrefix()))
			return String.format(CSS_TEMPLATE, browser.getPrefix(), styleName,
					styleValue);
		return String.format(CSS_TEMPLATE_W3C, styleName, styleValue);
	}

	/**
	 * Generates a specific browser CSS rule String for the given style name and
	 * value.
	 * <p>
	 * Note: the method is only applied with the browser prefix as the style
	 * name, if the CSS3 style usage rule is different between browsers, please
	 * use another method instead.
	 * 
	 * @param styleName
	 *            the value of the style name, like <code>box-sizing</code>,
	 *            <code>animation</code>
	 * @param styleValue
	 *            the value according to the style name, like
	 *            <code>border-box</code>, <code>mymove 5s infinite</code>
	 * @return a specific browser CSS rule string, like
	 *         <code>-moz-box-sizing</code> for firefox and
	 *         <code>-webkit-box-sizing</code> for safari and chrome
	 */
	public static String applyCSS3(String styleName, String styleValue) {
		return applyCSS(styleName, styleValue);
	}

	/**
	 * Generates a specific browser CSS rule string for box model.
	 * 
	 * @param styleName
	 *            the value of the style name, like <code>box-orient</code>,
	 *            <code>box-pack</code>
	 * @param styleValue
	 *            the value according to the style name, like
	 *            <code>horizontal</code>, <code>center</code>
	 * @return a specific browser CSS rule string, like
	 *         <code>-moz-box-orient</code> for firefox and
	 *         <code>-webkit-box-orient</code> for safari and chrome
	 */
	public static String box(String styleName, String styleValue) {
		Browser browser = getBrowser();
		StringBuilder sb = new StringBuilder(32);
		if (browser == Browser.Firefox || browser == Browser.WebKit) {
			sb.append("\t display:\t").append(browser.getPrefix())
					.append("box;\n");
		}
		sb.append("\t display:\t box;\n");
		return sb.append(applyCSS(styleName, styleValue)).toString();
	}

	/**
	 * Generates a specific browser CSS rule string for box model with two pair
	 * styles.
	 * 
	 * @see #box(String, String)
	 */
	public static String box2(String styleName, String styleValue,
			String styleName2, String styleValue2) {
		Browser browser = getBrowser();
		StringBuilder sb = new StringBuilder(32);
		if (browser == Browser.Firefox || browser == Browser.WebKit) {
			sb.append("\t display:\t").append(browser.getPrefix())
					.append("box;\n");
		}
		sb.append("\t display:\t box;\n");
		return sb.append(applyCSS(styleName, styleValue))
				.append(applyCSS(styleName2, styleValue2)).toString();
	}

	/**
	 * Generates a specific browser CSS rule string for box model with three pair
	 * styles.
	 * 
	 * @see #box(String, String)
	 * @see #box2(String, String, String, String)
	 */
	public static String box3(String styleName, String styleValue,
			String styleName2, String styleValue2, String styleName3,
			String styleValue3) {
		Browser browser = getBrowser();
		StringBuilder sb = new StringBuilder(32);
		if (browser == Browser.Firefox || browser == Browser.WebKit) {
			sb.append("\t display:\t").append(browser.getPrefix())
					.append("box;\n");
		}
		sb.append("\t display:\t box;\n");
		return sb.append(applyCSS(styleName, styleValue))
				.append(applyCSS(styleName2, styleValue2))
				.append(applyCSS(styleName3, styleValue3)).toString();
	}

	/**
	 * Generates a specific browser CSS transform.
	 * 
	 * @param style
	 *            the value of the transform
	 * @return a specific browser CSS transform
	 */
	public static String transform(String style) {
		return applyCSS("transform", style);
	}

	/**
	 * Generates a specific browser CSS box-shadow.
	 * 
	 * @param style
	 *            the value of the box-shadow
	 * @return a specific browser CSS box-shadow
	 */
	public static String boxShadow(String style) {
		return applyCSS("box-shadow", style);
	}

	/**
	 * Generates a specific browser CSS border-radius.
	 * 
	 * @param style
	 *            the value of the border-radius
	 * @return a specific browser CSS border-radius
	 */
	public static String borderRadius(String style) {
		return applyCSS("border-radius", style);
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

		Map<String, String> colors = (Map<String, String>) ServletFns
				.getCurrentRequest().getAttribute("themeFns.colors");
		if (colors == null)
			ServletFns.getCurrentRequest().setAttribute("themeFns.colors",
					colors = new HashMap<String, String>());
		if (!colors.containsKey(color))
			colors.put(color, toHex(toColor(color)));
		return colors.get(color);
	}
	private static String locate(String path) {
		try {
			if (path.startsWith("~./")) {
				path = Servlets.locate(ServletFns.getCurrentServletContext(), ServletFns.getCurrentRequest(),
					ClassWebResource.PATH_PREFIX + path.substring(2),
					Locators.getDefault());
				return path;
			}
			return Servlets.locate(ServletFns.getCurrentServletContext(), ServletFns.getCurrentRequest(), path, null);
		} catch (ServletException ex) {
			log(ex.getLocalizedMessage());
		}
		return path;
	}
	
	private static void log(String msg) {
		Log log = Log.lookup("global");
		if (log.errorable()) log.error(msg);
		else System.err.println(msg);
	}
	/**
	 * Loads a theme properties and apply them into the request scope.
	 * @param path a file path
	 */
	public static void loadProperties(String path) {
		if (!ThemeProperties.loadProperties(ServletFns.getCurrentRequest(), 
				locate(path))) {
			log("The properties file is not loaded correctly! [" + path + "]");
		}
	}
	
	private static String toHex(Color color) {
		return Colors.getHexString(color);
	}

	private static Color toColor(String color) {
		return Colors.parseCSS(color);
	}

	private enum Browser {
		WebKit("-webkit-", "Chrome10+,Safari5.1+"), W3C("", "W3C"), Firefox(
				"-moz-", "FF3.6+"), Opera("-o-", "Opera 11.10+"), IE("-ms-",
				"IE10+"), Old(null, null), Old_IE(null, "IE6-9"), Old_WebKit(
				"-webkit-", "Chrome,Safari4+");

		private final String _template;

		private final String _prefix;

		private HashMap<String, String> _GRAD_TYPE = null;

		Browser(String prefix, String browser) {
			_prefix = prefix;
			if ("IE6-9".equals(browser)) {
				_template = new StringBuilder(
						"\tfilter: progid:DXImageTransform.Microsoft.gradient( startColorstr='%1$s',")
						.append(" endColorstr='%2$s',GradientType=%5$s ); /* IE6-9 */\n")
						.toString();
			} else if ("Chrome,Safari4+".equals(browser)) {
				_template = new StringBuilder().append("\t").append(prefix)
						.append("gradient(%4$s, %3$s, %6$s); /* ")
						.append(browser).append(" */\n").toString();
				_GRAD_TYPE = new HashMap<String, String>();
				_GRAD_TYPE.put("ver", "left top, left bottom");
				_GRAD_TYPE.put("hor", "left top, right top");
				_GRAD_TYPE.put("diag-", "left top, right bottom");
				_GRAD_TYPE.put("diag+", "left bottom, right top");
				_GRAD_TYPE
						.put("rad", "center center, 0px, center center, 100%");
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
			} else _template = "\t%1$s; /* Old browsers */\n";
		}

		public String getPrefix() {
			return _prefix;
		}

		public String getGradient(String dir) {
			return _template.replace("%3$s", getType(dir));
		}

		private String getType(String dir) {
			return _GRAD_TYPE != null ? _GRAD_TYPE.get(dir) : "";
		}
	}

}
