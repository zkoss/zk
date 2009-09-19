/* Constrainted.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:52:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

import org.zkoss.zul.Constraint;

/**
 * Decorates a component that its value is constrainted by
 * {@link Constraint}.
 *
 * @author tomyeh
 */
public interface Constrainted {
	/** Sets the constraint.
	 * <p>Default: null (means no constraint all all).
	 */
	public void setConstraint(Constraint constr);
	/** Returns the constraint, or null if no constraint at all.
	 */
	public Constraint getConstraint();
}
