/* Evaluators.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 25 14:06:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;

/**
 * Utilities of evaluation.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class Evaluators {
	/** Evaluates the specified expression (which might or might not
	 * contain ${).
	 *
	 * @param comp the component to represent the self variable
	 */
	public static Object evaluate(Evaluator eval, Component comp,
	String expr, Class expectedClass) {
		if (expr != null && expr.indexOf("${") >= 0) {
			return eval.evaluate(comp, eval.parseExpression(expr, expectedClass));
		} else {
			return Classes.coerce(expectedClass, expr);
		}
	}
	/** Evaluates the specified expression (which might or might not
	 * contain ${).
	 *
	 * @param page the page to represent the self variable
	 */
	public static Object evaluate(Evaluator eval, Page page,
	String expr, Class expectedClass) {
		if (expr != null && expr.indexOf("${") >= 0) {
			return eval.evaluate(page, eval.parseExpression(expr, expectedClass));
		} else {
			return Classes.coerce(expectedClass, expr);
		}
	}
}
