/* BindExpressionFactoryImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 15, 2011 11:07:43 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import org.zkoss.bind.xel.BindXelFactory;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.impl.ExpressionFactoryImpl;
import org.zkoss.zel.impl.lang.ExpressionBuilder;
/**
 * Handle dot series script.
 * @author henrichen
 * @see BindExpressionBuilder
 * @see BindXelFactory
 * @since 6.0.0
 */
public class BindExpressionFactoryImpl extends ExpressionFactoryImpl {
    //20110815, Henri Chen: allow override node visiting (see BindExpressionBuilder#visit)
    protected ExpressionBuilder newExpressionBuilder(String expression, ELContext context) {
    	return new BindExpressionBuilder(expression, context);
    }
}
