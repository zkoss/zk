/* PageRenderer.java

	Purpose:

	Description:

	History:
		Tue Oct 14 17:31:02     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Library;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.theme.MarbleBrand.Brand;
import org.zkoss.zul.theme.MarbleDensity.Density;

/**
 * The page render for ZUL pages.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PageRenderer implements org.zkoss.zk.ui.sys.PageRenderer {
	private static final Logger log = LoggerFactory.getLogger(PageRenderer.class);
	private static final Pattern LANG_ATTRIBUTE_PATTERN = Pattern.compile("(?i)(?:^|\\s)lang\\s*=");
	private static final String DENSITY_PROPERTY = "org.zkoss.theme.marble.density";
	private static final String BRAND_PROPERTY = "org.zkoss.theme.marble.brand";
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})");
	private static final Pattern DENSITY_ATTRIBUTE_PATTERN = Pattern.compile("(?i)(?:^|\\s)data-density\\s*=");
	private static final Pattern BRAND_ATTRIBUTE_PATTERN = Pattern.compile("(?i)(?:^|\\s)data-brand\\s*=");

	public void render(Page page, Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		final String ctl = ExecutionsCtrl.getPageRedrawControl(exec);
		boolean au = exec.isAsyncUpdate(null);
		if (!au)
			HtmlPageRenders.setCspHeader(exec, page); //before any output, and for every document we render
		if (!au && (page.isComplete() || "complete".equals(ctl))) {
			renderComplete(exec, page, out);
			return;
		}

		boolean pageOnly = au;
		if (!pageOnly)
			pageOnly = (exec.isIncluded() || "page".equals(ctl)) && !"desktop".equals(ctl);

		if (pageOnly)
			renderPage(exec, page, out, au);
		else
			renderDesktop(exec, page, out);
	}

	/** Renders the desktop and the page.
	 */
	protected void renderDesktop(Execution exec, Page page, Writer out) throws IOException {
		HtmlPageRenders.setContentType(exec, page);

		final PageCtrl pageCtrl = (PageCtrl) page;
		final String rootAttrs = pageCtrl.getRootAttributes();
		final MarbleThemeDefaults themeDefaults = parseThemeDefaults();
		write(out, HtmlPageRenders.outFirstLine(exec, page)); //might null
		write(out, HtmlPageRenders.outDocType(exec, page)); //might null
		final Double number = exec.getBrowser("mobile");

		out.write("<html");
		if (!containsLangAttribute(rootAttrs))
			out.write(" lang=\"" + Locales.getCurrent().toLanguageTag() + "\"");
		write(out, rootAttrs);
		write(out, outThemeRootAttributes(themeDefaults, rootAttrs));
		if (number == null || number.intValue() == 0) {
			out.write(">\n<head>\n"
					// B70-ZK-2065: Remove meta for validation.
					//	+ "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n"
					//	+ "<meta http-equiv=\"Expires\" content=\"-1\" />\n"
					+ "<title>");
		} else {
			out.write(">\n<head>\n");
			// B70-ZK-2065: Remove meta for validation.
			//	+ "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n"
			//	+ "<meta http-equiv=\"Expires\" content=\"-1\" />\n");

			String viewport = page.getViewport();
			if (!"auto".equals(viewport))
				out.write("<meta name=\"viewport\" content=\"" + Encode.forHtml(viewport) + "\" > \n");
			else if (!"true".equals(Library.getProperty("org.zkoss.zul.tablet.meta.viewport.disabled", "false")))
				out.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" > \n");
			out.write("<title>");
		}
		write(out, Encode.forHtml(page.getTitle()));
		out.write("</title>\n");
		outHeaders(exec, page, out, themeDefaults);
		out.write("</head>\n");

		out.write("<body>\n");
		HtmlPageRenders.outPageContent(exec, page, out, false);
		writeln(out, HtmlPageRenders.outUnavailable(exec));

		WebApp webApp = exec.getDesktop().getWebApp();
		Object notice = webApp.getAttribute("org.zkoss.zk.ui.client.notice");
		if (notice instanceof String) {
			out.write((String) notice);
		}
		out.write("\n</body>\n</html>\n");
	}

	private static void outHeaders(Execution exec, Page page, Writer out, MarbleThemeDefaults themeDefaults)
			throws IOException {
		out.write(HtmlPageRenders.outHeaders(exec, page, true));
		//F70-ZK-2495: place init-crash-script before zk.wpd
		out.write(HtmlPageRenders.outInitCrashScript(exec, null));
		out.write(HtmlPageRenders.outLangJavaScripts(exec, null, null));
		out.write(HtmlPageRenders.outLangStyleSheets(exec, null, null));
		//ZK-6112: after the theme stylesheets so the seed wins, before the page's own headers so they can override it
		write(out, outThemeStyle(themeDefaults));
		out.write(HtmlPageRenders.outHeaders(exec, page, false));
	}

	/** Reads the Marble defaults configured in zk.xml (ZK-6112). An invalid value is logged and ignored.
	 */
	static MarbleThemeDefaults parseThemeDefaults() {
		String density = null, brand = null, primaryColor = null;

		String value = trimToNull(Library.getProperty(DENSITY_PROPERTY));
		if (value != null) {
			final Density d = findDensity(value);
			if (d == null)
				log.warn("Ignored {}={}: expected comfortable or compact", DENSITY_PROPERTY, value);
			else if (d != Density.COMFORTABLE)
				density = d.token();
		}

		value = trimToNull(Library.getProperty(BRAND_PROPERTY));
		if (value != null) {
			if (HEX_COLOR_PATTERN.matcher(value).matches()) {
				primaryColor = value;
			} else {
				final Brand b = findBrand(value);
				if (b == null)
					log.warn("Ignored {}={}: expected #rgb, #rrggbb or a preset name", BRAND_PROPERTY, value);
				else if (b != Brand.DEFAULT)
					brand = b.token();
			}
		}
		return new MarbleThemeDefaults(density, brand, primaryColor);
	}

	/** Returns the attributes to append to the html element, or null if none.
	 * An attribute already given by the page's root-attributes directive wins.
	 */
	static String outThemeRootAttributes(MarbleThemeDefaults themeDefaults, String rootAttrs) {
		final StringBuilder sb = new StringBuilder();
		if (themeDefaults.density != null && !containsAttribute(rootAttrs, DENSITY_ATTRIBUTE_PATTERN))
			sb.append(" data-density=\"").append(themeDefaults.density).append('"');
		if (themeDefaults.brand != null && !containsAttribute(rootAttrs, BRAND_ATTRIBUTE_PATTERN))
			sb.append(" data-brand=\"").append(themeDefaults.brand).append('"');
		return sb.length() > 0 ? sb.toString() : null;
	}

	/** Returns the style overriding the primary seed, or null if no brand color is configured.
	 * It must follow the theme stylesheets.
	 */
	static String outThemeStyle(MarbleThemeDefaults themeDefaults) {
		if (themeDefaults.primaryColor == null)
			return null;
		return HtmlPageRenders.outCspNonceAttr(
				"<style>:root{--zk-color-primary:" + themeDefaults.primaryColor + "}</style>\n");
	}

	private static Density findDensity(String token) {
		for (Density d : Density.values())
			if (d.token().equalsIgnoreCase(token))
				return d;
		return null;
	}

	private static Brand findBrand(String token) {
		for (Brand b : Brand.values())
			if (b.token().equalsIgnoreCase(token))
				return b;
		return null;
	}

	private static String trimToNull(String s) {
		if (s == null)
			return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	private static boolean containsAttribute(String rootAttrs, Pattern pattern) {
		return rootAttrs != null && pattern.matcher(rootAttrs).find();
	}

	/** The Marble defaults read from zk.xml library properties; a null field means "not configured".
	 * Values are validated, so they are safe to render without encoding.
	 */
	static final class MarbleThemeDefaults {
		/** The data-density token, e.g. "compact". */
		final String density;
		/** The data-brand preset token, e.g. "slate"; exclusive with {@link #primaryColor}. */
		final String brand;
		/** A validated #rgb or #rrggbb primary seed. */
		final String primaryColor;

		MarbleThemeDefaults(String density, String brand, String primaryColor) {
			this.density = density;
			this.brand = brand;
			this.primaryColor = primaryColor;
		}
	}

	private static void write(Writer out, String s) throws IOException {
		if (s != null)
			out.write(s);
	}

	private static void writeln(Writer out, String s) throws IOException {
		if (s != null) {
			out.write(s);
			out.write('\n');
		}
	}

	private static boolean containsLangAttribute(String rootAttrs) {
		return rootAttrs != null && LANG_ATTRIBUTE_PATTERN.matcher(rootAttrs).find();
	}

	/** Renders the page if {@link Page#isComplete} is false.
	 *
	 * @param au whether it is caused by an asynchronous update
	 */
	protected void renderPage(Execution exec, Page page, Writer out, boolean au) throws IOException {
		if (!au) {
			out.write(HtmlPageRenders.outLangStyleSheets(exec, null, null));
			out.write(HtmlPageRenders.outLangJavaScripts(exec, null, null));
		}

		HtmlPageRenders.outPageContent(exec, page, out, au);
		if (!au && ((PageCtrl) page).getOwner() == null)
			writeln(out, HtmlPageRenders.outUnavailable(exec));
	}

	/** Renders the page if {@link Page#isComplete} is true.
	 * In other words, the page content contains HTML/BODY tags.
	 */
	protected void renderComplete(Execution exec, Page page, Writer out) throws IOException {
		write(out, HtmlPageRenders.outFirstLine(exec, page)); //might null
		write(out, HtmlPageRenders.outDocType(exec, page)); //might null
		HtmlPageRenders.setContentType(exec, page);

		for (Component root = page.getFirstRoot(); root != null; root = root.getNextSibling())
			((ComponentCtrl) root).redraw(out);

		write(out, HtmlPageRenders.outHeaderZkTags(exec, page));
		writeln(out, HtmlPageRenders.outUnavailable(exec));
	}
}
