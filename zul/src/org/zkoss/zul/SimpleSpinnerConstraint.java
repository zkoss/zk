/* SimpleSpinnerConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 14 10:26:55 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import javax.swing.SpinnerNumberModel;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * A simple spinner constraint.
 * 
 * @author gracelin
 * @since 3.1.0
 */
public class SimpleSpinnerConstraint extends SimpleConstraint {
	private SpinnerNumberModel _model;

	/** Constraints a constraint.
	 *
	 * @param model a SpinnerNumberModel to set constraint.
	 */
	public SimpleSpinnerConstraint(SpinnerNumberModel model) {
		super(0);
		_model = (model == null ? new SpinnerNumberModel() : model);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param model a SpinnerNumberModel to set constraint.
	 */
	public SimpleSpinnerConstraint(int flags, SpinnerNumberModel model) {
		super(flags);
		_model = (model == null ? new SpinnerNumberModel() : model);
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @param model a SpinnerNumberModel to set constraint.
	 */
	public SimpleSpinnerConstraint(String constraint, SpinnerNumberModel model) {
		super(constraint);
		_model = (model == null ? new SpinnerNumberModel() : model);
	}

	/**
	 * Returns the minimum value, or null if there is no constraint of the
	 * minimum value.
	 */
	public Integer getMinimum() {
		return (Integer) _model.getMinimum();
	}

	/**
	 * Returns the maximum value, or null if therer is no constraint of the maximum
	 * value.
	 */
	public Integer getMaximum() {
		return (Integer) _model.getMaximum();
	}

	// super//
	public void validate(Component comp, Object value)
			throws WrongValueException {
		if (value instanceof Integer) {
			final Integer intValue = (Integer) value;
			
			if (_model.getMinimum() != null && _model.getMinimum().compareTo(intValue) > 0)
				throw outOfRangeValue(comp);				
			if (_model.getMaximum() != null && _model.getMaximum().compareTo(intValue) < 0)
				throw outOfRangeValue(comp);
		}
		super.validate(comp, value);
	}

	private WrongValueException outOfRangeValue(Component comp) {
		final String errmsg = getErrorMessage(comp);
		return errmsg != null ? new WrongValueException(comp, errmsg)
				: new WrongValueException(comp, MZul.OUT_OF_RANGE,
						new Object[] { _model.getMinimum().toString(),
								_model.getMaximum().toString() });
	}
}
