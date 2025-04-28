/* Doublebox.java

	Purpose:
		
	Description:
		
	History:
		Sat Oct 14 12:59:39     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.ArithmeticWrongValueException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding an float point value (double).
 * <p>Default {@link #getZclass}: z-doublebox.(since 3.5.0)
 *
 * @author henrichen
 */
public class Doublebox extends NumberInputElement {

	private static final Logger log = LoggerFactory.getLogger(Doublebox.class);

	public Doublebox() {
		setCols(11);
	}

	public Doublebox(double value) throws WrongValueException {
		this();
		setValue(value);
	}

	public Doublebox(Double value) throws WrongValueException {
		this();
		setValue(value);
	}

	/** Returns the value (in Double), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Double getValue() throws WrongValueException {
		return (Double) getTargetValue();
	}

	/** Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double) val).doubleValue() : 0.0;
	}

	/** Returns the value in integer. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double) val).intValue() : 0;
	}

	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double) val).longValue() : 0;
	}

	/** Returns the value in short. If null, zero is returned.
	 */
	public short shortValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double) val).shortValue() : 0;
	}

	/** Sets the value (in Double).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Double value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	/** Sets the value (in double)
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(double value) throws WrongValueException {
		setValue(new Double(value));
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-doublebox" : _zclass;
	}

	public Object unmarshall(Object value) {
		return value instanceof Number //sometimes JSON might interpret value to Integer
				? new Double(((Number) value).doubleValue()) : value;
	}

	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @since 10.2.0
	 */
	// -- super --//
	public void setConstraint(String constr) {
		String clsnm = Library.getProperty("org.zkoss.zul.Doublebox.constraint.class");
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
			double v = Double.parseDouble(val);
			int divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
			if (divscale > 0)
				v /= Math.pow(10, divscale);
			return new Double(v);
		} catch (NumberFormatException ex) {
			throw showCustomError(new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
		}
	}

	protected String coerceToString(Object value) {
		try {
			return value != null && getFormat() == null
					? value instanceof Double ? toLocaleString((Double) value, getDefaultLocale()) : value.toString()
					/*just in case*/ : formatNumber(value, null);
		} catch (ArithmeticException ex) {
			throw new ArithmeticWrongValueException(this, ex.getMessage(), ex, value);
		}
	}

	/*package*/ static String toLocaleString(Double v, java.util.Locale locale) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		final char DECIMAL = symbols.getDecimalSeparator();
		final char MINUS = symbols.getMinusSign();
		String valStr = v.toString();
		String patternStr = "\\.0+$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(v.toString());
		int zIndex = valStr.length();
		if (matcher.find())
			zIndex = matcher.start();
		// only replace MINUS and DECIMAL as toPlainString() implementation
		// only involves these two chars.
		// B65-ZK-1909: Remove .0 part
		return valStr.substring(0, zIndex).replace('.', DECIMAL).replace('-', MINUS);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);

	static {
		_properties.put("value", new PropertyAccess<Double>() {
			public void setValue(Component cmp, Double value) {
				((Doublebox) cmp).setValue(value);
			}

			public Class<Double> getType() {
				return Double.class;
			}

			public Double getValue(Component cmp) {
				return ((Doublebox) cmp).getValue();
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
