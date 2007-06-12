/* TextField.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 23, 2007 7:05:38 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil.impl;

import org.zkoss.lang.Objects;
import org.zkoss.mil.Item;
//import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Inputable;

/**
 * TextField related component.
 * @author henrichen
 */
abstract public class InputElement extends Item {
	protected static final int ANY = 0;
	protected static final int EMAILADDR = 1;
	protected static final int NUMERIC = 2;
	protected static final int PHONENUMBER = 3;
	protected static final int URL = 4;
	protected static final int DECIMAL = 5;
	
	protected static final int PASSWORD = 0x10000;
	protected static final int UNEDITABLE = 0x20000;
	protected static final int SENSITIVE = 0x40000;
	protected static final int NON_PREDICTIVE = 0x80000;
	protected static final int INITIAL_CAPS_WORD = 0x100000;
	protected static final int INITIAL_CAPS_SENTENCE = 0x200000;

	protected static final int CONSTRAINT_MASK = 0xffff;
	
//	private static final Log log = Log.lookup(InputElement.class);

	/** The value. */
	private Object _value;
	/** Used by setTextByClient() to disable sending back the value */
	private String _txtByClient;
	private int _maxlength = 32;
	private boolean _readonly;

	/** Returns whether it is readonly.
	 * <p>Default: false.
	 */
	public boolean isReadonly() {
		return _readonly;
	}
	/** Sets whether it is readonly.
	 */
	public void setReadonly(boolean readonly) {
		if (_readonly != readonly) {
			_readonly = readonly;
			smartUpdateConstraints();
		}
	}

	/** Returns the value in the String format.
	 * In most case, you shall use the setValue method instead, e.g.,
	 * {@link org.zkoss.zul.Textbox#getValue} and
	 * {@link org.zkoss.zul.Intbox#getValue}.
	 *
	 * <p>It invokes {@link #checkUserError} to ensure no user error.
	 *
	 * <p>It invokes {@link #coerceToString} to convert the stored value
	 * into a string.
	 *
	 * @exception WrongValueException if user entered a wrong value
	 */
	public String getText() throws WrongValueException {
		return coerceToString(_value);
	}

	/** Sets the value in the String format.
	 * In most case, you shall use the setValue method instead, e.g.,
	 * {@link org.zkoss.zul.Textbox#setValue} and
	 * {@link org.zkoss.zul.Intbox#setValue}.
	 *
	 * <p>It invokes {@link #coerceFromString} fisrt and then {@link #validate}.
	 * Derives might override them for type conversion and special
	 * validation.
	 *
	 * @param value the value; If null, it is considered as empty.
	 */
	public void setText(String value) throws WrongValueException {
		final Object val = coerceFromString(value);

		if (!Objects.equals(_value, val)) {
			_value = val;

			final String fmtval = coerceToString(_value);
			if (_txtByClient == null || !Objects.equals(_txtByClient, fmtval)) {
				_txtByClient = null; //only once
				smartUpdate("tx", fmtval); //text
			}
				//being sent back to the server.
		} else if (_txtByClient != null) {
			//value equals but formatted result might differ because
			//our parse is more fault tolerant
			final String fmtval = coerceToString(_value);
			if (!Objects.equals(_txtByClient, fmtval)) {
				_txtByClient = null; //only once
				smartUpdate("tx", fmtval); //text
			}
		}
	}

	/** Internal type of this InputElement (ANY, EMAILADDR, NUMERIC, PHONENUMBER, 
	 * URL, or DECIMAL). */
	abstract protected int getInternalType();
	
	/** Coerces the value passed to {@link #setText}.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString} and {@link #coerceFromString}
	 * to convert between a string and your targeting type.
	 *
	 * <p>Moreover, when {@link org.zkoss.zul.Textbox} is called, it calls this method
	 * with value = null. Derives shall handle this case properly.
	 */
	abstract protected
	Object coerceFromString(String value) throws WrongValueException;
	/** Coerces the value passed to {@link #setText}.
	 *
	 * <p>Default: convert null to an empty string.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString} and {@link #coerceFromString}
	 * to convert between a string and your targeting type.
	 */
	abstract protected String coerceToString(Object value);

	/** Returns the maxlength.
	 * <p>Default: 32.
	 */
	public int getMaxlength() {
		return _maxlength;
	}

	/** Sets the maxlength.
	 */
	public void setMaxlength(int maxlength) {
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			
			smartUpdate("xs", maxlength); //maxSize
		}
	}

	/** Returns the type.
	 * <p>Default: text.
	 */
	public String getType() {
		return "text";
	}

	//-- super --//
	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super.getInnerAttrs());

		HTMLs.appendAttribute(sb, "tx",  coerceToString(_value)); //text, string
		HTMLs.appendAttribute(sb, "xs",  _maxlength); //maxSize
		HTMLs.appendAttribute(sb, "cs", getConstraints()); //constraints 

		return sb.toString();
	}
	
	protected int getConstraints() {
		int constraints = getInternalType();
		if ("password".equals(getType())) {
			constraints |= PASSWORD;
		}
		if (isReadonly()) {
			constraints |= UNEDITABLE;
		}
		return constraints;
	}
	
	protected void smartUpdateConstraints() {
		smartUpdate("cs", getConstraints());
	}

	public String getOuterAttrs() {
		final StringBuffer sb =	new StringBuffer(64).append(super.getOuterAttrs());
		
		appendAsapAttr(sb, "onChange");
		appendAsapAttr(sb, "onChanging");

		return sb.toString();
	}

	/** Returns the value in the targeting type.
	 * It is used by the deriving class to implement the getValue method.
	 * For example, {@link org.zkoss.zul.Intbox#getValue} is the same
	 * as this method except with a different signature.
	 *
	 * <p>It invokes {@link #checkUserError} to ensure no user error.
	 * @exception WrongValueException if the user entered a wrong value
	 * @see #getText
	 */
	protected Object getTargetValue() throws WrongValueException {
		return _value;
	}

	/** Returns the raw value directly with checking whether any
	 * error message not yet fixed. In other words, it does NOT invoke
	 * {@link #checkUserError}.
	 *
	 * <p>Note: if the user entered an incorrect value (i.e., caused
	 * {@link WrongValueException}), the incorrect value doesn't
	 * be stored so this method returned the last correct value.
	 *
	 * @see #getRawText
	 * @see #getText
	 * @see #setRawValue
	 */
	public Object getRawValue() {
		return _value;
	}
	
	/** Returns the text directly without checking whether any error
	 * message not yet fixed. In other words, it does NOT invoke
	 * {@link #checkUserError}.
	 *
	 * <p>Note: if the user entered an incorrect value (i.e., caused
	 * {@link WrongValueException}), the incorrect value doesn't
	 * be stored so this method returned the last correct value.
	 *
	 * @see #getRawValue
	 * @see #getText
	 */
	public String getRawText() {
		return coerceToString(_value);
	}
	
	/** Sets the raw value directly. The caller must make sure the value
	 * is correct (or intend to be incorrect), because this method
	 * doesn't do any validation.
	 *
	 * <p>If you feel confusing with setValue, such as {@link org.zkoss.zul.Textbox#setValue},
	 * it is usually better to use setValue instead. This method
	 * is reserved for developer that really want to set an 'illegal'
	 * value (such as an empty string to a textbox with no-empty contraint).
	 *
	 * <p>Like setValue, the result is returned back to the server
	 * by calling {@link #getText}.
	 *
	 * @see #getRawValue
	 */
	public void setRawValue(Object value) {
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", coerceToString(_value));
		}
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl implements Inputable {
		//-- Inputable --//
		public void setTextByClient(String value) throws WrongValueException {
			_txtByClient = value;
			try {
				setText(value);
			} catch (WrongValueException ex) {
				throw ex;
			} finally {
				_txtByClient = null;
			}
		}
	}
}
