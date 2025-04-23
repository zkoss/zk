/* SimpleSpinnerConstraint.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 14 10:26:55 TST 2008, Created by gracelin

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A simple spinner constraint.
 * 
 * @author gracelin
 * @since 3.5.0
 */
public class SimpleSpinnerConstraint extends SimpleNumberInputConstraint<Integer> {

	/** Constraints a constraint.
	 *
	 */
	public SimpleSpinnerConstraint() {
		super(0);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public SimpleSpinnerConstraint(int flags) {
		super(flags);
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	public SimpleSpinnerConstraint(String constraint) {
		super(constraint);
	}

	@Override
	protected Integer parseValue(String value) {
		return Integer.parseInt(value);
	}
}
