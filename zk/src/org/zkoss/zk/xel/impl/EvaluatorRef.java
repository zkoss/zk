/* EvaluatorRef.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep  3 21:44:33     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.xel.Evaluator;

/**
 * A reference to {@link Evaluator}.
 * Thought it also implements {@link Evaluator}, it is different in two
 * ways:
 *
 * <ul>
 * <li>It is serializable, while {@link Evaluator} is not.</li>
 * <li>It is late-binding to the real evaluator.
 * The evaluator is accessed only {@link #evaluate}, {@link #parseExpression},
 * or {@link #getEvaluator} is called.
 * </li>
 * </ul>
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface EvaluatorRef extends Evaluator, java.io.Serializable {
	/** Returns the real evaluator.
	 */
	public Evaluator getEvaluator();
	/** Returns the page definition of this reference, or null if not
	 * available.
	 */
	public PageDefinition getPageDefinition();
}
