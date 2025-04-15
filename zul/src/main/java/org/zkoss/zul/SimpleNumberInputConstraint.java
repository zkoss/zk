/* SimpleNumberInputConstraint.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 13:17:43 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.math.BigDecimal;

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
public class SimpleNumberInputConstraint<T extends Number & Comparable<T>> extends SimpleConstraint {
    private T _min;
    private T _max;
    private Class<T> _typeClass;

    /** Constructs a constraint.
     * @param typeClass the class of type T
     */
    private SimpleNumberInputConstraint(Class<T> typeClass) {
        super(0);
        _typeClass = typeClass;
    }

    /** Constructs a constraint.
     *
     * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
     * {@link #NO_ZERO}, and so on.
     * @param typeClass the class of type T
     */
    private SimpleNumberInputConstraint(int flags, Class<T> typeClass) {
        super(flags);
        _typeClass = typeClass;
    }

    /** Constructs a constraint with a list of constraints separated by comma.
     *
     * @param constraint a list of constraints separated by comma.
     * Example: no positive, no zero
     * @param typeClass the class of type T
     */
    private SimpleNumberInputConstraint(String constraint, Class<T> typeClass) {
        super(constraint);
        _typeClass = typeClass;
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
     * Parse the string to create a value of type {@link T}.
     * @param s the string to parse
     * @return the parsed value
     */
    @SuppressWarnings("unchecked")
    private T parseValue(String s) {
        try {
            if (_typeClass.equals(BigDecimal.class)) {
                return (T) new BigDecimal(s);
            } else if (_typeClass.equals(Double.class)) {
                return (T) Double.valueOf(s);
            } else if (_typeClass.equals(Integer.class)) {
                return (T) Integer.valueOf(s);
            } else if (_typeClass.equals(Long.class)) {
                return (T) Long.valueOf(s);
            } else {
                throw new UnsupportedOperationException("Unsupported number type: " + _typeClass.getName());
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Unable to parse '" + s + "' as " + _typeClass.getSimpleName() + " type: " + e.getMessage());
        }
    }

    /**
     * Checks if the value is of the correct type for this constraint.
     * @param value the value to check
     * @return true if the value is of the correct type
     */
    private boolean isValidValueType(Object value) {
        return value != null && _typeClass.isInstance(value);
    }

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
        if (isValidValueType(value)) {
            @SuppressWarnings("unchecked")
            T typedValue = (T) value;

            if (_min != null && _min.compareTo(typedValue) > 0)
                throw outOfRangeValue(comp);
            if (_max != null && _max.compareTo(typedValue) < 0)
                throw outOfRangeValue(comp);
        }
    }

    protected WrongValueException outOfRangeValue(Component comp) {
        final String errmsg = getErrorMessage(comp);
        if (errmsg != null)
            return new WrongValueException(comp, errmsg);

        final String s = _min != null ? _max != null ? _min + " ~ " + _max : ">= " + _min : "<= " + _max;
        return new WrongValueException(comp, MZul.OUT_OF_RANGE, s);
    }

    /**
     * Creates a SimpleNumberInputConstraint for Integer values.
     * @param constraint constraint string
     * @return a new SimpleNumberInputConstraint for Integer
     */
    public static SimpleNumberInputConstraint<Integer> ofInteger(String constraint) {
        return new SimpleNumberInputConstraint<>(constraint, Integer.class);
    }

    /**
     * Creates a SimpleNumberInputConstraint for Double values.
     * @param constraint constraint string
     * @return a new SimpleNumberInputConstraint for Double
     */
    public static SimpleNumberInputConstraint<Double> ofDouble(String constraint) {
        return new SimpleNumberInputConstraint<>(constraint, Double.class);
    }

    /**
     * Creates a SimpleNumberInputConstraint for BigDecimal values.
     * @param constraint constraint string
     * @return a new SimpleNumberInputConstraint for BigDecimal
     */
    public static SimpleNumberInputConstraint<BigDecimal> ofBigDecimal(String constraint) {
        return new SimpleNumberInputConstraint<>(constraint, BigDecimal.class);
    }

    /**
     * Creates a SimpleNumberInputConstraint for Long values.
     * @param constraint constraint string
     * @return a new SimpleNumberInputConstraint for Long
     */
    public static SimpleNumberInputConstraint<Long> ofLong(String constraint) {
        return new SimpleNumberInputConstraint<>(constraint, Long.class);
    }
} 