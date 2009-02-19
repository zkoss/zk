/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  4 09:39:33     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.util.Maps;
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
	/** Used with {@link #parseComposite} to indicate the expression
	 * is a scalar value.
	 * In other words, it won't handle it specially. If a
	 */
	public static final int SCALAR = 0;
	/** Used with {@link #parseComposite} to indicate the expression
	 * is a vector, i.e., a list separated with comma.
	 * Example: "first, ${second}".
	 */
	public static final int LIST = 1;
	/** Used with {@link #parseComposite} to indicate the expression
	 * is a map, i.e., a map paired with equal and
	 * separated with comma.
	 * Example: "one=first, two=${second}".
	 */
	public static final int MAP = 2;
	/** Parses a list of expressions that is separated by comma.
	 * For example, parseList("${a}, b, ${c}", Object.class)
	 * will return a three-element array.
	 *
	 * @param ignoreEmpty whether to return null if expr is an empty expression.
	 * @return an array of the parsed expressions (at least with one element),
	 * or null if expr is null.
	 * @see #parseComposite
	 */
	public static ExValue[] parseList(String expr, Class expcls,
	boolean ignoreEmpty) {
		if (expr == null)
			return null;

		if (expr.length() != 0) {
			List dst = new LinkedList();
			Collection src = CollectionsX.parse(null, expr, ',', true, true);
			for (Iterator it = src.iterator(); it.hasNext();) {
				final String s = (String)it.next();
				if (!ignoreEmpty || s.length() > 0)
					dst.add(new ExValue(s, expcls));
			}
			if (!dst.isEmpty())
				return (ExValue[])dst.toArray(new ExValue[dst.size()]);
		}

		return ignoreEmpty ? null: new ExValue[] {new ExValue(expr, expcls)};
	}
	/** Parses an expression which could a scalar, vector or map,
	 * depending on the specified type.
	 *
	 * <p>If type is {@link #SCALAR}, it is a simple expression and
	 * no special parsing happens. For example, "a, ${b}" will be evaluated
	 * to "a, boy" if b is "boy" if {@link #evaluateComposite} is called.</p>
	 *
	 * <p>If type is {@link #LIST}, the expression is a list separated
	 * with comma. For example, "a, ${b}" will be evaluated
	 * to a list with two elements: "a" and "boy".
	 * It is similar to {@link #parseList}.</p>
	 *
	 * <p>If type is {@link #MAP}, the expression is a list of paired
	 * entries. For example, "a=apple, b=${b}" will be evaluated
	 * to a map with two entries, ("a", "apple") and ("b", "boy").
	 *
	 * @param type one of {@link #SCALAR}, {@link #LIST} and {@link #MAP}.
	 * @return ExValue if {@link #SCALAR}, ExValue[] if {@link #LIST},
	 * Map(String, ExValue) if {@link #MAP}, or null if expr is null.
	 * To evaluate it, invoke {@link #evaluateComposite} by passing
	 * the returned value as its expr argument.
	 */
	public static Object parseComposite(String expr, Class expcls,
	int type) {
		if (expr == null)
			return null;

		if (type == LIST) {
			return parseList(expr, expcls, false);
		} else if (type == MAP) {
			Map dst = Maps.parse(new LinkedHashMap(), expr, ',', '\'', false, false, true);
			for (Iterator it = dst.entrySet().iterator(); it.hasNext();) {
				Map.Entry me = (Map.Entry)it.next();
				me.setValue(new ExValue((String)me.getValue(), expcls));
			}
			return dst;
		} else {
			return new ExValue(expr, expcls);
		}
	}

	/** Evaluates the composite expression parsed by
	 * {@link #parseComposite} against a component.
	 */
	public static
	Object evaluateComposite(Evaluator eval, Component comp, Object expr) {
		if (expr == null) {
			return null;
		} else if (expr instanceof ExValue) {
			return ((ExValue)expr).getValue(eval, comp);
		} else if (expr instanceof ExValue[]) {
			ExValue[] src = (ExValue[])expr;
			Object[] dst = new Object[src.length];
			for (int j = 0; j < src.length; ++j)
				dst[j] = src[j].getValue(eval, comp);
			return dst;
		} else {
			Map src = (Map)expr;
			Map dst = new LinkedHashMap(src.size());
			for (Iterator it = src.entrySet().iterator(); it.hasNext();) {
				Map.Entry me = (Map.Entry)it.next();
				dst.put(me.getKey(),
					((ExValue)me.getValue()).getValue(eval, comp));
			}
			return dst;
		}
	}
	/** Evaluates the composite expression parsed by
	 * {@link #parseComposite} against a page.
	 */
	public static
	Object evaluateComposite(Evaluator eval, Page page, Object expr) {
		if (expr == null) {
			return null;
		} else if (expr instanceof ExValue) {
			return ((ExValue)expr).getValue(eval, page);
		} else if (expr instanceof ExValue[]) {
			ExValue[] src = (ExValue[])expr;
			Object[] dst = new Object[src.length];
			for (int j = 0; j < src.length; ++j)
				dst[j] = src[j].getValue(eval, page);
			return dst;
		} else {
			Map src = (Map)expr;
			Map dst = new LinkedHashMap(src.size());
			for (Iterator it = src.entrySet().iterator(); it.hasNext();) {
				Map.Entry me = (Map.Entry)it.next();
				dst.put(me.getKey(),
					((ExValue)me.getValue()).getValue(eval, page));
			}
			return dst;
		}
	}
}
