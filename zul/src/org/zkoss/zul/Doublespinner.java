/* Doublespinner.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 17, 2010 9:51:04 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding a constrained double.
 * 
 * <p>
 * Default {@link #getZclass}: z-doublespinner.
 * 
 * <p>
 * doublespinner supports below key events.
 * <lu>
 * <li>0-9 : set the value on the inner text box.
 * <li>delete : clear the value to empty (null)
 * </lu>
 * 
 * @author jumperchen
 * @since 5.0.6
 */
public class Doublespinner extends NumberInputElement implements
		org.zkoss.zul.api.Doublespinner {
	private double _step = 1.0;
	private boolean _btnVisible = true;
	
	public Doublespinner() {
		setCols(11);
	}

	public Doublespinner(double value) throws WrongValueException {
		this();
		setValue(new Double(value));
	}
	
	/** Returns the value (in Double), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Double getValue() throws WrongValueException {
		return (Double)getTargetValue();
	}

	/** Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double)val).doubleValue(): 0;
	}

	/** Sets the value (in Double).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Double value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	/**
	 * Return the step of double spinner
	 */
	public double getStep() {
		return _step;
	}

	/**
	 * Set the step of double spinner
	 */
	public void setStep(double step) {
		if (_step != step) {
			_step = step;
			smartUpdate("step", _step);
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
			smartUpdate("buttonVisible", visible);
		}
	}
	
	// super
	public String getZclass() {
		return _zclass == null ?  "z-doublespinner" : _zclass;
	}
	
	/**
	 * @param constr a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	// -- super --//
	public void setConstraint(String constr) {
		setConstraint(constr != null ? new SimpleDoubleSpinnerConstraint(constr): null);
	}
	
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String) vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			double v = Double.parseDouble(val);
			double divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
			while (v != 0 && --divscale >= 0)
				v /= 10;
			return new Double(v);
		} catch (NumberFormatException ex) {
			throw showCustomError(new WrongValueException(this,
					MZul.NUMBER_REQUIRED, value));
		}
	}

	protected String coerceToString(Object value) {
		return value != null && getFormat() == null ?
			value instanceof Double ?
				Doublebox.toLocaleString((Double)value, getDefaultLocale()):
			value.toString()/*just in case*/: formatNumber(value, null);
	}
	
	protected Object unmarshall(Object value) {
		if(value instanceof Integer){
			return new Double(((Integer)value).doubleValue());	
		}
		return value;
	}
	
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);
		if(_step != 1.0)
			renderer.render("step", _step);
		if(_btnVisible != true)
			renderer.render("buttonVisible", _btnVisible);
	}

}
