/* BranchInput.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 9, 2007 12:42:46 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Some code of this file is refer to Common Development and Distribution License
 the license file is https://javaserverfaces.dev.java.net/CDDL.html

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;

/**
 * 
 * The skeletal class used to implement the ZULJSF components
 *  which needs to support {@link javax.faces.component.EditableValueHolder}.<br/>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 * 
 */
abstract public class BranchInput extends BranchOutput implements
		EditableValueHolder, ClientInputSupport{
	private static final Log log = Log.lookup(RootComponent.class);

//	------------code refer to UIInput in JSF RI-----------------
	/**
	 * <p>
	 * The submittedValue value of this {@link javax.faces.component.UIInput} component.
	 * </p>
	 */
	private Object _submittedValue = null;

	/**
	 * <p>
	 * Return the submittedValue value of this {@link javax.faces.component.UIInput} component. This
	 * method should only be used by the <code>decode()</code> and
	 * <code>validate()</code> method of this component, or its corresponding
	 * {@link javax.faces.render.Renderer}.
	 * </p>
	 */
	public Object getSubmittedValue() {

		return (this._submittedValue);

	}

	/**
	 * <p>
	 * Set the submittedValue value of this {@link javax.faces.component.UIInput} component. This
	 * method should only be used by the <code>decode()</code> and
	 * <code>validate()</code> method of this component, or its corresponding
	 * {@link javax.faces.render.Renderer}.
	 * </p>
	 * 
	 * @param submittedValue
	 *            The new submitted value
	 */
	public void setSubmittedValue(Object submittedValue) {

		this._submittedValue = submittedValue;

	}

	/**
	 * <p>
	 * The "required field" state for this component.
	 * </p>
	 */
	private boolean _required = false;

	private boolean _requiredSet = false;

	/**
	 * <p>
	 * Return the "required field" state for this component.
	 * </p>
	 */
	public boolean isRequired() {

		if (this._requiredSet) {
			return (this._required);
		}
		ValueBinding vb = getValueBinding("required");
		if (vb != null) {
			return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
		} else {
			return (this._required);
		}

	}

	private boolean _valid = true;

	public boolean isValid() {

		return (this._valid);

	}

	public void setValid(boolean valid) {

		this._valid = valid;

	}

	/**
	 * <p>
	 * Set the "required field" state for this component.
	 * </p>
	 * 
	 * @param required
	 *            The new "required field" state
	 */
	public void setRequired(boolean required) {

		this._required = required;
		this._requiredSet = true;

	}

	/**
	 * <p>
	 * The immediate flag.
	 * </p>
	 */
	private boolean _immediate = false;

	private boolean _immediateSet = false;

	public boolean isImmediate() {

		if (this._immediateSet) {
			return (this._immediate);
		}
		ValueBinding vb = getValueBinding("immediate");
		if (vb != null) {
			return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
		} else {
			return (this._immediate);
		}

	}

	public void setImmediate(boolean immediate) {

		// if the immediate value is changing.
		if (immediate != this._immediate) {
			this._immediate = immediate;
		}
		this._immediateSet = true;

	}

	private MethodBinding _validatorBinding = null;

	/**
	 * <p>
	 * Return a <code>MethodBinding</code> pointing at a method that will be
	 * called during <em>Process Validations</em> phase of the request
	 * processing lifecycle, to validate the current value of this component.
	 * </p>
	 */
	public MethodBinding getValidator() {

		return (this._validatorBinding);

	}

	/**
	 * <p>
	 * Set a <code>MethodBinding</code> pointing at a method that will be
	 * called during <em>Process Validations</em> phase of the request
	 * processing lifecycle, to validate the current value of this component.
	 * </p>
	 * 
	 * <p>
	 * Any method referenced by such an expression must be public, with a return
	 * type of <code>void</code>, and accept parameters of type
	 * {@link FacesContext}, {@link UIComponent}, and <code>Object</code>.
	 * </p>
	 * 
	 * @param validatorBinding
	 *            The new <code>MethodBinding</code> instance
	 */
	public void setValidator(MethodBinding validatorBinding) {

		this._validatorBinding = validatorBinding;

	}

	private MethodBinding _valueChangeMethod = null;

	/**
	 * <p>
	 * Return a <code>MethodBinding </code> instance method that will be called
	 * during <em>Process Validations</em> phase of he request processing
	 * lifecycle, after any registered {@link ValueChangeListener}s have been
	 * notified of a value change.
	 * </p>
	 */
	public MethodBinding getValueChangeListener() {

		return (this._valueChangeMethod);

	}

	/**
	 * <p>
	 * Set a <code>MethodBinding</code> instance a that will be called during
	 * <em>Process Validations</em> phase of he request processing lifecycle,
	 * after any registered {@link ValueChangeListener}s have been notified of
	 * a value change.
	 * </p>
	 * 
	 * @param valueChangeMethod
	 *            The new method binding instance
	 */
	public void setValueChangeListener(MethodBinding valueChangeMethod) {

		this._valueChangeMethod = valueChangeMethod;

	}

	// ----------------------------------------------------- UIComponent Methods

	/**
	 * <p>
	 * Specialized decode behavior on top of that provided by the superclass. In
	 * addition to the standard <code>processDecodes</code> behavior inherited
	 * from {@link javax.faces.component.UIComponentBase}, calls <code>validate()</code> if the the
	 * <code>immediate</code> property is true; if the component is invalid
	 * afterwards or a <code>RuntimeException</code> is thrown, calls
	 * {@link javax.faces.context.FacesContext#renderResponse}.
	 * </p>
	 * 
	 * @exception NullPointerException
	 */
	public void processDecodes(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		// Skip processing if our rendered flag is false
		if (!isRendered()) {
			return;
		}

		super.processDecodes(context);

		if (isImmediate()) {
			executeValidate(context);
		}
	}

	/**
	 * <p>
	 * In addition to the standard <code>processValidators</code> behavior
	 * inherited from {@link javax.faces.component.UIComponentBase}, calls <code>validate()</code>
	 * if the <code>immediate</code> property is false (which is the default);
	 * if the component is invalid afterwards, calls
	 * {@link javax.faces.context.FacesContext#renderResponse}. If a <code>RuntimeException</code>
	 * is thrown during validation processing, calls
	 * {@link javax.faces.context.FacesContext#renderResponse} and re-throw the exception.
	 * </p>
	 * 
	 * @exception NullPointerException
	 */
	public void processValidators(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		// Skip processing if our rendered flag is false
		if (!isRendered()) {
			return;
		}

		super.processValidators(context);
		if (!isImmediate()) {
			executeValidate(context);
		}
	}

	/**
	 * <p>
	 * In addition to the standard <code>processUpdates</code> behavior
	 * inherited from {@link javax.faces.component.UIComponentBase}, calls <code>updateModel()</code>.
	 * If the component is invalid afterwards, calls
	 * {@link javax.faces.context.FacesContext#renderResponse}. If a <code>RuntimeException</code>
	 * is thrown during update processing, calls
	 * {@link javax.faces.context.FacesContext#renderResponse} and re-throw the exception.
	 * </p>
	 * 
	 * @exception NullPointerException
	 */
	public void processUpdates(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		// Skip processing if our rendered flag is false
		if (!isRendered()) {
			return;
		}

		super.processUpdates(context);

		try {
			updateModel(context);
		} catch (RuntimeException e) {
			context.renderResponse();
			throw e;
		}

		if (!isValid()) {
			context.renderResponse();
		}
	}

	/**
	 * @exception NullPointerException
	 */
	public void decode(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		// Force validity back to "true"
		setValid(true);
		super.decode(context);
		
		//add by dennis.
		clientInputDecode(context);
	}

	/**
	 * <p>
	 * In addition to to the default {@link UIComponent#broadcast} processing,
	 * pass the {@link ValueChangeEvent} being broadcast to the method
	 * referenced by <code>valueChangeListener</code> (if any).
	 * </p>
	 * 
	 * @param event
	 *            {@link FacesEvent} to be broadcast
	 * 
	 * @exception AbortProcessingException
	 *                Signal the JavaServer Faces implementation that no further
	 *                processing on the current event should be performed
	 * @exception IllegalArgumentException
	 *                if the implementation class of this {@link FacesEvent} is
	 *                not supported by this component
	 * @exception NullPointerException
	 *                if <code>event</code> is <code>null</code>
	 */
	public void broadcast(FacesEvent event) throws AbortProcessingException {

		// Perform standard superclass processing
		super.broadcast(event);

		if (event instanceof ValueChangeEvent) {
			MethodBinding method = getValueChangeListener();
			if (method != null) {
				FacesContext context = getFacesContext();
				method.invoke(context, new Object[] { event });
			}
		}

	}

	/**
	 * <p>
	 * Perform the following algorithm to update the model data associated with
	 * this {@link javax.faces.component.UIInput}, if any, as appropriate.
	 * </p>
	 * <ul>
	 * <li>If the <code>valid</code> property of this component is
	 * <code>false</code>, take no further action.</li>
	 * <li>If the <code>localValueSet</code> property of this component is
	 * <code>false</code>, take no further action.</li>
	 * <li>If no {@link ValueBinding} for <code>value</code> exists, take no
	 * further action.</li>
	 * <li>Call <code>setValue()</code> method of the {@link ValueBinding} to
	 * update the value that the {@link ValueBinding} points at.</li>
	 * <li>If the <code>setValue()</code> method returns successfully:
	 * <ul>
	 * <li>Clear the local value of this {@link javax.faces.component.UIInput}.</li>
	 * <li>Set the <code>localValueSet</code> property of this
	 * {@link javax.faces.component.UIInput} to false.</li>
	 * </ul>
	 * </li>
	 * <li>If the <code>setValue()</code> method call fails:
	 * <ul>
	 * <li>Enqueue an error message by calling <code>addMessage()</code> on
	 * the specified {@link FacesContext} instance.</li>
	 * <li>Set the <code>valid</code> property of this {@link javax.faces.component.UIInput} to
	 * <code>false</code>.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param context
	 *            {@link javax.faces.context.FacesContext} for the request we are processing
	 * 
	 * @exception NullPointerException
	 *                if <code>context</code> is <code>null</code>
	 */
	public void updateModel(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		if (!isValid() || !isLocalValueSet()) {
			return;
		}

		ValueBinding vb = getValueBinding("value");
		if (vb != null) {
			try {
				vb.setValue(context, getLocalValue());
				setValue(null);
				setLocalValueSet(false);
				return;
			} catch (EvaluationException e) {
				Throwable cause = e.getCause();
				if(cause!=null){
					log.warning(e);
				}
				
				String messageStr = e.getMessage();
				FacesMessage message = null;
				if (null == messageStr) {
					message = MessageFactory.getMessage(context,
							MessageFactory.CONVERSION_MESSAGE_ID);
				} else {
					message = new FacesMessage(messageStr);
				}
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(getClientId(context), message);
				setValid(false);
			} catch (FacesException e) {
				log.warning(e);
				FacesMessage message = MessageFactory.getMessage(context,
						MessageFactory.CONVERSION_MESSAGE_ID);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(getClientId(context), message);
				setValid(false);
			} catch (IllegalArgumentException e) {
				log.warning(e);
				FacesMessage message = MessageFactory.getMessage(context,
						MessageFactory.CONVERSION_MESSAGE_ID);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(getClientId(context), message);
				setValid(false);
			} catch (Exception e) {
				log.warning(e);
				FacesMessage message = MessageFactory.getMessage(context,
						MessageFactory.CONVERSION_MESSAGE_ID);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(getClientId(context), message);
				setValid(false);
			}
		}
	}

	// ------------------------------------------------------ Validation Methods

	/**
	 * <p>
	 * Perform the following algorithm to validate the local value of this
	 * {@link javax.faces.component.UIInput}.
	 * </p>
	 * <ul>
	 * <li>Retrieve the submitted value with <code>getSubmittedValue()</code>.
	 * If this returns null, exit without further processing. (This indicates
	 * that no value was submitted for this component.)</li>
	 * 
	 * <li> Convert the submitted value into a "local value" of the appropriate
	 * data type by calling {@link #getConvertedValue}.</li>
	 * 
	 * <li>Validate the property by calling {@link #validateValue}.</li>
	 * 
	 * <li>If the <code>valid</code> property of this component is still
	 * <code>true</code>, retrieve the previous value of the component (with
	 * <code>getValue()</code>), store the new local value using
	 * <code>setValue()</code>, and reset the submitted value to null. If the
	 * local value is different from the previous value of this component, fire
	 * a {@link ValueChangeEvent} to be broadcast to all interested listeners.</li>
	 * </ul>
	 * 
	 * <p>
	 * Application components implementing {@link javax.faces.component.UIInput} that wish to perform
	 * validation with logic embedded in the component should perform their own
	 * correctness checks, and then call the <code>super.validate()</code>
	 * method to perform the standard processing described above.
	 * </p>
	 * 
	 * @param context
	 *            The {@link javax.faces.context.FacesContext} for the current request
	 * 
	 * @exception NullPointerException
	 *                if <code>context</code> is null
	 */
	public void validate(FacesContext context) {

		if (context == null) {
			throw new NullPointerException();
		}

		// Submitted value == null means "the component was not submitted
		// at all"; validation should not continue
		Object submittedValue = getSubmittedValue();
		if (submittedValue == null) {
			return;
		}

		Object newValue = null;

		try {
			newValue = getConvertedValue(context, submittedValue);
		} catch (ConverterException ce) {
			Throwable cause = ce.getCause();
			if(cause!=null){
				log.warning(ce);
			}
			addConversionErrorMessage(context, ce, submittedValue);
			setValid(false);
		}

		validateValue(context, newValue);

		// If our value is valid, store the new value, erase the
		// "submitted" value, and emit a ValueChangeEvent if appropriate
		if (isValid()) {
			Object previous = getValue();
			setValue(newValue);
			setSubmittedValue(null);
			if (compareValues(previous, newValue)) {
				queueEvent(new ValueChangeEvent(this, previous, newValue));
			}
		}

	}

	/**
	 * <p>
	 * Convert the submitted value into a "local value" of the appropriate data
	 * type, if necessary. Employ the following algorithm to do so:
	 * </p>
	 * <ul>
	 * <li>If a <code>Renderer</code> is present, call
	 * <code>getConvertedValue()</code> to convert the submitted value.</li>
	 * <li>If no <code>Renderer</code> is present, and the submitted value is
	 * a String, locate a {@link Converter} as follows:
	 * <ul>
	 * <li>If <code>getConverter()</code> returns a non-null
	 * {@link Converter}, use that instance.</li>
	 * <li>Otherwise, if a value binding for <code>value</code> exists, call
	 * <code>getType()</code> on it.
	 * <ul>
	 * <li>If this call returns <code>null</code>, assume the output type is
	 * <code>String</code> and perform no conversion.</li>
	 * <li>Otherwise, call <code>Application.createConverter(Class)</code> to
	 * locate any registered {@link Converter} capable of converting data values
	 * of the specified type.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <li>If a {@link Converter} instance was located, call its
	 * <code>getAsObject()</code> method to perform the conversion. If
	 * conversion fails:
	 * <ul>
	 * <li>Enqueue an appropriate error message by calling the
	 * <code>addMessage()</code> method on the <code>FacesContext</code>.</li>
	 * <li>Set the <code>valid</code> property on this component to
	 * <code>false</code> </li>
	 * </ul>
	 * </li>
	 * <li>Otherwise, use the submitted value without any conversion</li>
	 * </ul>
	 * </li>
	 * 
	 * </p>
	 * 
	 * <p>
	 * This method can be overridden by subclasses for more specific behavior.
	 * </p>
	 * 
	 */

	protected Object getConvertedValue(FacesContext context,
			Object newSubmittedValue) throws ConverterException {
		Renderer renderer = getRenderer(context);
		Object newValue = null;

		if (renderer != null) {
			newValue = renderer.getConvertedValue(context, this,
					newSubmittedValue);
		} else if (newSubmittedValue instanceof String) {
			// If there's no Renderer, and we've got a String,
			// run it through the Converter (if any)
			Converter converter = getConverterWithType(context);
			if (converter != null) {
				newValue = converter.getAsObject(context, this,
						(String) newSubmittedValue);
			} else {
				newValue = newSubmittedValue;
			}
		} else {
			newValue = newSubmittedValue;
		}
		return newValue;
	}

	/**
	 * 
	 * <p>
	 * Set the "valid" property according to the below algorithm.
	 * </p>
	 * 
	 * <ul>
	 * 
	 * <li>If the <code>valid</code> property on this component is still
	 * <code>true</code>, and the <code>required</code> property is also
	 * true, ensure that the local value is not empty (where "empty" is defined
	 * as <code>null</code> or a zero-length String. If the local value is
	 * empty:
	 * <ul>
	 * <li>Enqueue an appropriate error message by calling the
	 * <code>addMessage()</code> method on the <code>FacesContext</code>
	 * instance for the current request.</li>
	 * <li>Set the <code>valid</code> property on this component to
	 * <code>false</code>.</li>
	 * </ul>
	 * </li>
	 * <li>If the <code>valid</code> property on this component is still
	 * <code>true</code>, and the local value is not empty, call the
	 * <code>validate()</code> method of each {@link Validator} registered for
	 * this {@link javax.faces.component.UIInput}, followed by the method pointed at by the
	 * <code>validatorBinding</code> property (if any). If any of these
	 * validators or the method throws a {@link ValidatorException}, catch the
	 * exception, add its message (if any) to the {@link FacesContext}, and set
	 * the <code>valid</code> property of this component to false.</li>
	 * 
	 * </ul>
	 * 
	 */

	protected void validateValue(FacesContext context, Object newValue) {
		// If our value is valid, enforce the required property if present
		if (isValid() && isRequired() && isEmpty(newValue)) {
			FacesMessage message = MessageFactory.getMessage(context,
					MessageFactory.REQUIRED_MESSAGE_ID);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(getClientId(context), message);
			setValid(false);
		}

		// If our value is valid and not empty, call all validators
		// DENNIS:remove isEmpty, WHY JSF RI check is Empty here? a Bug?
		if (isValid() /*&& !isEmpty(newValue)*/) {
			if (this._validators != null) {
				Iterator validators = this._validators.iterator();
				while (validators.hasNext()) {
					Validator validator = (Validator) validators.next();
					try {
						validator.validate(context, this, newValue);
					} catch (ValidatorException ve) {
						// If the validator throws an exception, we're
						// invalid, and we need to add a message
						setValid(false);
						FacesMessage message = ve.getFacesMessage();
						if (message != null) {
							message.setSeverity(FacesMessage.SEVERITY_ERROR);
							context.addMessage(getClientId(context), message);
						}
					}
				}
			}
			if (_validatorBinding != null) {
				try {
					_validatorBinding.invoke(context, new Object[] { context,
							this, newValue });
				} catch (EvaluationException ee) {
					if (ee.getCause() instanceof ValidatorException) {
						ValidatorException ve = (ValidatorException) ee
								.getCause();

						// If the validator throws an exception, we're
						// invalid, and we need to add a message
						setValid(false);
						FacesMessage message = ve.getFacesMessage();
						if (message != null) {
							message.setSeverity(FacesMessage.SEVERITY_ERROR);
							context.addMessage(getClientId(context), message);
						}
					} else {
						// Otherwise, rethrow the EvaluationException
						throw ee;
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Return <code>true</code> if the new value is different from the
	 * previous value.
	 * </p>
	 * 
	 * @param previous
	 *            old value of this component (if any)
	 * @param value
	 *            new value of this component (if any)
	 */
	protected boolean compareValues(Object previous, Object value) {

		if (previous == null) {
			return (value != null);
		} else if (value == null) {
			return (true);
		} else {
			return (!(previous.equals(value)));
		}

	}

	/**
	 * Executes validation logic.
	 */
	private void executeValidate(FacesContext context) {
		try {
			validate(context);
		} catch (RuntimeException e) {
			context.renderResponse();
			throw e;
		}

		if (!isValid()) {
			context.renderResponse();
		}
	}

	private boolean isEmpty(Object value) {

		if (value == null) {
			return (true);
		} else if ((value instanceof String) && (((String) value).length() < 1)) {
			return (true);
		} else if (value.getClass().isArray()) {
			if (0 == java.lang.reflect.Array.getLength(value)) {
				return (true);
			}
		} else if (value instanceof List) {
			if (0 == ((List) value).size()) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * <p>
	 * The set of {@link Validator}s associated with this
	 * <code>UIComponent</code>.
	 * </p>
	 */
	List _validators = null;

	/**
	 * <p>
	 * Add a {@link javax.faces.validator.Validator} instance to the set associated with this
	 * {@link javax.faces.component.UIInput}.
	 * </p>
	 * 
	 * @param validator
	 *            The {@link Validator} to add
	 * 
	 * @exception NullPointerException
	 *                if <code>validator</code> is null
	 */
	public void addValidator(Validator validator) {

		if (validator == null) {
			throw new NullPointerException();
		}
		if (_validators == null) {
			_validators = new ArrayList();
		}
		_validators.add(validator);

	}

	/**
	 * <p>
	 * Return the set of registered {@link javax.faces.validator.Validator}s for this {@link javax.faces.component.UIInput}
	 * instance. If there are no registered validators, a zero-length array is
	 * returned.
	 * </p>
	 */
	public Validator[] getValidators() {

		if (_validators == null) {
			return (new Validator[0]);
		} else {
			return ((Validator[]) _validators.toArray(new Validator[_validators
					.size()]));
		}

	}

	/**
	 * <p>
	 * Remove a {@link javax.faces.validator.Validator} instance from the set associated with this
	 * {@link javax.faces.component.UIInput}, if it was previously associated. Otherwise, do nothing.
	 * </p>
	 * 
	 * @param validator
	 *            The {@link javax.faces.validator.Validator} to remove
	 */
	public void removeValidator(Validator validator) {

		if (_validators != null) {
			_validators.remove(validator);
		}

	}

	// ------------------------------------------------ Event Processing Methods

	/**
	 * <p>
	 * Add a new {@link ValueChangeListener} to the set of listeners interested
	 * in being notified when {@link ValueChangeEvent}s occur.
	 * </p>
	 * 
	 * @param listener
	 *            The {@link ValueChangeListener} to be added
	 * 
	 * @exception NullPointerException
	 *                if <code>listener</code> is <code>null</code>
	 */
	public void addValueChangeListener(ValueChangeListener listener) {

		addFacesListener(listener);

	}

	/**
	 * <p>
	 * Return the set of registered {@link javax.faces.event.ValueChangeListener}s for this
	 * {@link javax.faces.component.UIInput} instance. If there are no registered listeners, a
	 * zero-length array is returned.
	 * </p>
	 */
	public ValueChangeListener[] getValueChangeListeners() {

		ValueChangeListener vcl[] = (ValueChangeListener[]) getFacesListeners(ValueChangeListener.class);
		return (vcl);

	}

	/**
	 * <p>
	 * Remove an existing {@link ValueChangeListener} (if any) from the set of
	 * listeners interested in being notified when {@link ValueChangeEvent}s
	 * occur.
	 * </p>
	 * 
	 * @param listener
	 *            The {@link ValueChangeListener} to be removed
	 * 
	 * @exception NullPointerException
	 *                if <code>listener</code> is <code>null</code>
	 */
	public void removeValueChangeListener(ValueChangeListener listener) {

		removeFacesListener(listener);

	}

	// ----------------------------------------------------- StateHolder Methods
	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {

		Object values[] = new Object[9];
		values[0] = super.saveState(context);
		values[1] = _required ? Boolean.TRUE : Boolean.FALSE;
		values[2] = _requiredSet ? Boolean.TRUE : Boolean.FALSE;
		values[3] = this._valid ? Boolean.TRUE : Boolean.FALSE;
		values[4] = _immediate ? Boolean.TRUE : Boolean.FALSE;
		values[5] = _immediateSet ? Boolean.TRUE : Boolean.FALSE;
		values[6] = saveAttachedState(context, _validators);
		values[7] = saveAttachedState(context, _validatorBinding);
		values[8] = saveAttachedState(context, _valueChangeMethod);
		return (values);

	}
	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_required = ((Boolean) values[1]).booleanValue();
		_requiredSet = ((Boolean) values[2]).booleanValue();
		_valid = ((Boolean) values[3]).booleanValue();
		_immediate = ((Boolean) values[4]).booleanValue();
		_immediateSet = ((Boolean) values[5]).booleanValue();
		List restoredValidators = null;
		Iterator iter = null;

		if (null != (restoredValidators = (List) restoreAttachedState(context,
				values[6]))) {
			// if there were some validators registered prior to this
			// method being invoked, merge them with the list to be
			// restored.
			if (null != _validators) {
				iter = restoredValidators.iterator();
				while (iter.hasNext()) {
					_validators.add(iter.next());
				}
			} else {
				_validators = restoredValidators;
			}
		}

		_validatorBinding = (MethodBinding) restoreAttachedState(context,
				values[7]);
		_valueChangeMethod = (MethodBinding) restoreAttachedState(context,
				values[8]);

	}

	private Converter getConverterWithType(FacesContext context) {
		Converter converter = getConverter();
		if (converter != null) {
			return converter;
		}

		ValueBinding valueBinding = getValueBinding("value");
		if (valueBinding == null) {
			return null;
		}

		Class converterType = valueBinding.getType(context);
		// if converterType is null, String, or Object, assume
		// no conversion is needed
		if (converterType == null || converterType == String.class
				|| converterType == Object.class) {
			return null;
		}

		// if getType returns a type for which we support a default
		// conversion, acquire an appropriate converter instance.
		try {
			Application application = context.getApplication();
			return application.createConverter(converterType);
		} catch (Exception e) {
			return (null);
		}
	}

	private void addConversionErrorMessage(FacesContext context,
			ConverterException ce, Object value) {
		FacesMessage message = ce.getFacesMessage();
		if (message == null) {
			message = MessageFactory.getMessage(context,
					MessageFactory.CONVERSION_MESSAGE_ID);
			if (message.getDetail() == null) {
				message.setDetail(ce.getMessage());
			}
		}

		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		context.addMessage(getClientId(context), message);
	}
	
	//-------------end of code refer ----------------
	
	
	//--------------ZUL JSF implemenation ----------------
	
	
	
	/**
	 * Override Method,
	 * if instance implements {@link ClientInputSupport} (always for now), 
	 * then i will try to set the name/value which get from ClientInputSupport into zulcomp.
	 * For Example of TextboxComponent, a name=form:compid will set to zulcomp and the result in HTML  
	 * will likes (&lt;input id=&quot;z_fm_2c&quot; type=&quot;text&quot; name=&quot;helloForm:txt1&quot; value=&quot;0&quot; z.type=&quot;zul.widget.Txbox&quot;/&gt; ).
	 * And then , after form submit, I can decode the submitted value from request by name "helloForm:txt1".  
	 *  
	 */
	protected void afterZULComponentComposed(Component zulcomp){
		super.afterZULComponentComposed(zulcomp);
		if(this instanceof ClientInputSupport){
			String name = ((ClientInputSupport)this).getInputAttributeName();
			String value = ((ClientInputSupport)this).getInputAttributeValue();
			try {
				Fields.setField(zulcomp,name,value,true);
			} catch (Exception x){
				throw new RuntimeException(x.getMessage(),x);
			}
		}
	}

	//	--------------Client Input Interface----------
	
	/**
	 * Return ZUL Component attribute name which can handler the submition of input. 
	 *  
	 * Note : Default is "name"
	 * 
	 * @see ClientInputSupport
	 */
	public String getInputAttributeName() {
		return "name";
	}

	/**
	 * Return ZUL Component attribute value which can handler the submition of input. 
	 * @see ClientInputSupport
	 * @see javax.faces.component.UIComponent#getClientId(FacesContext)
	 */
	public String getInputAttributeValue() {
		String cid = super.getClientId(getFacesContext());
		return cid;
	}
	
	/**
	 * Decode value in request's parameter. 
	 * call by {@link #decode}
	 */
	protected void clientInputDecode(FacesContext context) {
		String clientId = this.getClientId(context);
		Map requestMap = context.getExternalContext().getRequestParameterMap();
		if (requestMap.containsKey(clientId)) {
			String newValue = (String)context.getExternalContext().getRequestParameterMap().get(clientId);
			setSubmittedValue(newValue);
		}
	}

}
