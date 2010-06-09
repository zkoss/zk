/* InputElement.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.impl.api;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.ext.Constrainted;

/**
 * A skeletal implementation of an input box.
 * <p>
 * Events: onChange, onChanging, onFocus, onBlur, and onSelection.<br/>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface InputElement extends XulElement, Constrainted {
	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisabled();

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is readonly.
	 * <p>
	 * Default: false.
	 */
	public boolean isReadonly();

	/**
	 * Sets whether it is readonly.
	 */
	public void setReadonly(boolean readonly);

	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 */
	public String getName();

	/**
	 * Sets the name of this component.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * 
	 * @param name
	 *            the name of this component.
	 */
	public void setName(String name);

	/**
	 * Returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 * 
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 * 
	 * <p>
	 * If the error message is set, we say this input is in the error mode. Any
	 * following invocation to {@link #getText} or getValue will throw any
	 * exception. Example, {@link org.zkoss.zul.Textbox#getValue} and
	 * {@link org.zkoss.zul.Intbox#getValue}.
	 */
	public String getErrorMessage();

	/**
	 * Clears the error message.
	 * 
	 * <p>
	 * The error message is cleared automatically, so you rarely need to call
	 * this method. However, if a constraint depends on multiple input fields
	 * and the error can be corrected by changing one of these fields, then you
	 * may have to clear the error message manullay by invoking this method.
	 * 
	 * <p>
	 * For example, assume you have two {@link org.zkoss.zul.Intbox} and want
	 * the value of the first one to be smaller than that of the second one.
	 * Then, you have to call this method for the second intbox once the
	 * validation of the first intbox succeeds, and vice versa. Otherwise, the
	 * error message for the seoncd intbox remains if the user fixed the error
	 * by lowering down the value of the first one Why? The second intbox got no
	 * idea to clear the error message (since its content doesn't change).
	 * 
	 * @param revalidateRequired
	 *            whether to re-validate the current value when {@link #getText}
	 *            or others (such as {@link org.zkoss.zul.Intbox#getValue}) is
	 *            called. If false, the current value is assumed to be correct
	 *            and the following invocation to {@link #getText} or others
	 *            (such as {@link org.zkoss.zul.Intbox#getValue}) won't check
	 *            the value again. Note: when an input element is constrcuted,
	 *            the initial value is assumed to be "not-validated-yet".
	 */
	public void clearErrorMessage(boolean revalidateRequired);

	/**
	 * Clears the error message. It is the same as clearErrorMessage(false).
	 * That is, the current value is assumed to be correct. {@link #getText} or
	 * others (such as {@link org.zkoss.zul.Intbox#getValue}) won't re-validate
	 * it again.
	 * 
	 * <p>
	 * The error message is cleared automatically, so you rarely need to call
	 * this method.
	 * 
	 * @see #clearErrorMessage(boolean)
	 */
	public void clearErrorMessage();

	/**
	 * Returns the value in the String format. In most case, you shall use the
	 * setValue method instead, e.g., {@link org.zkoss.zul.Textbox#getValue} and
	 * {@link org.zkoss.zul.Intbox#getValue}.
	 * 
	 * <p>
	 * It invokes {@link org.zkoss.zul.impl.InputElement#checkUserError} to
	 * ensure no user error.
	 * 
	 * <p>
	 * It invokes {@link org.zkoss.zul.impl.InputElement#coerceToString} to
	 * convert the stored value into a string.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public String getText() throws WrongValueException;

	/**
	 * Sets the value in the String format. In most case, you shall use the
	 * setValue method instead, e.g., {@link org.zkoss.zul.Textbox#setValue} and
	 * {@link org.zkoss.zul.Intbox#setValue}.
	 * 
	 * <p>
	 * It invokes {@link org.zkoss.zul.impl.InputElement#coerceFromString} fisrt
	 * and then {@link org.zkoss.zul.impl.InputElement#validate}. Derives might
	 * override them for type conversion and special validation.
	 * 
	 * @param value
	 *            the value; If null, it is considered as empty.
	 */
	public void setText(String value) throws WrongValueException;

	/**
	 * Returns the maxlength.
	 * <p>
	 * Default: 0 (non-postive means unlimited).
	 */
	public int getMaxlength();

	/**
	 * Sets the maxlength.
	 */
	public void setMaxlength(int maxlength);

	/**
	 * Returns the cols.
	 * <p>
	 * Default: 0 (non-positive means the same as browser's default).
	 */
	public int getCols();

	/**
	 * Sets the cols.
	 */
	public void setCols(int cols) throws WrongValueException;

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex();

	/**
	 * Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException;

	/**
	 * Returns whether it is multiline.
	 * <p>
	 * Default: false.
	 */
	public boolean isMultiline();

	/**
	 * Returns the type.
	 * <p>
	 * Default: text.
	 */
	public String getType();

	/**
	 * Selects the whole text in this input.
	 */
	public void select();

	// -- Constrainted --//
	public void setConstraint(String constr);

	/**
	 * Returns the raw value directly with checking whether any error message
	 * not yet fixed. In other words, it does NOT invoke
	 * {@link org.zkoss.zul.impl.InputElement#checkUserError} .
	 * 
	 * <p>
	 * Note: if the user entered an incorrect value (i.e., caused
	 * {@link WrongValueException}), the incorrect value doesn't be stored so
	 * this method returned the last correct value.
	 * 
	 * @see #getRawText
	 * @see #getText
	 * @see #setRawValue
	 */
	public Object getRawValue();

	/**
	 * Returns the text directly without checking whether any error message not
	 * yet fixed. In other words, it does NOT invoke
	 * {@link org.zkoss.zul.impl.InputElement#checkUserError}.
	 * 
	 * <p>
	 * Note: if the user entered an incorrect value (i.e., caused
	 * {@link WrongValueException}), the incorrect value doesn't be stored so
	 * this method returned the last correct value.
	 * 
	 * @see #getRawValue
	 * @see #getText
	 */
	public String getRawText();

	/**
	 * Sets the raw value directly. The caller must make sure the value is
	 * correct (or intend to be incorrect), because this method doesn't do any
	 * validation.
	 * 
	 * <p>
	 * If you feel confusing with setValue, such as
	 * {@link org.zkoss.zul.Textbox#setValue}, it is usually better to use
	 * setValue instead. This method is reserved for developer that really want
	 * to set an 'illegal' value (such as an empty string to a textbox with
	 * no-empty contraint).
	 * 
	 * <p>
	 * Note: since 3.0.1, the value will be re-validate again if
	 * {@link #getText} or others (such as {@link org.zkoss.zul.Intbox#getValue}
	 * ) is called. In other words, it is assumed that the specified value is
	 * not validated yet -- the same state when this component is created. If
	 * you want to avoid the re-valiation, you have to invoke
	 * {@link #clearErrorMessage()}.
	 * 
	 * <p>
	 * Like setValue, the result is returned back to the server by calling
	 * {@link #getText}.
	 * 
	 * @see #getRawValue
	 */
	public void setRawValue(Object value);

	/**
	 * Returns the current content of this input is correct. If the content is
	 * not correct, next call to the getvalue method will throws
	 * WrongValueException.
	 */
	public boolean isValid();

	/**
	 * Sets the text of this InputElement to the specified text which is
	 * begining with the new start point and ending with the new end point.
	 * 
	 * @param start
	 *            the start position of the text (included)
	 * @param end
	 *            the end position of the text (excluded)
	 * @param newtxt
	 *            the new text to be set.
	 * @param isHighLight
	 *            Sets whether it will represent highlihgt style or cursor
	 *            style.If the start point same with the end point always
	 *            represent cursor style.
	 */
	public void setSelectedText(int start, int end, String newtxt,
			boolean isHighLight);

	/**
	 * Sets the selection end to the specified position and the selection start
	 * to the specified position. The new end point is constrained to be at or
	 * after the current selection start. If the new start point is different
	 * with the new end point, then will represent the result of highlight in
	 * this text.
	 * 
	 * <p>
	 * Set both arguments to the same value to move the cursor to the
	 * corresponding position without selecting text.
	 * 
	 * @param start
	 *            the start position of the text (included)
	 * @param end
	 *            the end position of the text (excluded)
	 */
	public void setSelectionRange(int start, int end);
}
