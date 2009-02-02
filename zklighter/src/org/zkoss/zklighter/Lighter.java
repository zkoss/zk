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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

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
			else
				throw new Exception("Unknown "+nm+", "+el.getLocator());
		}
	}
	public static void genJS(WpdExtendlet wpd, Element el) throws Exception {
		final String dst = IDOMs.getRequiredElementValue(el, "destination");
		final File dstfl = new File(dst);
		final List srcs = new LinkedList();
		for (Iterator it = el.getElements("source").iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			final File fl = new File(e.getText(true));
			if (!fl.exists())
				throw new FileNotFoundException("Not found: "+fl+", "+e.getLocator());
			srcs.add(fl);
		}
		for (Iterator it = srcs.iterator(); it.hasNext();)
			wpd.service((File)it.next());
	}
	public static void genCSS(Element el) throws Exception {
	}
}
