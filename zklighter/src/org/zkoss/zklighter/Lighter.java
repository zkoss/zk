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

import org.zkoss.lang.SystemException;
import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;
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
	private static int _cnt = 0;

	public static void main(String[] args) throws Exception {
		Log.setHierarchy(false);
		Log.lookup("org.zkoss").setLevel(Log.ERROR);

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
			else throw new IOException("Unknown "+nm+", "+el.getLocator());
		}
		System.out.println(_cnt+" files are processed successfully");
	}

	//copy//
	private static void copy(Element el) throws IOException {
		final File dst = new File(IDOMs.getRequiredElementValue(el, "destination"));
		for (Iterator it = el.getElements("source").iterator(); it.hasNext();) {
			final File src = new File(((Element)it.next()).getText(true));
			if (dst.isFile())
				throw new IOException("Directory required: "+dst);
			dst.mkdirs();
			++_cnt;
			Files.copy(dst, src, Files.CP_UPDATE|Files.CP_SKIP_SVN);
		}
	}

	//JS//
	private static void genJS(WpdExtendlet wpd, Element el) throws IOException {
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
	String locale, boolean debugJS) throws IOException {
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

		++_cnt;
		dst.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(dst);
		try {
			for (Iterator it = srcs.iterator(); it.hasNext();)
				out.write(wpd.service((File)it.next()));
		} catch (Throwable ex) {
			out.close();
			if (ex instanceof IOException) throw (IOException)ex;
			throw SystemException.Aide.wrap(ex);
		}
	}

	//CSS//
	private static void genCSS(Element el) throws IOException {
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
		ci.pure = false;
		final List browsers = new LinkedList();
		for (Iterator it = el.getElements("browser").iterator(); it.hasNext();) {
			browsers.add(((Element)it.next()).getText(true));
		}

		//merge all browser CSS into one
		ci.pure = true;
		outCombinedCSS(dst, srcs, browsers, ci);
	}
	private static
	void outCombinedCSS(File dst, List srcs, List browsers, CSSInfo ci)
	throws IOException {
		++_cnt;
		Writer out = new FileWriter(dst, "UTF-8");
		try {
			boolean ignoreInclude = false;
			for (Iterator ito = browsers.iterator(); ito.hasNext();) {
				final String browser = (String)ito.next();
				for (Iterator it = srcs.iterator(); it.hasNext();) {
					File src = (File)it.next();
					outCSS(out, renByBrowser(src, browser), ci, ignoreInclude);
				}
				ignoreInclude = true;
			}
		} finally {
			out.close();
		}
	}
	private static void outCSS(File dst, List srcs, String browser, CSSInfo ci)
	throws IOException {
		++_cnt;
		dst = renByBrowser(dst, browser);
		Writer out = new FileWriter(dst, "UTF-8");
		try {
			for (Iterator it = srcs.iterator(); it.hasNext();) {
				File src = (File)it.next();
				outCSS(out, renByBrowser(src, browser), ci, false);
			}
		} finally {
			out.close();
		}
	}
	private static File renByBrowser(File fl, String browser) {
		final String nm = fl.getName();
		final int j = nm.indexOf('.', nm.lastIndexOf('/') +1); 
		return new File(fl.getParent(),
			j >= 0 ? nm.substring(0, j) + browser + nm.substring(j):
				nm + browser);
	}
	private static
	void outCSS(Writer out, File src, CSSInfo ci, boolean ignoreInclude)
	throws IOException {
		ci.source = src;
		ci.lineno = 1;
		final String in = Files.readAll(new FileReader(src, "UTF-8")).toString();
		for (int j = 0, len = in.length(); j < len; ++j) {
			char cc = in.charAt(j);
			if (cc == '$' && j + 1 < len && in.charAt(j + 1) == '{') {
				int k = in.indexOf('}', j += 2);
				if (k < 0)
					throw new IOException(ci.message("Non-terminated EL"));
				outEL(out, in.substring(j, k), ci);
				j = k;
			} else if (cc == '<' && j + 2 < len) {
				cc = in.charAt(++j);
				int k = in.indexOf('>', ++j);
				if (k < 0)
					throw new IOException(ci.message("Non-terminated <"));

				if (cc == 'c' && in.charAt(j++) == ':') { //restrict but safer
					outDirective(out, in.substring(j, k), ci, ignoreInclude);
				} else if (cc != '/' && cc != '%')
					throw new IOException(ci.message("Unknown <"+cc));
				j = k;
			} else {
				if (cc == '\n') ++ci.lineno;
				out.write(cc);
			}
		}
	}
	private static void outEL(Writer out, String cnt, CSSInfo ci)
	throws IOException {
		int j = cnt.indexOf("encodeURL");
		if (j < 0) {
			if (ci.pure) {
				String s = (String)ci.vars.get(cnt);
				if (s != null)
					out.write(s);
				else if (isFormula(cnt)) //formula
					out.write('?'); //mark error
				else
					throw new IOException(ci.message("Unknown EL, ${"+ cnt+"}"));
			} else {
				if (isFormula(cnt)) //formula
					out.write('?'); //mark error
				else {
					out.write("${");
					out.write(cnt);
					out.write('}');
				}
			}
			return;
		}

		//encodeURL
		j = cnt.indexOf('\'', j);
		int k = cnt.indexOf('\'', ++j);
		if (j <= 0 || k < 0)
			throw new IOException(ci.message("Unknown EL, ${"+ cnt+"}: '...' not found"));
		cnt = cnt.substring(j, k);
		for (Iterator it = ci.translates.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String nm = (String)me.getKey();
			j = cnt.indexOf(nm);
			if (j >= 0) {
				cnt = cnt.substring(0, j) + me.getValue() + cnt.substring(j + nm.length());
				break; //done
			}
		}
		out.write(cnt);
	}
	private static boolean isFormula(String s) {
		for (int j = s.length(); --j >= 0;) {
			char cc = s.charAt(j);
			if ((cc > 'z' || cc < 'a') && (cc > 'Z' || cc < 'A')
			&& (cc > '9' || cc < '0') && cc != '_' && cc != '$')
				return true;
		}
		return false;
	}
	private static void outDirective(Writer out, String cnt, CSSInfo ci,
	boolean ignoreInclude)
	throws IOException {
		int j = cnt.indexOf(' ');
		if (j < 0) j = cnt.length();

		String nm = cnt.substring(0, j);
		if ("include".equals(nm)) {
			if (ignoreInclude) return; //nothing to do

			j = cnt.indexOf("page=\"", j);
			if (j < 0)
				throw new IOException(ci.message("The page attribute not found"));
			nm = cnt.substring(j += 6, cnt.indexOf('"', j));
			if (!nm.startsWith("~./"))
				throw new IOException(ci.message("Unknown URI: "+nm));
			nm = nm.substring(2);
			j = nm.length();

			for (String path = ci.source.getPath().replace('\\', '/');;) {
				int k = nm.lastIndexOf('/', j);
				if (k < 0)
					throw new IOException(ci.message("Unmatched URI: "+nm));
				final String s = nm.substring(0, k + 1);
				j = path.indexOf(s);
				if (j >= 0) { //found
					nm = path.substring(0, j + s.length()) + nm.substring(k + 1);
					break;
				}
				j = k - 1;
			}

			int oldln = ci.lineno;
			File oldsrc = ci.source;
			try {
				outCSS(out, new File(nm), ci, false);
			} finally {
				ci.lineno = oldln;
				ci.source = oldsrc;
			}
		} else if ("choose".equals(nm) || "when".equals(nm) || "otherwise".equals(nm)
		|| "if".equals(nm) || "set".equals(nm)) {
			out.write("//?");
			out.write(nm);
		} else
			throw new IOException(ci.message("Unknown <c:"+nm));
	}
	private static class CSSInfo {
		private final Map vars = new HashMap();
		private final Map translates = new LinkedHashMap();
		/** Whether to generate pure CSS, i.e., containing no ${xxx}. */
		private boolean pure;
		/** The current line number in {@link #source}. */
		private int lineno;
		/** The source file to parse. */
		private File source;

		private CSSInfo(Element el) {
			for (Iterator it = el.getElements("variable").iterator(); it.hasNext();) {
				final Element e = (Element)it.next();
				this.vars.put(IDOMs.getRequiredAttributeValue(e, "name"), e.getText(true));
			}

			el = el.getElement("encodeURL");
			if (el == null)
				return;

			for (Iterator it = el.getElements("translate").iterator(); it.hasNext();) {
				final Element e = (Element)it.next();
				this.translates.put(IDOMs.getRequiredAttributeValue(e, "from"),
					IDOMs.getRequiredAttributeValue(e, "to"));
			}
		}
		private String message(String msg) {
			return msg + " at line "+lineno+", "+source;
		}
	}
}
