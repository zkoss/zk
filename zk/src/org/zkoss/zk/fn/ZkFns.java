/* ZkFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun  7 11:09:48     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DecimalFormatSymbols;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;
import org.zkoss.util.CacheMap;
import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.servlet.JavaScript;
import org.zkoss.web.servlet.StyleSheet;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.ThemeProvider;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.device.Device;

/**
 * Utilities for using EL.
 *
 * @author tomyeh
 */
public class ZkFns {
	private static final Log log = Log.lookup(ZkFns.class);

	/** Denotes whether style sheets are generated for this request. */
	private static final String ATTR_LANG_CSS_GENED
		= "javax.zkoss.zk.lang.css.generated";
		//Naming with javax to be able to shared among portlets
	/** Denotes whether JavaScripts are generated for this request. */
	private static final String ATTR_LANG_JS_GENED
		= "javax.zkoss.zk.lang.js.generated";
		//Naming with javax to be able to shared among portlets

	protected ZkFns() {}

	/** Redraw the specified component into the specified out.
	 *
	 * @param comp the component. If null, nothing happens
	 * @param out the output. If null, the current output
	 * will be used.
	 */
	public static final void redraw(Component comp, Writer out)
	throws IOException {
		if (comp == null)
			return; //nothing to do
		if (out == null)
			out = getCurrentOut();
		try {
			comp.redraw(out);
		} catch (Throwable ex) {
			//Commons-el sometime eat exception, so show more info to debug
			log.realCauseBriefly("Failed to redraw "+comp, ex);
			if (ex instanceof IOException)
				throw (IOException)ex;
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Returns the current writer to generate the output.
	 * @since 3.0.0
	 */
	public static final Writer getCurrentOut() throws IOException {
		return ServletFns.getCurrentOut();
	}

	/** Returns JavaScript for handling the specified response.
	 */
	public static final
	String outResponseJavaScripts(Collection responses) {
		if (responses == null || responses.isEmpty()) return "";

		final StringBuffer sb = new StringBuffer(256)
			.append("zk.addInit(function(){\n");
		for (Iterator it = responses.iterator(); it.hasNext();) {
			final AuResponse response = (AuResponse)it.next();
			sb.append("zk.process('").append(response.getCommand())
				.append("'");

			final String[] data = response.getData();
			final int datanum = data != null ? data.length: 0;
			for (int j = 0; j < datanum; ++j) {
				sb.append(",\"");
				if (data[j] != null)
					sb.append(Strings.escape(data[j], "\"\\\n\r"));
				sb.append('"');
			}
			sb.append(");\n");
		}
		return sb.append("});").toString();
	}

	/** Returns HTML tags to include all JavaScript files and codes that are
	 * required when loading a ZUML page.
	 *
	 * <p>Note: it assumes {@link Executions#getCurrent} is available.
	 *
	 * <p>FUTURE CONSIDERATION: we might generate the inclusion on demand
	 * instead of all at once.
	 *
	 * @param dummy ignored since 3.0.6 (reserved for backward compatibility)
	 */
	public static final String outLangJavaScripts(String dummy) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_JS_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_LANG_JS_GENED, Boolean.TRUE);

		final Desktop desktop = Executions.getCurrent().getDesktop();
		final WebApp wapp = desktop.getWebApp();
		final Configuration config = wapp.getConfiguration();
		final String deviceType = desktop.getDeviceType();

		final StringBuffer sb = new StringBuffer(1536);

		final Set jses = new LinkedHashSet(37);
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();)
			jses.addAll(((LanguageDefinition)it.next()).getJavaScripts());
		for (Iterator it = jses.iterator(); it.hasNext();)
			append(sb, (JavaScript)it.next());

		sb.append("\n<script type=\"text/javascript\">\n")
			.append("zk_ver='").append(wapp.getVersion())
			.append("';\nzk.build='").append(wapp.getBuild())
			.append("';\nzkau.addURI('").append(desktop.getId())
				.append("','").append(desktop.getUpdateURI(null))
			.append("');\nzk_procto=")
				.append(config.getProcessingPromptDelay())
			.append(";\nzk_tipto=")
				.append(config.getTooltipDelay())
			.append(";\nzk_resndto=")
				.append(config.getResendDelay())
			.append(";\nzk_clkflto=")
				.append(config.getClickFilterDelay())
			.append(";\n");

		if (config.isDebugJS())
			sb.append("zk.debugJS=true;\n");
		if (config.isDisableBehindModalEnabled())
			sb.append("zk.dbModal=true;\n");

		if (config.isKeepDesktopAcrossVisits()
		|| request.getAttribute(Attributes.NO_CACHE) == null)
			sb.append("zk.keepDesktop=true;\n");

		if (config.getPerformanceMeter() != null)
			sb.append("zk.pfmeter=true;\n");

		sb.append("zk.eru={");
		final int[] cers = config.getClientErrorReloadCodes();
		boolean first = true;
		for (int j = 0; j < cers.length; ++j) {
			final String uri = config.getClientErrorReload(cers[j]);
			if (uri != null) {
				if (first) first = false;
				else sb.append(',');

				sb.append("e").append(cers[j]).append(":'")
					.append(Strings.escape(uri, "'\\")).append('\'');
			}
		}
		sb.append("};\n");

		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			final LanguageDefinition langdef = (LanguageDefinition)it.next();

			//Generate module versions
			final Set mods = langdef.getJavaScriptModules().entrySet();
			if (!mods.isEmpty()) {
				for (Iterator e = mods.iterator(); e.hasNext();) {
					final Map.Entry me = (Map.Entry)e.next();
					sb.append("\nzk.mods[\"").append(me.getKey())
						.append("\"]=\"").append(me.getValue()).append("\";");
				}
			}
		}

		sb.append("\n</script>\n");

		final Device device = desktop.getDevice();
		String s = device.getUnavailableMessage();
		if (s != null)
			sb.append("<noscript>\n").append(s).append("\n</noscript>\n");
		s = device.getEmbedded();
		if (s != null)
			sb.append(s).append('\n');

		return sb.toString();
	}

	private static void append(StringBuffer sb, JavaScript js) {
		sb.append("\n<script type=\"text/javascript\"");
		if (js.getSrc() != null) {
			String url;
			try {
				url = ServletFns.encodeURL(js.getSrc());
			} catch (javax.servlet.ServletException ex) {
				throw new UiException(ex);
			}

			//Note: Jetty might encode jessionid into URL, which
			//Dojo cannot handle, so we have to remove it
			int j = url.lastIndexOf(';');
			if (j > 0 && url.indexOf('.', j + 1) < 0
			&& url.indexOf('/', j + 1) < 0)
				url = url.substring(0, j);

			sb.append(" src=\"").append(url).append('"');
			final String charset = js.getCharset();
			if (charset != null)
				sb.append(" charset=\"").append(charset).append('"');
			sb.append('>');
		} else {
			sb.append(">\n").append(js.getContent());
		}
		sb.append("\n</script>");
	}
	/** Returns HTML tags to include all style sheets that are
	 * defined in all languages.
	 *
	 * <p>Note: it assumes {@link Executions#getCurrent} is available.
	 *
	 * <p>In addition to style sheets defined in lang.xml and lang-addon.xml,
	 * it also include:
	 * <ol>
	 * <li>The style sheet specified in the theme-uri parameter.</li>
	 * </ol>
	 *
	 * <p>FUTURE CONSIDERATION: we might generate the inclusion on demand
	 * instead of all at once.
	 */
	public static final String outLangStyleSheets() {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_CSS_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, Boolean.TRUE);

		final StringBuffer sb = new StringBuffer(512);
		final Execution exec = Executions.getCurrent();
		for (Iterator it = getStyleSheets(exec).iterator(); it.hasNext();)
			append(sb, (StyleSheet)it.next(), exec, null);

		return sb.toString();
	}
	/** Returns HTML tags to include style sheets of the specified device
	 * of the current application.
	 *
	 * <p>Unlike {@link #outLangStyleSheets}, it uses the current
	 *  servlet context
	 * to look for the style sheets. Thus, this method can be used even
	 * if the current execution is not available ({@link Executions#getCurrent}
	 * can be null).
	 *
	 * <p>In summary:<br/>
	 * {@link #outLangStyleSheets} is used to design the component
	 * templates, while {@link #outDeviceStyleSheets} is used by DSP/JSP
	 * that does nothing with ZUML pages (i.e., not part of an execution).
	 *
	 * @param deviceType the device type, such as ajax.
	 * It can not be null.
	 * @since 3.0.2
	 */
	public static final String outDeviceStyleSheets(String deviceType) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_CSS_GENED) != null)
			return ""; //nothing to generate
		if (deviceType == null)
			throw new IllegalArgumentException();
		WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, Boolean.TRUE);

		final StringBuffer sb = new StringBuffer(256);

		final ServletContext svlctx = ServletFns.getCurrentServletContext();
		final Configuration config =
			WebManager.getWebManager(svlctx).getWebApp().getConfiguration();

		Execution exec = ExecutionsCtrl.getCurrent();
		final boolean fake = exec == null;
		if (fake)//it shall be null, but, just in case,
			ExecutionsCtrl.setCurrent(
				exec = new ExecutionImpl(
					svlctx, (HttpServletRequest)request,
					(HttpServletResponse)ServletFns.getCurrentResponse(),
					null, null));
		try {
			final List ss = getStyleSheets0(exec, config, deviceType);
			for (Iterator it = ss.iterator(); it.hasNext();)
				append(sb, (StyleSheet)it.next(), exec, null);
			return sb.toString();
		} finally {
			if (fake)
				ExecutionsCtrl.setCurrent(null);
		}
	}

	/** Returns a list of {@link StyleSheet} that shall be generated
	 * to the client for the specified execution.
	 */
	public static final List getStyleSheets(Execution exec) {
		//Process all languages
		final Desktop desktop = exec.getDesktop();
		return getStyleSheets0(exec, desktop.getWebApp().getConfiguration(),
			desktop.getDeviceType());
	}
	private static final List getStyleSheets0(Execution exec,
	Configuration config, String deviceType) {
		final Set disabled = config.getDisabledThemeURIs();
		final List sses = new LinkedList(); //a list of StyleSheet
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			final LanguageDefinition langdef = (LanguageDefinition)it.next();
			for (Iterator e = langdef.getStyleSheets().iterator(); e.hasNext();) {
				final StyleSheet ss = (StyleSheet)e.next();
				if (!disabled.contains(ss.getHref()))
					sses.add(ss);
			}
		}

		//Process configuration
		final ThemeProvider themeProvider = config.getThemeProvider();
		if (themeProvider != null) {
			final List org = new LinkedList();
			for (Iterator it =  sses.iterator(); it.hasNext();) {
				final StyleSheet ss = (StyleSheet)it.next();
				org.add(ss.getHref()); //we don't support getContent
			}

			final String[] hrefs = config.getThemeURIs();
			for (int j = 0; j < hrefs.length; ++j)
				org.add(hrefs[j]);

			sses.clear();
			final Collection res = themeProvider.getThemeURIs(exec, org);
			if (res != null) {
				for (Iterator it = res.iterator(); it.hasNext();)
					sses.add(new StyleSheet((String)it.next(), "text/css"));
			}
		} else {
			final String[] hrefs = config.getThemeURIs();
			for (int j = 0; j < hrefs.length; ++j)
				sses.add(new StyleSheet(hrefs[j], "text/css"));
		}
		return sses;
	}

	private static void append(StringBuffer sb, StyleSheet ss,
	Execution exec, Page page) {
		String href = ss.getHref();
		if (href != null) {
			try {
				if (exec != null)
					href = (String)exec.evaluate(page, href, String.class);

				if (href != null && href.length() > 0)
					sb.append("\n<link rel=\"stylesheet\" type=\"")
						.append(ss.getType()).append("\" href=\"")
						.append(ServletFns.encodeURL(href))
						.append("\"/>");
			} catch (javax.servlet.ServletException ex) {
				throw new UiException(ex);
			}
		} else {
			sb.append("\n<style");
			if (ss.getType() != null)
				sb.append(" type=\"").append(ss.getType()).append('"');
			sb.append(">\n").append(ss.getContent()).append("\n</style>");
		}
	}

	/** Converts the specified URI to absolute if necessary.
	 * Refer to {@link Execution#toAbsoluteURI}.
	 *
	 * @param skipInclude whether not to convert to an absolute URI if
	 * the current page is included by another page.
	 * When use the include directive, skipInclude shall be true.
	 */
	public static String toAbsoluteURI(String uri, boolean skipInclude) {
		return Executions.getCurrent().toAbsoluteURI(uri, skipInclude);
	}
	/** Converts the specified URI to absolute if not included by another page.
	 * It is a shortcut of {@link #toAbsoluteURI(String, boolean)} with skipInclude
	 * is true.
	 */
	public static String toAbsoluteURI(String uri) {
		return toAbsoluteURI(uri, true);
	//we preserve this method for backward compatibility (since some developers
	//might have old version core.dsp.tld
	}

	/** Returns the content that will be placed inside the header element
	 * of the specified page.
	 * For HTML, the header element is the HEAD element.
	 */
	public static final String outHeaders(Page page) {
		return ((PageCtrl)page).getHeaders();
	}
	/** Returns the content that will be generated
	 * as the attributes of the root element of the specified page.
	 * For HTML, the root element is the HTML element.
	 */
	public static final String outRootAttributes(Page page) {
		return ((PageCtrl)page).getRootAttributes();
	}

	/** Returns the content type (never null).
	 * @since 3.0.0
	 */
	public static final String outContentType(Page page) {
		String contentType = ((PageCtrl)page).getContentType();
		if (contentType == null) {
			contentType = page.getDesktop().getDevice().getContentType();
			if (contentType == null) contentType = "";
		}

		final int j = contentType.indexOf(';');
		if (j < 0) {
			final String cs = page.getDesktop().getWebApp()
				.getConfiguration().getResponseCharset();
			if (cs != null && cs.length() > 0)
				contentType += ";charset=" + cs;
		}

		return contentType;
	}
	/** Returns the doc type, or null if not available.
	 * It is null or &lt;!DOCTYPE ...&gt;.
	 * @since 3.0.0
	 */
	public static final String outDocType(Page page) {
		final String docType = ((PageCtrl)page).getDocType();
		return trimAndLF(docType != null ?
			docType: page.getDesktop().getDevice().getDocType());
	}
	/** Trims and appends a linefeed if necessary.
	 */
	private static final String trimAndLF(String s) {
		if (s != null) {
			s = s.trim();
			final int len = s.length();
			if (len > 0 && s.charAt(len-1) != '\n')
				s += '\n';
		}
		return s;
	}
	/** Returns the first line to be generated to the output,
	 * or null if no special first line.
	 */
	public static final String outFirstLine(Page page) {
		return trimAndLF(((PageCtrl)page).getFirstLine());
	}
	/** Generates Locale-dependent strings in JavaScript syntax.
	 */
	public final static String outLocaleJavaScript() {
		final Locale locale = Locales.getCurrent();
		return outNumberJavaScript(locale) + outDateJavaScript(locale);
	}
	/** Output number relevant texts.
	 */
	private final static String outNumberJavaScript(Locale locale) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		final StringBuffer sb = new StringBuffer(128);
		appendAssignJavaScript(
			sb, "zk.GROUPING", symbols.getGroupingSeparator());
		appendAssignJavaScript(
			sb, "zk.DECIMAL", symbols.getDecimalSeparator());
		appendAssignJavaScript(
			sb, "zk.PERCENT", symbols.getPercent());
		appendAssignJavaScript(
			sb, "zk.MINUS", symbols.getMinusSign());
		return sb.toString();
	}
	private final static
	void appendAssignJavaScript(StringBuffer sb, String nm, char val) {
		final char quot = val == '"' ? '\'': '"';
		sb.append(nm).append('=').append(quot).append(val).append(quot).append(";\n");
	}
	/** Output date/calendar relevant labels.
	 */
	private final static String outDateJavaScript(Locale locale) {
		synchronized (_datejs) {
			final String djs = (String)_datejs.get(locale);
			if (djs != null) return djs;
		}

		String djs = getDateJavaScript(locale);
		synchronized (_datejs) { //OK to race
			//To minimize memory use, reuse the string if they are the same
			//which is common
			for (Iterator it = _datejs.values().iterator(); it.hasNext();) {
				final String val = (String)it.next();
				if (val.equals(djs))
					djs = val; 
			}
			_datejs.put(locale, djs);
		}
		return djs;
	}
	private final static String getDateJavaScript(Locale locale) {
		final StringBuffer sb = new StringBuffer(512);

		final Calendar cal = Calendar.getInstance(locale);
		cal.clear();

		final int firstDayOfWeek = cal.getFirstDayOfWeek();
		sb.append("zk.DOW_1ST=")
			.append(firstDayOfWeek - Calendar.SUNDAY)
			.append(";\n");

		final boolean zhlang = locale.getLanguage().equals("zh");
		SimpleDateFormat df = new SimpleDateFormat("E", locale);
		final String[] sdow = new String[7], s2dow = new String[7];
		for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
			cal.set(Calendar.DAY_OF_WEEK, j);
			sdow[k] = df.format(cal.getTime());
			if (++j > Calendar.SATURDAY) j = Calendar.SUNDAY;

			if (zhlang) {
				s2dow[k] = sdow[k].length() >= 3 ?
					sdow[k].substring(2): sdow[k];
			} else {
				final int len = sdow[k].length();
				final char cc  = sdow[k].charAt(len - 1);
				s2dow[k] = cc == '.' || cc == ',' ?
					sdow[k].substring(0, len - 1): sdow[k];
			}
		}

		df = new SimpleDateFormat("EEEE", locale);
		final String[] fdow = new String[7];
		for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
			cal.set(Calendar.DAY_OF_WEEK, j);
			fdow[k] = df.format(cal.getTime());
			if (++j > Calendar.SATURDAY) j = Calendar.SUNDAY;
		}

		df = new SimpleDateFormat("MMM", locale);
		final String[] smon = new String[12], s2mon = new String[12];
		for (int j = 0; j < 12; ++j) {
			cal.set(Calendar.MONTH, j);
			smon[j] = df.format(cal.getTime());

			if (zhlang) {
				s2mon[j] = smon[0].length() >= 2 ? //remove the last char
					smon[j].substring(0, smon[j].length() -1): smon[j];
			} else {
				final int len = smon[j].length();
				final char cc  = smon[j].charAt(len - 1);
				s2mon[j] = cc == '.' || cc == ',' ?
					smon[j].substring(0, len - 1): smon[j];
			}
		}

		df = new SimpleDateFormat("MMMM", locale);
		final String[] fmon = new String[12];
		for (int j = 0; j < 12; ++j) {
			cal.set(Calendar.MONTH, j);
			fmon[j] = df.format(cal.getTime());
		}

		appendJavaScriptArray(sb, "SDOW", sdow);
		if (Objects.equals(s2dow, sdow))
			sb.append("zk.S2DOW=zk.SDOW;\n");
		else
			appendJavaScriptArray(sb, "S2DOW", s2dow);
		if (Objects.equals(fdow, sdow))
			sb.append("zk.FDOW=zk.SDOW;\n");
		else
			appendJavaScriptArray(sb, "FDOW", fdow);

		appendJavaScriptArray(sb, "SMON", smon);
		if (Objects.equals(s2mon, smon))
			sb.append("zk.S2MON=zk.SMON;\n");
		else
			appendJavaScriptArray(sb, "S2MON", s2mon);
		if (Objects.equals(fmon, smon))
			sb.append("zk.FMON=zk.SMON;\n");
		else
			appendJavaScriptArray(sb, "FMON", fmon);

		//AM/PM available since ZK 3.0
		df = new SimpleDateFormat("a", locale);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		final String[] ampm = new String[2];
		ampm[0] = df.format(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 15);
		ampm[1] = df.format(cal.getTime());
		appendJavaScriptArray(sb, "APM", ampm);

		return sb.toString();
	}
	private static final void appendJavaScriptArray(StringBuffer sb,
	String varnm, String[] vals) {
		sb.append("zk.").append(varnm).append("=[");
		for (int j = 0;;) {
			sb.append('"').append(Strings.escape(vals[j], "\\\"")).append('"');
			if (++j >= vals.length) break;
			else sb.append(',');
		}
		sb.append("];\n");
	}
	private static final CacheMap _datejs;
	static {
		_datejs = new CacheMap(8);
		_datejs.setLifetime(24*60*60*1000);
	}

	/** Returns the attributes to render a page.
	 * Used only in HTML devices.
	 * @since 3.0.6
	 */
	public static final String outPageAttrs(Page page) {
		final Desktop desktop = page.getDesktop();
		final PageCtrl pageCtrl = (PageCtrl)page;
		final Component owner = pageCtrl.getOwner();
		boolean contained = false; //included by non-ZK servlet
		if (owner == null) {
			//Bug 2001707: Don't use exec.isIncluded() though
			//exec.getNativeRequest reutrns the original request
			//(while ServletFns.getCurrentRequest returns the 'real' one
			//-- for page.dsp, it is different due to being included).
			//WebLogic's request.getAttribute (which Servlets.include
			//depends on) returns the same set of attributes for both
			// the 'real' request and the original request.

			final Execution exec = Executions.getCurrent();
			contained = exec != null
				&& exec.getAttribute("org.zkoss.zk.ui.page.included") != null;
		}

		//prepare style
		String style = page.getStyle();
		if (style == null || style.length() == 0) {
			String wd = null, hgh = null;
			if (owner instanceof HtmlBasedComponent) {
				final HtmlBasedComponent hbc = (HtmlBasedComponent)owner;
				wd = hbc.getWidth(); //null if not set
				hgh = hbc.getHeight(); //null if not set
			}

			final StringBuffer sb = new StringBuffer(32);
			HTMLs.appendStyle(sb, "width", wd != null ? wd: "100%");
			HTMLs.appendStyle(sb, "height",
				hgh != null ? hgh: contained ? null: "100%");
			style = sb.toString();
		}

		final StringBuffer sb = new StringBuffer(100)
			.append(" class=\"zk\"");
		HTMLs.appendAttribute(sb, "id", page.getUuid());
		HTMLs.appendAttribute(sb, "z.dtid", desktop.getId());
		HTMLs.appendAttribute(sb, "style", style);
		HTMLs.appendAttribute(sb, "z.zidsp", contained ? "ctpage": "page");

		return sb.toString();
	}
}
