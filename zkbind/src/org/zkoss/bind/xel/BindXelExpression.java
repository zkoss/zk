/* BindXelExpression.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 5:42:27 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel;

import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.zel.ELXelExpression;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ValueExpression;

/**
 * @author henrichen
 * @since 6.0.0
 */
public class BindXelExpression extends ELXelExpression {

	private static final long serialVersionUID = 1L;

	BindXelExpression(ValueExpression expr) {
		super(expr);
	}
	protected ELContext newELContext(XelContext xelc) {
		return new BindELContext(xelc);
	}
}
