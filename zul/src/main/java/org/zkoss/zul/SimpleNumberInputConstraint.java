/* SimpleNumberInputConstraint.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 24 13:17:43 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * A simple NumberInput constraint.
 *
 * @author Jamson Chan
 * @since 10.2.0
 */
public abstract class SimpleNumberInputConstraint<T extends Number & Comparable<T>> extends SimpleConstraint {
    
    private T _min;
    private T _max;

    /** Constructs a constraint.
     */
    public SimpleNumberInputConstraint() {
        super(0);
    }

    /** Constructs a constraint.
     *
     * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
     * {@link #NO_ZERO}, and so on.
     */
    public SimpleNumberInputConstraint(int flags) {
        super(flags);
    }

    /** Constructs a constraint with a list of constraints separated by comma.
     *
     * @param constraint a list of constraints separated by comma.
     * Example: no positive, no zero
     */
    public SimpleNumberInputConstraint(String constraint) {
        super(constraint);
    }

    /**
     * Returns the minimum value.
     */
    public T getMin() {
        return _min;
    }

    /**
     * Sets the minimum value.
     */
    public void setMin(T min) {
        _min = min;
    }

    /**
     * Returns the maximum value.
     */
    public T getMax() {
        return _max;
    }

    /**
     * Sets the maximum value.
     */
    public void setMax(T max) {
        _max = max;
    }

    /**
     * Parse the string value to the specified number type T.
     * This method should be implemented by subclasses to convert a string representation
     * of a number to the appropriate numeric type for this constraint.
     *
     * @param value the string representation of a number
     * @return the parsed number of type T
     */
    protected abstract T parseValue(String value);

    // super//
    @Override
    protected int parseConstraint(String constraint) throws UiException {
        int minIndex = constraint.indexOf("min");
        int maxIndex = constraint.indexOf("max");

        try {
            if (minIndex >= 0 && maxIndex >= 0) { // have "min" & "max"
                if (maxIndex > minIndex) { // min first
                    _min = parseValue(constraint.substring(minIndex + 3, maxIndex).trim());
                    _max = parseValue(constraint.substring(maxIndex + 3).trim());
                } else { // max first
                    _min = parseValue(constraint.substring(minIndex + 3).trim());
                    _max = parseValue(constraint.substring(maxIndex + 3, minIndex).trim());
                }
                if (_min.compareTo(_max) > 0)
                    throw new UiException("Constraint error: " + _min + " > " + _max);
                return 0;
            } else if (minIndex >= 0) { // only have "min"
                _min = parseValue(constraint.substring(minIndex + 3).trim());
                return 0;
            } else if (maxIndex >= 0) { // only have "max"
                _max = parseValue(constraint.substring(maxIndex + 3).trim());
                return 0;
            }
        } catch (NumberFormatException e) {
            throw new UiException("Constraint error: " + constraint);
        }
        return super.parseConstraint(constraint);
    }

    public void validate(Component comp, Object value) throws WrongValueException {
        super.validate(comp, value);
        T typedValue;
        try {
            typedValue = (T) value;
        } catch (ClassCastException e) {
            throw new WrongValueException(comp, "Type mismatch");
        }
        if (_min != null && _min.compareTo(typedValue) > 0)
            throw outOfRangeValue(comp);
        if (_max != null && _max.compareTo(typedValue) < 0)
            throw outOfRangeValue(comp);
    }

    protected WrongValueException outOfRangeValue(Component comp) {
        final String errmsg = getErrorMessage(comp);
        if (errmsg != null)
            return new WrongValueException(comp, errmsg);

        final String s = _min != null ? _max != null ? _min + " ~ " + _max : ">= " + _min : "<= " + _max;
        return new WrongValueException(comp, MZul.OUT_OF_RANGE, s);
    }
}
