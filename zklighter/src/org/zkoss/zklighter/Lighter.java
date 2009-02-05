/* Lighter.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 22 11:06:33     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zklighter;

import java.util.*;
import java.io.*;

import org.zkoss.util.Locales;
import org.zkoss.io.Files;
import org.zkoss.io.FileWriter;
import org.zkoss.io.FileReader;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.zk.ui.http.WpdExtendlet;

/**
 * The entry of ZK Lighter
 *
 * @author tomyeh
 */
public class Lighter {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("ZK Lighter - ZK Light JavaScript/CSS Generator\n\n"
			+"Usage:\n\tjava -classpath $CP org.zkoss.zklighter.Lighter zklighter.xml\n");
			System.exit(-1);
		}
		final WpdExtendlet wpd = new WpdExtendlet();
		for (Iterator it = new SAXBuilder(true, false, true).build(args[0])
		.getRootElement().getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String nm = el.getName();
			if ("javascript".equals(nm)) genJS(wpd, el);
			else if ("css".equals(nm)) genCSS(el);
			else if ("copy".equals(nm)) copy(el);
			else throw new Exception("Unknown "+nm+", "+el.getLocator());
		}
	}
	private static void copy(Element el) throws Exception {
		final File dst = new File(IDOMs.getRequiredElementValue(el, "destination"));
		for (Iterator it = el.getElements("source").iterator(); it.hasNext();) {
			final File src = new File(((Element)it.next()).getText(true));
			System.out.println("Copy "+src+" to "+dst);
			if (!dst.isDirectory())
				throw new IOException("Directory required: "+dst);
			Files.copy(dst, src, Files.CP_UPDATE|Files.CP_SKIP_SVN);
		}
	}
	private static void genJS(WpdExtendlet wpd, Element el) throws Exception {
		final File dst = new File(IDOMs.getRequiredElementValue(el, "destination"));
		final List srcs = new LinkedList();
		for (Iterator it = el.getElements("source").iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			final File fl = new File(e.getText(true));
			if (!fl.exists())
				throw new FileNotFoundException("Not found: "+fl+", "+e.getLocator());
			srcs.add(fl);
		}

		boolean done = false;
		for (Iterator it = el.getElements("locale").iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			final String locale = e.getText(true);
			Locales.setThreadLocal(new Locale(locale));
			outJS(wpd, dst, srcs, locale, false);
			outJS(wpd, dst, srcs, locale, true);
			Locales.setThreadLocal(null);
			done = true;
		}
		if (!done) {
			outJS(wpd, dst, srcs, null, false);
			outJS(wpd, dst, srcs, null, true);
		}
	}
	private static void outJS(WpdExtendlet wpd, File dst, List srcs,
	String locale, boolean debugJS) throws Exception {
		wpd.setDebugJS(debugJS);
		if (debugJS || locale != null) {
			final String dstnm = dst.getName();
			final int j = dstnm.lastIndexOf('.');
			final StringBuffer sb = new StringBuffer();
			if (debugJS) sb.append("src/");

			sb.append(j >= 0 ? dstnm.substring(0, j): dstnm);
			if (locale != null && locale.length() > 0)
				sb.append('_').append(locale);
			if (j >= 0)
				sb.append(dstnm.substring(j));

			dst = new File(dst.getParent(), sb.toString());
		}

		System.out.println("Generate "+dst);
		FileOutputStream out = new FileOutputStream(dst);
		try {
			for (Iterator it = srcs.iterator(); it.hasNext();)
				out.write(wpd.service((File)it.next()));
		} catch (Exception ex) {
			out.close();
		}
	}
	private static void genCSS(Element el) throws Exception {
		final File dst = new File(IDOMs.getRequiredElementValue(el, "destination"));
		final List srcs = new LinkedList();
		for (Iterator it = el.getElements("source").iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			final File fl = new File(e.getText(true));
			if (!fl.exists())
				throw new FileNotFoundException("Not found: "+fl+", "+e.getLocator());
			srcs.add(fl);
		}

		final CSSInfo ci = new CSSInfo(el);
		final List browsers = new LinkedList();
		for (Iterator it = el.getElements("browser").iterator(); it.hasNext();) {
			final String browser = ((Element)it.next()).getText(true);
			browsers.add(browser);
			ci.pure = true;
			outCSS(dst, srcs, browser, ci);
			ci.pure = false;
			outCSS(dst, srcs, browser, ci);
		}

		//merge all browser CSS into one
		System.out.println("Generate "+dst);
	}
	private static void outCSS(File dst, List srcs, String browser, CSSInfo ci)
	throws Exception {
		final String nm = dst.getName();
		final int j = nm.lastIndexOf('.');
		dst = new File(dst.getParent(),
			j >= 0 ? nm.substring(0, j) + browser + nm.substring(j):
				nm + browser);
		System.out.println("Generate "+dst);
		Writer out = new FileWriter(dst, "UTF-8");
		try {
			for (Iterator it = srcs.iterator(); it.hasNext();)
				outCSS(out, (File)it.next(), ci, false);
		} finally {
			out.close();
		}
	}
	private static
	void outCSS(Writer out, File src, CSSInfo ci, boolean ignoreInclude)
	throws Exception {
		final String in = Files.readAll(new FileReader(src, "UTF-8")).toString();
		for (int j = 0, len = in.length(); j < len; ++j) {
		}
	}
	private static class CSSInfo {
		private final Map vars = new HashMap();
		private final Map translates = new HashMap();
		private final String prefix;
		/** Whether to generate pure CSS, i.e., containing no ${xxx}. */
		private boolean pure;

		private CSSInfo(Element el) {
			for (Iterator it = el.getElements("variable").iterator(); it.hasNext();) {
				final Element e = (Element)it.next();
				this.vars.put(IDOMs.getRequiredAttributeValue(e, "name"), e.getText(true));
			}

			el = el.getElement("encodeURL");
			if (el == null) {
				this.prefix = "";
				return;
			}
			this.prefix = el.getAttribute("prefix");

			for (Iterator it = el.getElements("translate").iterator(); it.hasNext();) {
				final Element e = (Element)it.next();
				this.translates.put(IDOMs.getRequiredAttributeValue(e, "from"),
					IDOMs.getRequiredAttributeValue(e, "to"));
			}
		}
	}
}
