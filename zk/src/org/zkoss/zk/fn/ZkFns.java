/* ZkFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun  7 11:09:48     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletRequest;

import org.zkoss.lang.Strings;
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
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.device.Devices;

/**
 * Utilities for using EL.
 *
 * @author tomyeh
 */
public class ZkFns extends DspFns {
	private static final Log log = Log.lookup(ZkFns.class);

	/** Denotes whether style sheets are generated for this request. */
	private static final String ATTR_LANG_CSS_GENED
		= "javax.zkoss.zk.lang.css.generated";
		//Naming with javax to be able to shared among portlets
	/** Denotes whether JavaScripts are generated for this request. */
	private static final String ATTR_LANG_JS_GENED
		= "javax.zkoss.zk.lang.js.generated";
		//Naming with javax to be able to shared among portlets
	/** Denotes whether JavaScripts are generated for this request. */
	private static final String ATTR_DESKTOP_INFO_GENED
		= "javax.zkoss.zk.desktopInfo.generated";
	/** Denotes whether the unavailable message is generated for this request. */
	private static final String ATTR_UNAVAILABLE_GENED
		= "javax.zkoss.zk.unavail.generated";

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
			.append("\n<script type=\"text/javascript\">\n")
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
		return sb.append("});\n</script>\n").toString();
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
		return outLangJavaScripts(Executions.getCurrent(), null, null);
	}
	private static final
	String outLangJavaScripts(Execution exec, WebApp wapp, String deviceType) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_JS_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_LANG_JS_GENED, Boolean.TRUE);

		final Desktop desktop = exec.getDesktop();
		if (wapp == null) wapp = desktop.getWebApp();
		if (deviceType == null) deviceType = desktop.getDeviceType();
		final Configuration config = wapp.getConfiguration();

		final StringBuffer sb = new StringBuffer(1536);

		final Set jses = new LinkedHashSet(32);
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();)
			jses.addAll(((LanguageDefinition)it.next()).getJavaScripts());
		for (Iterator it = jses.iterator(); it.hasNext();)
			append(sb, (JavaScript)it.next());

		sb.append("\n<script type=\"text/javascript\">\n")
			.append("zk_ver='").append(wapp.getVersion())
			.append("';\nzk.build='").append(wapp.getBuild())
			.append("';\nzk_procto=")
				.append(config.getProcessingPromptDelay())
			.append(";\nzk_tipto=")
				.append(config.getTooltipDelay())
			.append(";\nzk_resndto=")
				.append(config.getResendDelay())
			.append(";\nzk_clkflto=")
				.append(config.getClickFilterDelay())
			.append(";\n");

		final Device device = Devices.getDevice(deviceType);
		if (desktop != null) { //null if called by JSP:zkhead
			Boolean autoTimeout = getAutomaticTimeout(desktop);
			if (autoTimeout != null ?
			autoTimeout.booleanValue(): device.isAutomaticTimeout()) {
				int tmout = desktop.getSession().getMaxInactiveInterval();
				if (tmout > 0) { //unit: seconds
					int extra = tmout / 8;
					tmout += extra > 180 ? 180: extra;
						//Add extra seconds to ensure it is really timeout
					sb.append("zk_tmout=").append(tmout).append(";\n");
				}
			}
		}

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

		final String s = device.getEmbedded();
		if (s != null)
			sb.append(s).append('\n');

		return sb.toString();
	}
	private static Boolean getAutomaticTimeout(Desktop desktop) {
		for (Iterator it = desktop.getPages().iterator(); it.hasNext();) {
			Boolean b = ((PageCtrl)it.next()).getAutomaticTimeout();
			if (b != null) return b;
		}
		return null;
	}
	/** Generates the unavailable message in HTML tags, if any.
	 * @since 3.5.2
	 */
	public static String outHtmlUnavailable(Page page) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_UNAVAILABLE_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_UNAVAILABLE_GENED, Boolean.TRUE);

		final Device device = page.getDesktop().getDevice();
		String s = device.getUnavailableMessage();
		return s != null ?
			"<noscript>" + s + "</noscript>": "";
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
		return outLangStyleSheets(Executions.getCurrent(), null, null);
	}
	private static final
	String outLangStyleSheets(Execution exec, WebApp wapp, String deviceType) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_CSS_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, Boolean.TRUE);

		final StringBuffer sb = new StringBuffer(512);
		for (Iterator it = getStyleSheets(exec, wapp, deviceType).iterator();
		it.hasNext();)
			append(sb, (StyleSheet)it.next(), exec, null);

		if (sb.length() > 0) sb.append('\n');
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
	 * templates, while {@link DspFns#outDeviceStyleSheets} is used by DSP/JSP
	 * that does nothing with ZUML pages (i.e., not part of an execution).
	 *
	 * @param exec the execution (never null)
	 * @param wapp the Web application.
	 * If null, exec.getDesktop().getWebApp() is used.
	 * So you have to specify it if the execution is not associated
	 * with desktop (a fake execution).
	 * @param deviceType the device type, such as ajax.
	 * If null, exec.getDesktop().getDeviceType() is used.
	 * So you have to specify it if the execution is not associated
	 * with desktop (a fake execution).
	 */
	/*package*/ static final
	String outDeviceStyleSheets(Execution exec, WebApp wapp, String deviceType) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_LANG_CSS_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_LANG_CSS_GENED, Boolean.TRUE);

		final StringBuffer sb = new StringBuffer(256);
		final List ss = getStyleSheets(exec, wapp, deviceType);
		for (Iterator it = ss.iterator(); it.hasNext();)
			append(sb, (StyleSheet)it.next(), exec, null);
		return sb.toString();
	}

	/** Returns a list of {@link StyleSheet} that shall be generated
	 * to the client for the specified execution.
	 */
	public static final List getStyleSheets(Execution exec) {
		final Desktop desktop = exec.getDesktop();
		return getStyleSheets(exec, null, null);
	}
	private static final
	List getStyleSheets(Execution exec, WebApp wapp, String deviceType) {
		final Desktop desktop = exec.getDesktop();
		if (wapp == null) wapp = desktop.getWebApp();
		if (deviceType == null) deviceType = desktop.getDeviceType();

		final Configuration config = wapp.getConfiguration();
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

	/** Returns the content of the specified condition
	 *  that will be placed inside the header element
	 * of the specified page.
	 * For HTML, the header element is the HEAD element.
	 * @param before whether to return the headers that shall be shown
	 * before ZK's CSS/JS headers.
	 * If true, only the headers that shall be shown before (such as meta)
	 * are returned.
	 * If true, only the headers that shall be shown after (such as link)
	 * are returned.
	 * @since 3.6.1
	 */
	public static final String outHeaders(Page page, boolean before) {
		return ((PageCtrl)page).getHeaders(before);
	}
	/** @deprecated As of release 3.6.1, replaced with {@link #outHeaders(Page,boolean)}.
	 */
	public static final String outHeaders(Page page) {
		return outHeaders(page, true) + outHeaders(page, false);
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

		final ServletRequest request = ServletFns.getCurrentRequest();
		WebManager.setRequestLocal(request, ATTR_DESKTOP_INFO_GENED, Boolean.TRUE);

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
		if (owner == null)
			HTMLs.appendAttribute(sb, "z.au", desktop.getUpdateURI(null));

		return sb.toString();
	}
	/** Returns the desktop info to render a desktop.
	 * It must be called if {@link #outPageAttrs} might not be called.
	 * On the other hand, {@link #outDesktopInfo} does nothing if
	 * {@link #outPageAttrs} was called.
	 *
	 * <p>It is OK to call both {@link #outPageAttrs}
	 * and {@link #outDesktopInfo}.
	 * @since 3.5.0.
	 */
	public static final String outDesktopInfo(Desktop desktop) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, ATTR_DESKTOP_INFO_GENED) != null)
			return ""; //nothing to generate
		WebManager.setRequestLocal(request, ATTR_DESKTOP_INFO_GENED, Boolean.TRUE);

		return "<script type=\"text/javascript\">\n"
			+"zkau.addDesktop(\""+desktop.getId()+"\");"
			+"zkau.addURI(\""+desktop.getId()+"\",\""+desktop.getUpdateURI(null)
			+"\");\n</script>\n";
	}

	/** Generates and returns the ZK specific HTML tags for
	 * a desktop (never null).
	 *
	 * <p>For each desktop, we have to generate a set of HTML tags
	 * to load ZK Client engine, style sheets and so on.
	 * For ZUL pages, it is generated automatically by page.dsp.
	 * However, for ZHTML pages, we have to generate these tags
	 * with special component such as org.zkoss.zhtml.Head, such that
	 * the result HTML page is legal.
	 *
	 * @return the string holding the HTML tags, or an empty string if already generated.
	 * @since 3.5.0
	 */
	public static String outZkHtmlTags() {
		final Execution exec = Executions.getCurrent();
		return exec != null ? outZkHtmlTags(exec, null, null): "";
	}
	/*package*/ static final
	String outZkHtmlTags(Execution exec, WebApp wapp, String deviceType) {
		final ServletRequest request = ServletFns.getCurrentRequest();
		if (WebManager.getRequestLocal(request, "zkHtmlTagsGened") != null)
			return null;
		WebManager.setRequestLocal(request, "zkHtmlTagsGened", Boolean.TRUE);

		final StringBuffer sb = new StringBuffer(512).append('\n')
			.append(outLangStyleSheets(exec, wapp, deviceType))
			.append(outLangJavaScripts(exec, wapp, deviceType));

		final Desktop desktop = exec.getDesktop();
		if (desktop != null)
			sb.append(outDesktopInfo(desktop));

		final String ATTR_RESPONSES = "zk_argResponses";
		sb.append(outResponseJavaScripts(
			(Collection)exec.getAttribute(ATTR_RESPONSES)));
		exec.removeAttribute(ATTR_RESPONSES);
		return sb.toString();
	}

	/**
	 * Removes the class and style attributes from the specified one.
	 * 
	 * <p>For example, if attrs is <code>href="a" class="c"</code>,
	 * then the output will be <code>href="a"</code>
	 *
	 * @param attrs the attributes to filter
	 * @since 3.5.0
	 */
	public static final String noCSSAttrs(String attrs) {
		if (attrs == null || attrs.length() == 0) return attrs;

		final String CSS = "class=\"";
		final String STYLE = "style=\"";
		StringBuffer sb = null;

		int start = attrs.indexOf(CSS);
		if (start >= 0) {
			sb = new StringBuffer(attrs);
			int end = sb.indexOf("\"", start + CSS.length());
			if (end >= 0)
				sb.delete(start, end + 1);

			start = sb.indexOf(STYLE);
		} else {
			start = attrs.indexOf(STYLE);
		}

		if (start >= 0) {
			if (sb == null) sb = new StringBuffer(attrs);
			int end = sb.indexOf("\"", start + STYLE.length());
			if (end >= 0)
				sb.delete(start, end + 1);
		}
		return sb != null ? sb.toString(): attrs;
	}
	/** Returns only the class and style attributes of the specified one.
	 *
	 * <p>For example, if attrs is <code>href="a" class="c"</code>,
	 * then the output will be <code>class="c"</code>
	 *
	 * @param attrs the attributes to filter
	 * @since 3.5.0
	 */
	public static final String outCSSAttrs(String attrs) {
		if (attrs == null || attrs.length() == 0) return attrs;

		final String CSS = "class=\"";
		final String STYLE = "style=\"";
		StringBuffer sb = null;

		int start = attrs.indexOf(CSS);
		if (start >= 0) {
			int end = attrs.indexOf("\"", start + CSS.length());
			if (end >= 0)
				sb = new StringBuffer().append(' ')
					.append(attrs.substring(start, end + 1));
		}

		start = attrs.indexOf(STYLE);
		if (start >= 0) {
			int end = attrs.indexOf("\"", start + STYLE.length());
			if (end >= 0) {
				if (sb == null) sb = new StringBuffer();
				sb.append(' ').append(attrs.substring(start, end + 1));
			}
		}
		return sb != null ? sb.toString(): "";
	}
}
