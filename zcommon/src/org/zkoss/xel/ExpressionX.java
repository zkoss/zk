/* ExpressionX.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 9:23:14 AM, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.xel;

/**
 * An extension of {@link Expression} to have more control to handle Expression Language EL 2.2 specs.
 *
 * <p>With {@link ExpressionX}, you are allow to set value into property resolved by this expression.</p>
 * <p>Notice it is available only <a href="http://code.google.com/p/zel/">ZEL 2.2</a>
 * is used. Please refer to {@link Expressions#newExpressionFactory} for more information.
 * @author henri
 * @since 5.5.0
 */
public interface ExpressionX extends Expression {
	/** Returns whether this is a read only (cannot setValue) expression.
	 * @param ctx the evaluation context
	 * @return whether this is a read only (cannot setValue) expression.
	 */
	public boolean isReadOnly(XelContext ctx)
	throws XelException;
	
	/** Set values into the property resolved by this expression.
	 * @param ctx the evaluation context
	 * @param value the value to be set into the property resolved by this expression
	 */
	public void setValue(XelContext ctx, Object value)
	throws XelException;
	
	/** Returns the expression in String form.
	 *  
	 * @return the expression in String form.
	 */
	public String getExpressionString();
	
	/** 
	 * Returns the result type of this expression.
	 * 
	 * @param ctx the evaluation context
	 * @return the result type of this expression.
	 */
	public Class getType(XelContext xelc);
	
	/**
	 * Returns the target bean and field name of this expression.
	 * @param xelc the evaluation context
	 * @return the target bean and field name of this expression.
	 */
	public ValueReference getValueReference(XelContext xelc);
}
