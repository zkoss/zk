/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 09:39:33     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.util.CollectionsX;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.ExValue;

/**
 * Utilities to handle the metainfo.
 *
 * @author tomyeh
 * @since 3.0.6
 */
public class Utils {
	/** Parses a list of expressions that is separated by comma.
	 * For example, parseList("${a}, b, ${c}", Object.class)
	 * will return a three-element array.
	 *
	 * @param ignoreEmpty whether to return null if expr is an empty expression.
	 * @return an array of the parsed expressions (at least with one element),
	 * or null if expr is null.
	 */
	public static ExValue[] parseList(String expr, Class expcls, boolean ignoreEmpty) {
		if (expr == null)
			return null;
		if (expr.length() != 0) {
			List dst = new LinkedList();
			Collection src = CollectionsX.parse(null, expr, ',', true, true);
			for (Iterator it = src.iterator(); it.hasNext();) {
				final String s = (String)it.next();
				if (s.length() > 0)
					dst.add(new ExValue(s, expcls));
			}
			if (!dst.isEmpty())
				return (ExValue[])dst.toArray(new ExValue[dst.size()]);
		}

		return ignoreEmpty ? null: new ExValue[] {new ExValue(expr, expcls)};
	}
	/** Evaluates the array of expressions against a component.
	 * <ol>
	 * <li>expr.length == 0, return null</li>
	 * <li>expr.length == 1, return expr[0].getValue()</li>
	 * <li>expr.length > 1, return an array where element i is
	 * expr[i].getValue()</li>
	 * </ol>
	 */
	public static
	Object evaluate(Evaluator eval, Component comp, ExValue[] expr) {
		if (expr == null || expr.length == 0)
			return null;
		if (expr.length == 1)
			return expr[0].getValue(eval, comp);

		Object[] ary = new Object[expr.length];
		for (int j = 0; j < expr.length; ++j)
			ary[j] = expr[j].getValue(eval, comp);
		return ary;
	}
	/** Evaluates the array of expressions against a page.
	 * <ol>
	 * <li>expr.length == 0, return null</li>
	 * <li>expr.length == 1, return expr[0].getValue()</li>
	 * <li>expr.length > 1, return an array where element i is
	 * expr[i].getValue()</li>
	 * </ol>
	 */
	public static
	Object evaluate(Evaluator eval, Page page, ExValue[] expr) {
		if (expr == null || expr.length == 0)
			return null;
		if (expr.length == 1)
			return expr[0].getValue(eval, page);

		Object[] ary = new Object[expr.length];
		for (int j = 0; j < expr.length; ++j)
			ary[j] = expr[j].getValue(eval, page);
		return ary;
	}
}
