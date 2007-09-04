/* ApacheELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  4 14:02:44     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.el;

import javax.servlet.jsp.el.ExpressionEvaluator;

/**
 * An implemetation that is based on Apache commons-el:
 * org.apache.commons.el.ExpressionEvaluatorImpl.
 *
 * <p>{@link ELFactory} is recommended since the implementation
 * it encapsulates has the better performance.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ApacheELFactory extends ELFactory {
	protected ExpressionEvaluator newExpressionEvaluator() {
		return new org.apache.commons.el.ExpressionEvaluatorImpl();
	}
}
