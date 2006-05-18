/* Evaluator.java

{{IS_NOTE
	$Id: Evaluator.java,v 1.1 2006/04/17 07:10:05 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sun Apr 16 16:08:41     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.Component;

/**
 * An evaluator for evaluating EL expressions.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/04/17 07:10:05 $
 */
public interface Evaluator {
	/** Evaluates the specified expression against the specified component.
	 */
	public Object evaluate(Component comp, String expr, Class expectedType);
}
