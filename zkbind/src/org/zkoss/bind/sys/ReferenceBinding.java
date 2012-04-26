/* Reference.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 9:03:41 AM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import org.zkoss.bind.xel.zel.BindELContext;

/**
 * A reference to an expression.
 * @author henrichen
 * @since 6.0.0
 * @see BindELContext#getVariableMapper
 */
public interface ReferenceBinding extends LoadBinding {
	/**
	 * Returns the referenced value.
	 * 
	 * @param ctx the context to evaluate the reference expression.
	 * @return the referenced value.
	 */
	public Object getValue(BindELContext ctx); //@see BindELResolver
	
	
	/**
	 * Sets the value to referenced object
	 * @param ctx the context to evaluate the reference expression.
	 */
	public void setValue(BindELContext ctx,Object value); //@see BindELResolver
	
	/**
	 * Returns the reference expression script of this binding.
	 * @return the reference expression script of this binding. 
	 */
	public String getPropertyString();
	
	/**
	 * Invalidate cached value of this reference.
	 */
	public void invalidateCache();
}
