/* SimpleDoubleSpinnerConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 17, 2010 10:04:58 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A simple double spinner constraint.
 * @author jumperchen
 * @since 5.0.6
 */
public class SimpleDoubleSpinnerConstraint extends SimpleNumberInputConstraint<Double> {

	/** Constraints a constraint.
	 *
	 */
	public SimpleDoubleSpinnerConstraint() {
		super(0);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public SimpleDoubleSpinnerConstraint(int flags) {
		super(flags);
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	public SimpleDoubleSpinnerConstraint(String constraint) {
		super(constraint);
	}

	@Override
	protected Double parseValue(String value) {
		return Double.parseDouble(value);
	}
}
