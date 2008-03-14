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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * An edit box for holding a constrained integer.
 *
 * <p>Default {@link #getSclass}: spinner.
 *
 * <p>spinner supports below key events.
 * <lu>
 *  <li>0-9 : set the value on the inner text box.
 * 	<li>delete : clear the value to empty (null)
 * </lu>
 * @author gracelin
 * @since 3.1.0
 */
public class Spinner extends NumberInputElement {
	private static final String DEFAULT_IMAGE = "~./zul/img/updnbtn.gif";
	private String _img;
	private boolean _btnVisible = true;
	private SpinnerNumberModel _model;

	public Spinner() {
		setCols(11);
		_model = new SpinnerNumberModel();
		setConstraint(new SimpleSpinnerConstraint(_model));
	}

	public Spinner(int value) throws WrongValueException {
		this();
		setValue(new Integer(value));
	}

	public Spinner(SpinnerNumberModel model) {
		setCols(11);
		_model = model;
		setConstraint(new SimpleSpinnerConstraint(_model));
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
		if (_model == null)
			_model = new SpinnerNumberModel();
		
		validate(value);
		_model.setValue(value);
		setRawValue(_model.getValue());
	}
	
	/**
	 * Return the SpinnerNumberModel, or null if no model.
	 */
	public SpinnerNumberModel getModel() {
		return _model;
	}
	/**
	 * Set the SpinnerNumberModel
	 */
	public void setModel(SpinnerNumberModel model) {
		_model = model;
	}

	/**
	 * Return the Maximum of the value
	 */
	public Integer getMax() {
		return (Integer) _model.getMaximum();
	}

	/**
	 * Set the Maximum of the value
	 */
	public void setMax(Integer max) {
		_model.setMaximum(max);
		smartUpdate("z.max", _model.getMaximum().toString());
	}

	/**
	 * Return the minimum of the value
	 */
	public Integer getMin() {
		return (Integer) _model.getMinimum();
	}

	/**
	 * Set the minimum of the value
	 */
	public void setMin(Integer min) {
		_model.setMinimum(min);
		smartUpdate("z.min", _model.getMinimum().toString());
	}
	
	/**
	 * Return the step of spinner
	 */
	public Integer getStep(){
		return (Integer)_model.getStepSize();
	}
	
	/**
	 * Set the step of spinner
	 */
	public void setStep(Integer step) {
		_model.setStepSize(step);
		smartUpdate("z.step", _model.getStepSize().toString());
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
	/** Returns the URI of the button image.
	 */
	public String getImage() {
		return _img != null ? _img: DEFAULT_IMAGE;
	}
	/** Sets the URI of the button image.
	 *
	 * @param img the URI of the button image. If null or empty, the default
	 * URI is used.
	 */
	public void setImage(String img) {
		if (img != null && (img.length() == 0 || DEFAULT_IMAGE.equals(img)))
			img = null;
		if (!Objects.equals(_img, img)) {
			_img = img;
			invalidate();
		}
	}
	
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.onchange", Boolean.TRUE);
		return sb.toString();
	}
	
	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "z.step",  _model.getStepSize().toString());
		HTMLs.appendAttribute(sb, "z.min",  _model.getMinimum().toString());
		HTMLs.appendAttribute(sb, "z.max",  _model.getMaximum().toString());

		final String attrs = sb.toString();
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
		setConstraint(new SimpleSpinnerConstraint(constr, _model));
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
