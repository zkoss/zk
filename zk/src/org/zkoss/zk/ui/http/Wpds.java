/* Wpds.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 17 22:10:49     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.idom.Document;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.util.CacheMap;
import org.zkoss.util.Locales;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.MessageLoader;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.xel.Evaluator;

/**
 * Utilities to used with WPD files.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class Wpds {
	/** Generates all widgets in the specified language.
	 * @param lang the language to look at
	 */
	public static String outWidgetListJavaScript(String lang) {
		final StringBuffer sb = new StringBuffer(4096)
			.append("zk.wgt.WidgetInfo.register([");

		boolean first = true;
		for (Iterator it = LanguageDefinition.lookup(lang)
		.getComponentDefinitions().iterator(); it.hasNext();) {
			final ComponentDefinition compdef = (ComponentDefinition)it.next();
			for (Iterator e = compdef.getMoldNames().iterator(); e.hasNext();) {
				final String mold = (String)e.next();
				final String wgtcls = compdef.getWidgetClass(null, mold);
				if (wgtcls != null) {
					if (first) first = false;
					else sb.append(',');
					sb.append('\'').append(wgtcls).append('\'');
				}
			}
		}

		return sb.append("]);").toString();
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
		appendAssignJavaScript(
			sb, "zk.PER_MILL", symbols.getPerMill());
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
		final int firstDayOfWeek = Utils.getFirstDayOfWeek();
		final String djkey = locale + ":" + firstDayOfWeek;
		synchronized (_datejs) {
			final String djs = _datejs.get(djkey);
			if (djs != null) return djs;
		}

		String djs = getDateJavaScript(locale, firstDayOfWeek);
		synchronized (_datejs) { //OK to race
			//To minimize memory use, reuse the string if they are the same
			//which is common
			for (String val: _datejs.values()) {
				if (val.equals(djs))
					djs = val; 
			}
			_datejs.put(djkey, djs);
		}
		return djs;
	}
	private final static String getDateJavaScript(Locale locale, int firstDayOfWeek) {
		final StringBuffer sb = new StringBuffer(512);
		final Calendar cal = Calendar.getInstance(locale);
		cal.clear();

		if (firstDayOfWeek < 0)
			firstDayOfWeek = cal.getFirstDayOfWeek();
		sb.append("zk.DOW_1ST=")
			.append(firstDayOfWeek - Calendar.SUNDAY)
			.append(";\n");

		//Note: no need to df.setTimeZone(TimeZones.getCurrent()) since
		//it is used to generate locale-dependent labels

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
		df = new SimpleDateFormat("G", locale);
		final boolean helang = locale.getLanguage().equals("iw");
		if (helang) //Bug ZK-1353: Era designator contains double quote in Hebrew language
			sb.append("zk.ERA='").append(df.format(new java.util.Date())).append("';\n");
		else
			sb.append("zk.ERA=\"").append(df.format(new java.util.Date())).append("\";\n");
		

		Calendar ec = Calendar.getInstance(Locale.ENGLISH);
		Calendar lc = Calendar.getInstance(locale);
		sb.append("zk.YDELTA=").append(lc.get(Calendar.YEAR) - ec.get(Calendar.YEAR)).append(";\n");
		
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
			sb.append('\'')
				.append(Strings.escape(vals[j], Strings.ESCAPE_JAVASCRIPT))
				.append('\'');
			if (++j >= vals.length) break;
			else sb.append(',');
		}
		sb.append("];\n");
	}
	private static final CacheMap<String, String> _datejs;
	static {
		_datejs = new CacheMap<String, String>(8);
		_datejs.setLifetime(24*60*60*1000);
	}
	
	/**
	 * Generates Locale-dependent strings in JavaScript syntax.
	 * @since 6.5.1
	 */
	public static final String outLocaleJavaScript(ServletRequest request, ServletResponse response) 
	throws IOException {	
		final StringBuffer result = new StringBuffer(4096);
		final WebApp webApp = WebApps.getCurrent();
		final Execution exec = new FakeExecution(
			webApp.getServletContext(), request, response, null, null
		);

		//the same as AjaxDevice.reloadMessages()
		result.append(Devices.loadJavaScript(exec, "~./js/zk/lang/msgzk*.js"));
		result.append(Wpds.outLocaleJavaScript());
		for (LanguageDefinition langdef : LanguageDefinition.getByDeviceType("ajax"))
			//WpdExtendlet.getDeviceType() also return "ajax" directly...
			for (MessageLoader loader : langdef.getMessageLoaders())
				loader.load(result, exec);

		return result.toString();
	}
	
	private final static class FakeExecution extends ExecutionImpl {		
		FakeExecution(ServletContext ctx, ServletRequest request, ServletResponse response,
		Desktop desktop, Page creating) {
			super(ctx, (HttpServletRequest)request, (HttpServletResponse)response, desktop, creating);
		}
				
		//use AbstractExecution
		@Override public void onActivate() {}
		@Override public void onDeactivate() {}
		
		//use PhantomExecution		
		@Override public Evaluator getEvaluator(Page page, Class<? extends ExpressionFactory> expfcls) {
			return null;
		}
		
		@Override public Evaluator getEvaluator(Component page, Class expfcls) {
			return null;
		}
		
		@Override public Object evaluate(Component comp, String expr, Class expectedType) {
			throw new UnsupportedOperationException();
		}
		
		@Override public Object evaluate(Page page, String expr, Class expectedType) {
			throw new UnsupportedOperationException();
		}
		
		@Override public void include(Writer out, String page, Map<String, ?> params, int mode)
		throws IOException {
			throw new UnsupportedOperationException();
		}
		
		@Override public void include(String page) throws IOException {
			throw new UnsupportedOperationException();
		}
		
		@Override public void forward(Writer out, String page, Map<String, ?> params, int mode)
		throws IOException {
			throw new UnsupportedOperationException();
		}
		
		@Override public void forward(String page) throws IOException {
			throw new UnsupportedOperationException();
		}
		
		@Override public PageDefinition getPageDefinition(String uri) {
			throw new UnsupportedOperationException();
		}
		
		@Override public PageDefinition getPageDefinitionDirectly(String content, String ext) {
			throw new UnsupportedOperationException();
		}
		
		@Override public PageDefinition getPageDefinitionDirectly(Document content, String ext) {
			throw new UnsupportedOperationException();
		}
		
		@Override public PageDefinition getPageDefinitionDirectly(Reader reader, String ext)
		throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override public boolean addScopeListener(ScopeListener listener) {
			throw new UnsupportedOperationException();
		}
		
		@Override public boolean removeScopeListener(ScopeListener listener) {
			throw new UnsupportedOperationException();
		}
	}
}