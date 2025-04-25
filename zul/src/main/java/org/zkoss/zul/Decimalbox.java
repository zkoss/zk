/* Decimalbox.java

	Purpose:

	Description:

	History:
		Tue Jun 28 13:40:20     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.math.BigDecimals;
import org.zkoss.zk.ui.ArithmeticWrongValueException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ObjectPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding BigDecimal.
 * <p>Default {@link #getZclass}: z-decimalbox.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Decimalbox extends NumberInputElement {

	private static final Logger log = LoggerFactory.getLogger(Decimalbox.class);
	
	/** Used with {@link #setScale} to denote that the scale is decided by
	 * what user has entered.
	 */
	public static final int AUTO = -1000000000;
	private int _scale = AUTO;

	public Decimalbox() {
		setCols(11);
	}

	public Decimalbox(BigDecimal value) throws WrongValueException {
		this();
		setValue(value);
	}

	/** Returns the value (in BigDecimal), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public BigDecimal getValue() throws WrongValueException {
		return (BigDecimal) getTargetValue();
	}

	/** Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.doubleValue() : 0.0;
	}

	/** Returns the value in integer. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.intValue() : 0;
	}

	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.longValue() : 0;
	}

	/** Returns the value in short. If null, zero is returned.
	 */
	public short shortValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.shortValue() : 0;
	}

	/** Sets the value (in BigDecimal).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(BigDecimal value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	public void setValue(String str) {
		this.setValue(str == null ? null : new BigDecimal(str));
	}

	/** Returns the scale for the decimal number storing in this component,
	 * or {@link #AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>Default: {@link #AUTO}.
	 */
	public int getScale() {
		return _scale;
	}

	/** Sets the scale for the decimal number storing in this component,
	 * or {@link #AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>For example, set the scale of 1234.1234 to 2, the result will be 1234.12
	 * <p>Default: {@link #AUTO}.
	 */
	public void setScale(int scale) {
		//bug #3089502: setScale in decimalbox not working
		if (_scale != scale) {
			_scale = scale;
			smartUpdate("scale", scale);
			if (scale != AUTO) {
				BigDecimal v = (BigDecimal) _value;
				if (v != null) {
					setValue(v);
				}
			}
		}
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-decimalbox" : _zclass;
	}

	protected Object marshall(Object value) {
		return value != null ? ((BigDecimal) value).toPlainString() : value;
	}

	protected Object unmarshall(Object value) {
		return value != null ? new BigDecimal((String) value) : value;
	}

	public void setRawValue(Object value) {
		//bug #3089502: setScale in decimalbox not working
		if (_scale != AUTO && value != null) {
			value = ((BigDecimal) value).setScale(_scale, getRoundingMode());
		}
		super.setRawValue(value);
	}

	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @since 10.2.0   
	 */
	// -- super --//
	public void setConstraint(String constr) {
		String clsnm = Library.getProperty("org.zkoss.zul.Decimalbox.constraint.class");
		if (clsnm != null) {
			try {
				setConstraint((SimpleConstraint) Classes.newInstanceByThread(clsnm, new Class<?>[] {String.class}, new Object[] {constr}));
				return;
			} catch (Throwable ex) {
				log.error("Unable to instantiate " + clsnm, ex);
			}
		}
		super.setConstraint(constr);
	}

	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String) vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			BigDecimal v = new BigDecimal(val);
			if (_scale != AUTO)
				v = v.setScale(_scale, getRoundingMode());

			int divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
			if (divscale > 0) {
				final BigDecimal ten = new BigDecimal(10);
				do {
					v = v.divide(ten, _scale == AUTO ? v.scale() + 1 : _scale, getRoundingMode());
				} while (--divscale > 0);
			}
			return v;
		} catch (NumberFormatException ex) {
			throw showCustomError(new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
		}
	}

	protected String coerceToString(Object value) {
		try {
			return value != null && getFormat() == null ? value instanceof BigDecimal
					? BigDecimals.toLocaleString((BigDecimal) value, getDefaultLocale()) : value.toString()
					/*just in case*/ : formatNumber(value, null);
		} catch (ArithmeticException ex) {
			throw new ArithmeticWrongValueException(this, ex.getMessage(), ex, value);
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (_scale != AUTO)
			renderer.render("scale", _scale);
	}

	//--ComponentCtrl--//
	private static Map<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);

	static {
		_properties.put("value", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object value) {
				if (value instanceof BigDecimal)
					((Decimalbox) cmp).setValue((BigDecimal) value);
				else if (value instanceof String || value == null) // ZK-3698: Handle null
					((Decimalbox) cmp).setValue((String) value);
			}

			public BigDecimal getValue(Component cmp) {
				return ((Decimalbox) cmp).getValue();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}
}
