/* Longbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:39:37     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-longbox.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Longbox extends NumberInputElement {

	private static final Logger log = LoggerFactory.getLogger(Longbox.class);

	public Longbox() {
		setCols(11);
	}

	public Longbox(long value) throws WrongValueException {
		this();
		setValue(new Long(value));
	}

	public Longbox(int value) throws WrongValueException {
		this();
		setValue(new Long(value));
	}

	/** Returns the value (in Long), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Long getValue() throws WrongValueException {
		return (Long) getTargetValue();
	}

	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Long) val).longValue() : 0;
	}

	/** Returns the value in int. If null, zero is returned.
	 */
	public long intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Long) val).intValue() : 0;
	}

	/** Sets the value (in Long).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Long value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-longbox" : _zclass;
	}

	protected Object marshall(Object value) {
		return value != null ? ((Long) value).toString() : value;
	}

	protected Object unmarshall(Object value) {
		return value != null ? new Long((String) value) : value;
	}

	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @since 10.2.0
	 */
	// -- super --//
	public void setConstraint(String constr) {
		String clsnm = Library.getProperty("org.zkoss.zul.Longbox.constraint.class");
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
			long v = Long.parseLong(val);
			int divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
			while (v != 0 && --divscale >= 0)
				v /= 10;
			return new Long(v);
		} catch (NumberFormatException ex) {
			throw showCustomError(new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
		}
	}

	protected String coerceToString(Object value) {
		return value != null && getFormat() == null ? value.toString() : formatNumber(value, null);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);

	static {
		_properties.put("value", new PropertyAccess<Long>() {
			public void setValue(Component cmp, Long value) {
				((Longbox) cmp).setValue(value);
			}

			public Class<Long> getType() {
				return Long.class;
			}

			public Long getValue(Component cmp) {
				return ((Longbox) cmp).getValue();
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
