/* Spinner.java

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

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding a constrained integer.
 *
 * <p>Default {@link #getZclass}: z-spinner.
 *
 * <p>spinner supports below key events.
 * <lu>
 *  <li>0-9 : set the value on the inner text box.
 * 	<li>delete : clear the value to empty (null)
 * </lu>
 * @author gracelin
 * @since 3.5.0
 */
public class Spinner extends NumberInputElement implements org.zkoss.zul.api.Spinner {
	private int _step = 1;
	private boolean _btnVisible = true;

	public Spinner() {
		setCols(11);
	}

	public Spinner(int value) throws WrongValueException {
		this();
		setValue(new Integer(value));
	}

	public Spinner(SpinnerNumberModel model) {
		setCols(11);
	}
	
	/** Returns the value (in Integer), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Integer getValue() throws WrongValueException {
		return (Integer)getTargetValue();
	}
	/** Returns the value in int. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Integer)val).intValue(): 0;
	}
	/** Sets the value (in Integer).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Integer value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}
	
	/**
	 * Return the step of spinner
	 */
	public int getStep(){
		return _step;
	}
	
	/**
	 * Set the step of spinner
	 */
	public void setStep(int step) {
		if (_step != step) {
			_step = step;
			smartUpdate("z.step", _step);
		}
	}
	
	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}
	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("z.btnVisi", visible);
		}
	}

	// super
	public String getZclass() {
		return _zclass == null ?  "z-spinner" : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		if (getConstraint() instanceof SimpleSpinnerConstraint) {
			final SimpleSpinnerConstraint st = (SimpleSpinnerConstraint) getConstraint();
			Integer min = st.getMin();
			Integer max = st.getMax();
			if (min != null)
				HTMLs.appendAttribute(sb, "z.min", min.toString());
			if (max != null)
				HTMLs.appendAttribute(sb, "z.max", max.toString());
		}
		HTMLs.appendAttribute(sb, "z.step", _step);
		HTMLs.appendAttribute(sb, "z.onchange", "true");
		return sb.toString();
	}
	
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final String style = getInnerStyle();
		return style.length() > 0 ? attrs+" style=\""+style+'"': attrs;
	}
	
	private String getInnerStyle() {
		final StringBuffer sb = new StringBuffer(32)
			.append(HTMLs.getTextRelevantStyle(getRealStyle()));
		HTMLs.appendStyle(sb, "width", getWidth());
		HTMLs.appendStyle(sb, "height", getHeight());
		return sb.toString();
	}
	
	// -- super --//
	public void setConstraint(String constr) {
		setConstraint(new SimpleSpinnerConstraint(constr));
	}
	
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String) vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			int v = Integer.parseInt(val);
			int divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
			while (v != 0 && --divscale >= 0)
				v /= 10;
			return new Integer(v);
		} catch (NumberFormatException ex) {
			throw showCustomError(new WrongValueException(this,
					MZul.NUMBER_REQUIRED, value));
		}
	}

	protected String coerceToString(Object value) {
		return value != null && getFormat() == null ? value.toString()
				: formatNumber(value, null);
	}
}
