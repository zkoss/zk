/* SimpleSpinnerConstraint.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 14 10:26:55 TST 2008, Created by gracelin

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;

import javax.swing.SpinnerNumberModel;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * A simple spinner constraint.
 * 
 * @author gracelin
 * @since 3.5.0
 */
public class SimpleSpinnerConstraint extends SimpleConstraint {
	private Integer _min;
	private Integer _max;

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

	/**
	 * Returns the minimum value.
	 */
	public Integer getMin() {
		return _min;
	}

	/**
	 * Set the minimum value.
	 */
	public void setMin(Integer min) {
		_min = min;
	}

	/**
	 * Returns the maximum value.
	 */
	public Integer getMax() {
		return _max;
	}

	/**
	 * Set the maximum value.
	 */
	public void setMax(Integer max) {
		_max = max;
	}

	// super//
	protected int parseConstraint(String constraint) throws UiException {
		int minIndex = constraint.indexOf("min");
		int maxIndex = constraint.indexOf("max");

		try {
			if (minIndex >= 0 && maxIndex >= 0) { // have "min" & "max"
				if (maxIndex > minIndex) { // min first
					_min = new Integer(constraint.substring(minIndex + 3,
							maxIndex).trim());
					_max = new Integer(constraint.substring(maxIndex + 3)
							.trim());
				} else { // max first
					_min = new Integer(constraint.substring(minIndex + 3)
							.trim());
					_max = new Integer(constraint.substring(maxIndex + 3,
							minIndex).trim());
				}
				if (_min.compareTo(_max) > 0)
					throw new UiException("Constraint error: " + _min + " > "
							+ _max);
				return 0;
			} else if (minIndex >= 0) { // only have "min"
				_min = new Integer(constraint.substring(minIndex + 3).trim());
				return 0;
			} else if (maxIndex >= 0) { // only have "max"
				_max = new Integer(constraint.substring(maxIndex + 3).trim());
				return 0;
			}
		} catch (NumberFormatException e) {
			throw new UiException("Constraint error: " + constraint);
		}
		return super.parseConstraint(constraint);
	}
	
	public void validate(Component comp, Object value)
			throws WrongValueException {
		if (value instanceof Integer) {
			final Integer intValue = (Integer) value;

			if (_min != null && _min.compareTo(intValue) > 0)
				throw outOfRangeValue(comp);
			if (_max != null && _max.compareTo(intValue) < 0)
				throw outOfRangeValue(comp);
		}
		super.validate(comp, value);
	}

	private WrongValueException outOfRangeValue(Component comp) {
		final String errmsg = getErrorMessage(comp);
		if (errmsg != null)
			return new WrongValueException(comp, errmsg);

		final String s =
			_min != null ? _max != null ?
				_min + " ~ " + _max: ">= " + _min: "<= " + _max;
		return new WrongValueException(comp, MZul.OUT_OF_RANGE, s);
	}
}
