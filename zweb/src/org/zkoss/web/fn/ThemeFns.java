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
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locators;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.web.theme.Theme;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
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
		Double number = Servlets.getBrowser(
				ServletFns.getCurrentRequest(), "ff");
		if (number != null && number >= 3.6)
			return Browser.Firefox;
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "ie");
		if (number != null) {
			if (number < 10) {
				if (number == 9)
					return Browser.IE9;
				return Browser.Old_IE;
			} else return Browser.IE;
		}
		number = Servlets.getBrowser(ServletFns.getCurrentRequest(), "webkit");
		if (number != null) {
			Double android = Servlets.getBrowser(
					ServletFns.getCurrentRequest(), "android");
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

			// B65-ZK-1614: Full Screen iPad Web Apps Missing Component Buttons
			version = Servlets.getBrowser(
					ServletFns.getCurrentRequest(), "ios");
			if (version != null && version >= 500)
				return Browser.WebKit;
			
			version = Servlets.getBrowser(
					ServletFns.getCurrentRequest(), "safari");
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
				if (end == 0 && !color.toLowerCase(java.util.Locale.ENGLISH).contains("transparent"))
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
		} else if (template == Browser.IE9) {
			for (String color : colors) {
				color = color.trim();
				boolean hex = color.startsWith("#");

				int end = hex ? color.indexOf(" ") + 1 : color.indexOf(")") + 1;
				if (end == 0 && !color.toLowerCase(java.util.Locale.ENGLISH).contains("transparent"))
					if (hex)
						throw new IllegalArgumentException(
								"The format of hexadecimal is wrong! [" + color
										+ "]");
					else throw new IllegalArgumentException(
							"The format of RGBA is wrong! [" + color + "]");

				String pos = color.substring(end, color.length());
				color = color.substring(0, end);
				colorAll.append("<stop stop-color=\"").append(color).append("\" offset=\"")
						.append(pos).append("\"/>");
			}
		
		} else if (template == Browser.Old_IE) {
			color1 = toIEHex(colors[0]);
			color2 = toIEHex(colors[1]);
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
		String result = String.format(template.getGradient(dir), color1, color2, "",
				gradType, ieGradType, colorAll.toString());
		if (template == Browser.IE9) {
			try {
				result = Base64.encodeBase64String(result.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				result = Base64.encodeBase64String(result.getBytes());
			}
			result = "url(data:image/svg+xml;base64," + result + ")";
		}
		
		return result;
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
		if (!Strings.isEmpty(browser.getPrefix())) {
			return String.format(CSS_TEMPLATE, browser.getPrefix(), styleName,
					styleValue);
		}
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


	@SuppressWarnings("unchecked")
	private static String toIEHex(String color) {
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
				.getCurrentRequest().getAttribute("themeFns.IEcolors");
		if (colors == null)
			ServletFns.getCurrentRequest().setAttribute("themeFns.IEcolors",
					colors = new HashMap<String, String>());
		if (!colors.containsKey(color))
			colors.put(color, toIEHex(toColor(color)));
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
		// add ability to load theme properties from a folder
		// @since 6.5.2
		if (!ThemeProperties.loadProperties(ServletFns.getCurrentRequest(), 
				locate(ServletFns.resolveThemeURL(path)))) {
			log("The properties file is not loaded correctly! [" + path + "]");
		}
	}

	private static String toHex(Color color) {
		return Colors.getHexString(color);
	}
	private static String toIEHex(Color color) {
		return Colors.getIEHexString(color);
	}

	private static Color toColor(String color) {
		return Colors.parseCSS(color);
	}

	private enum Browser {
		WebKit("-webkit-", "Chrome10+,Safari5.1+"),
		W3C("", "W3C"),
		Firefox("-moz-", "FF3.6+"),
		Opera("-o-", "Opera 11.10+"),
		IE("-ms-", "IE10+"),
		IE9("-ms-", "IE9"),
		Old(null, null),
		Old_IE(null, "IE6-9"),
		Old_WebKit("-webkit-", "Chrome,Safari4+");

		private final String _template;

		private final String _prefix;

		private HashMap<String, String> _GRAD_TYPE = null;

		Browser(String prefix, String browser) {
			_prefix = prefix;
			if ("IE6-9".equals(browser)) {
				_template = new StringBuilder(
						"\tbackground: #FFFFFF;\tfilter: progid:DXImageTransform.Microsoft.gradient( startColorstr='%1$s',")
						.append(" endColorstr='%2$s',GradientType=%5$s ); /* IE6-9 */\n")
						.toString();
			} else if ("IE9".equals(browser)) {
				_template = new StringBuilder(
						"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%%\" height=\"100%%\" viewBox=\"0 0 1 1\" preserveAspectRatio=\"none\">\n")
						.append("%3$s\n")
						.append("%6$s\n")
						.append("</linearGradient>\n")
						.append("<rect x=\"0\" y=\"0\" width=\"1\" height=\"1\" fill=\"url(#zkie9)\" /></svg>").toString();
				_GRAD_TYPE = new HashMap<String, String>();
				_GRAD_TYPE.put("ver", "<linearGradient id=\"zkie9\" gradientUnits=\"userSpaceOnUse\" x1=\"0%%\" y1=\"0%%\" x2=\"0%%\" y2=\"100%%\">");
				_GRAD_TYPE.put("hor", "<linearGradient id=\"zkie9\" gradientUnits=\"userSpaceOnUse\" x1=\"0%%\" y1=\"0%%\" x2=\"100%%\" y2=\"0%%\">");
				_GRAD_TYPE.put("diag-", "<linearGradient id=\"zkie9\" gradientUnits=\"userSpaceOnUse\" x1=\"0%%\" y1=\"0%%\" x2=\"100%%\" y2=\"100%%\">");
				_GRAD_TYPE.put("diag+", "<linearGradient id=\"zkie9\" gradientUnits=\"userSpaceOnUse\" x1=\"0%%\" y1=\"100%%\" x2=\"100%%\" y2=\"0%%\">");
				_GRAD_TYPE
						.put("rad", "<radialGradient id=\"zkie9\" gradientUnits=\"userSpaceOnUse\" cx=\"50%%\" cy=\"50%%\" r=\"50%%\">");
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

	// the current theme registry
	private static ThemeRegistry _themeRegistry = null;
	// the current theme resolver
	private static ThemeResolver _themeResolver = null;
	
	/**
	 * Returns the current theme registry
	 * 
	 * @return the current theme registry
	 * @since 6.5.2
	 */
	public static ThemeRegistry getThemeRegistry() {
		return _themeRegistry;
	}
	
	/**
	 * Change the theme registry
	 * 
	 * @param themeRegistry the new theme registry
	 * @since 6.5.2
	 */
	public static void setThemeRegistry(ThemeRegistry themeRegistry) {
		_themeRegistry = themeRegistry;
	}
	
	/**
	 * Returns the current theme resolver
	 * 
	 * @return the current theme resolver
	 * @since 6.5.2
	 */
	public static ThemeResolver getThemeResolver() {
		return _themeResolver;
	}
	
	/**
	 * Change the current theme resolver
	 * 
	 * @param themeResolver the new theme resolver
	 */
	public static void setThemeResolver(ThemeResolver themeResolver) {
		_themeResolver = themeResolver;
	}
	
	private final static String THEME_PREFERRED_KEY = "org.zkoss.theme.preferred";
	
	/**
	 * Returns the current theme name
	 * 
	 * @return the current theme name
	 * @since 6.5.2
	 */
	public static String getCurrentTheme() {
		// 1. cookie's key
		String t = getTheme();
		if (_themeRegistry.hasTheme(t))
			return t;
		
		// 2. library property
		t = Library.getProperty(THEME_PREFERRED_KEY);
		if (_themeRegistry.hasTheme(t))
			return t;
		
		// 3. theme of highest priority
		Theme[] themes = _themeRegistry.getThemes();
		StandardTheme highest = null;
		Comparator<StandardTheme> comparator = StandardTheme.getComparator();
		for (Theme theme : themes) {
			if (theme instanceof StandardTheme) {
				if (comparator.compare((StandardTheme)theme, highest) < 0) {
					highest = (StandardTheme)theme;
				}
			}
		}		
		return (highest != null) ? highest.getName() : StandardTheme.DEFAULT_NAME;
	}
	
	/**
	 * Returns the theme specified in cookies
	 * @return the name of the theme or default theme.
	 */
	private static String getTheme() {
		ServletRequest request = ServletFns.getCurrentRequest();
		
		if (!(request instanceof HttpServletRequest))
			return StandardTheme.DEFAULT_NAME;
		
		return _themeResolver.getTheme((HttpServletRequest)request);
	}
	
}
