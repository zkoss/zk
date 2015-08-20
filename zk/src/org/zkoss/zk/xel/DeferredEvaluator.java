/* DeferredEvaluator.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 14 12:20:05 CST 2015, Created by chunfu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.xel;

import org.zkoss.zk.ui.Component;

/**
 * A ZK specific expression builder that is based on deferred expression, which syntax is like #{}.
 * @author chunfu
 * @since 8.0.0
 */
public interface DeferredEvaluator<T> {
	/**
	 * Evaluates the expression.
	 * @param comp the component which has properties with deferred expressions.
	 * @param data an arbitrary data
	 */
	public void evaluate(Component comp, T data);
}
