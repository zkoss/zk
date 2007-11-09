/* BranchInput.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 9, 2007 12:42:46 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Some code of this file is refer to Apache License Version 2.0
 the license file is http://www.apache.org/licenses/LICENSE-2.0 

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
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
 * The skeletal class used to implement the ZULJSF components which needs to
 * support {@link javax.faces.component.EditableValueHolder}.<br/> Components
 * should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * 
 * see Javadoc of <a
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/api/index.html">JSF
 * Specification</a>
 * 
 * @author Dennis.Chen
 * 
 */
abstract public class BranchInput extends BranchOutput implements
		EditableValueHolder, ClientInputSupport {
	private static final Log log = Log.lookup(RootComponent.class);

	// ------------code refer to Apache MyFaces-----------------

	private Object _submittedValue = null;

	private Boolean _required ;

	private Boolean _immediate ;

	private boolean _valid = true;

	private MethodBinding _validator = null;

	private MethodBinding _valueChangeListener = null;

	List _validatorList = null;

	private static final Validator[] EMPTY_VALIDATOR_ARRAY = new Validator[0];

	public Object getSubmittedValue() {
		return this._submittedValue;
	}

	public void setSubmittedValue(Object submittedValue) {
		this._submittedValue = submittedValue;
	}

	public boolean isRequired() {
		if (_required != null) {
			return (_required.booleanValue());
		}
		ValueBinding vb = getValueBinding("required");
		if (vb != null) {
			return Boolean.TRUE.equals(vb.getValue(getFacesContext()));
		} else {
			return false;
		}
	}

	public boolean isValid() {
		return this._valid;
	}

	public void setValid(boolean valid) {
		this._valid = valid;
	}

	public void setRequired(boolean required) {
		this._required = required ? Boolean.TRUE : Boolean.FALSE;
	}

	public boolean isImmediate() {
		if (_immediate != null) {
			return (_immediate.booleanValue());
		}
		ValueBinding vb = getValueBinding("immediate");
		if (vb != null) {
			return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
		} else {
			return false;
		}
	}

	public void setImmediate(boolean immediate) {
		_immediate = immediate ? Boolean.TRUE : Boolean.FALSE;
	}

	public MethodBinding getValidator() {
		return this._validator;
	}

	public void setValidator(MethodBinding validatorBinding) {
		this._validator = validatorBinding;
	}

	public MethodBinding getValueChangeListener() {
		return (this._valueChangeListener);
	}

	public void setValueChangeListener(MethodBinding valueChangeMethod) {
		this._valueChangeListener = valueChangeMethod;
	}

	/**
	 * Set the "submitted value" of this component from the relevant data in the
	 * current servlet request object.
	 * <p>
	 * If this component is not rendered, then do nothing; no output would have
	 * been sent to the client so no input is expected.
	 * <p>
	 * Invoke the inherited functionality, which typically invokes the renderer
	 * associated with this component to extract and set this component's
	 * "submitted value".
	 * <p>
	 * If this component is marked "immediate", then immediately apply
	 * validation to the submitted value found. On error, call context method
	 * "renderResponse" which will force processing to leap to the "render
	 * response" phase as soon as the "decode" step has completed for all other
	 * components.
	 */
	public void processDecodes(FacesContext context) {
		if (context == null) {
			throw new NullPointerException("context");
		}

		// Skip processing if our rendered flag is false
		if (!isRendered()) {
			return;
		}

		super.processDecodes(context);

		if (isImmediate()) {
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
	}

	/**
	 * <p>
	 * In addition to the standard <code>processValidators</code> behavior
	 * inherited from {@link javax.faces.component.UIComponentBase}, calls
	 * <code>validate()</code> if the <code>immediate</code> property is
	 * false (which is the default); if the component is invalid afterwards,
	 * calls {@link javax.faces.context.FacesContext#renderResponse}. If a
	 * <code>RuntimeException</code> is thrown during validation processing,
	 * calls {@link javax.faces.context.FacesContext#renderResponse} and
	 * re-throw the exception.
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
	}

	public void processUpdates(FacesContext context) {

		if (context == null) {
			throw new NullPointerException("context");
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

		// We (re)set to valid, so that component automatically gets
		// (re)validated
		setValid(true);
		super.decode(context);

		// call abstract method to decode input , add by dennis.
		clientInputDecode(context);
	}

	public void broadcast(FacesEvent event) throws AbortProcessingException {

		// why MyFaces check Event type here? should it never wrong?, remove by dennis...
		/*
		 * if (!(event instanceof ValueChangeEvent)) { throw new
		 * IllegalArgumentException("FacesEvent of class " +
		 * event.getClass().getName() + " not supported by UIInput"); }
		 */

		// invoke standard listeners attached to this component first
		super.broadcast(event);

		// check is ValueChangeEvent
		if (event instanceof ValueChangeEvent) {

			MethodBinding valueChangeListenerBinding = getValueChangeListener();
			if (valueChangeListenerBinding != null) {
				FacesContext context = getFacesContext();
				try {
					valueChangeListenerBinding.invoke(context,
							new Object[] { event });
				} catch (EvaluationException e) {
					Throwable cause = e.getCause();
					if (cause != null
							&& cause instanceof AbortProcessingException) {
						throw (AbortProcessingException) cause;
					} else {
						throw e;
					}
				}
			}
		}

	}

	public void updateModel(FacesContext context) {

		if (context == null) {
			throw new NullPointerException("content");
		}

		if (!isValid())
			return;
		if (!isLocalValueSet())
			return;

		ValueBinding vb = getValueBinding("value");
		if (vb == null)
			return;
		try {
			vb.setValue(context, getLocalValue());
			setValue(null);
			setLocalValueSet(false);
		} catch (EvaluationException ee) {
			Throwable cause = ee.getCause();
			if (cause != null) {
				log.warning("exception when set value(" + getLocalValue()
						+ ") to Bean , please check EL and Object type. ", ee);
			}

			String exceptionMessage = ee.getMessage();
			if (exceptionMessage == null) {
				addErrorMessage(context, this,
						MessageFactory.CONVERSION_MESSAGE_ID,
						new Object[] { getId() });
			} else {
				addErrorMessage(context, this, exceptionMessage,
						new Object[] { getId() });
			}

			setValid(false);
		} catch (Exception e) {
			log.warning(e);
			context.getExternalContext().log(e.getMessage(), e);
			addErrorMessage(context, this,
					MessageFactory.CONVERSION_MESSAGE_ID,
					new Object[] { getId() });
			setValid(false);
		}
	}

	protected void validateValue(FacesContext context, Object convertedValue) {
		boolean empty = convertedValue == null
				|| (convertedValue instanceof String && ((String) convertedValue)
						.length() == 0);

		if (isRequired() && empty) {
			addErrorMessage(context, this, MessageFactory.REQUIRED_MESSAGE_ID,
					new Object[] { getId() });
			setValid(false);
			return;
		}

		if (!empty) {
			callValidators(context, this, convertedValue);
		}
	}

	/**
	 * Determine whether the new value is valid, and queue a ValueChangeEvent if
	 * necessary.
	 * <p>
	 * The "submitted value" is converted to the necessary type; conversion
	 * failure is reported as an error and validation processing terminates for
	 * this component. See documentation for method getConvertedValue for
	 * details on the conversion process.
	 * <p>
	 * Any validators attached to this component are then run, passing the
	 * converted value.
	 * <p>
	 * The old value of this component is then fetched (possibly involving the
	 * evaluation of a value-binding expression, ie invoking a method on a user
	 * object). The old value is compared to the new validated value, and if
	 * they are different then a ValueChangeEvent is queued for later
	 * processing.
	 * <p>
	 * On successful completion of this method:
	 * <ul>
	 * <li> isValid() is true
	 * <li> isLocalValueSet() is true
	 * <li> submittedValue is reset to null
	 * <li> a ValueChangeEvent is queued if the new value != old value
	 * </ul>
	 */
	public void validate(FacesContext context) {

		if (context == null)
			throw new NullPointerException("context");
		Object submittedValue = getSubmittedValue();
		if (submittedValue == null)
			return;

		Object convertedValue = getConvertedValue(context, submittedValue);

		if (!isValid())
			return;

		validateValue(context, convertedValue);

		if (!isValid())
			return;

		Object previousValue = getValue();
		setValue(convertedValue);
		setSubmittedValue(null);
		if (compareValues(previousValue, convertedValue)) {
			queueEvent(new ValueChangeEvent(this, previousValue, convertedValue));
		}

	}

	/**
	 * Convert the provided object to the desired value.
	 * <p>
	 * If there is a renderer for this component, then call the renderer's
	 * getConvertedValue method. While this can of course be implemented in any
	 * way the renderer desires, it typically performs exactly the same
	 * processing that this method would have done anyway (ie that described
	 * below for the no-renderer case).
	 * <p>
	 * Otherwise:
	 * <ul>
	 * <li>If the submittedValue is not a String then just return the
	 * submittedValue unconverted.
	 * <li>If there is no "value" value-binding then just return the
	 * submittedValue unconverted.
	 * <li>Use introspection to determine the type of the target property
	 * specified by the value-binding, and then use Application.createConverter
	 * to find a converter that can map from String to the required type. Apply
	 * the converter to the submittedValue and return the result.
	 * </ul>
	 */
	protected Object getConvertedValue(FacesContext context,
			Object submittedValue) {
		try {
			Renderer renderer = getRenderer(context);
			if (renderer != null) {
				return renderer
						.getConvertedValue(context, this, submittedValue);
			} else if (submittedValue instanceof String) {
				Converter converter = findBranchInputConverter(context, this);
				if (converter != null) {
					return converter.getAsObject(context, this,
							(String) submittedValue);
				}
			}
		} catch (ConverterException e) {
			FacesMessage facesMessage = e.getFacesMessage();
			if (facesMessage != null) {
				context.addMessage(getClientId(context), facesMessage);
			} else {
				addErrorMessage(context, this,
						MessageFactory.CONVERSION_MESSAGE_ID,
						new Object[] { getId() });
			}
			setValid(false);
		}
		return submittedValue;
	}

	protected boolean compareValues(Object previous, Object value) {
		return previous == null ? (value != null) : (!previous.equals(value));
	}

	public void addValidator(Validator validator) {

		if (validator == null)
			throw new NullPointerException("validator");
		if (_validatorList == null) {
			_validatorList = new ArrayList();
		}
		_validatorList.add(validator);

	}

	public Validator[] getValidators() {
		return _validatorList != null ? (Validator[]) _validatorList
				.toArray(new Validator[_validatorList.size()])
				: EMPTY_VALIDATOR_ARRAY;
	}

	public void removeValidator(Validator validator) {
		if (validator == null)
			throw new NullPointerException("validator");
		if (_validatorList != null) {
			_validatorList.remove(validator);
		}
	}

	public void addValueChangeListener(ValueChangeListener listener) {
		addFacesListener(listener);
	}

	public ValueChangeListener[] getValueChangeListeners() {
		return (ValueChangeListener[]) getFacesListeners(ValueChangeListener.class);
	}

	public void removeValueChangeListener(ValueChangeListener listener) {
		removeFacesListener(listener);
	}

	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {

		Object values[] = new Object[7];
		values[0] = super.saveState(context);
		values[1] = _immediate;
		values[2] = _required;
		values[3] = this._valid ? Boolean.TRUE : Boolean.FALSE;
		values[4] = saveAttachedState(context, _validator);
		values[5] = saveAttachedState(context, _valueChangeListener);
		values[6] = saveAttachedState(context, _validatorList);
		return values;

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_immediate = ((Boolean) values[1]);
		_required = ((Boolean) values[2]);
		_valid = ((Boolean) values[3]).booleanValue();
		_validator = (MethodBinding) restoreAttachedState(context, values[4]);
		_valueChangeListener = (MethodBinding) restoreAttachedState(context,
				values[5]);
		_validatorList = (List) restoreAttachedState(context, values[6]);

	}

	// -------------end of code refer ----------------

	// --------------ZUL JSF implemenation ----------------

	/**
	 * Override Method, if instance implements {@link ClientInputSupport}
	 * (for now, it is always ), then i will try to set the name/value which get from
	 * ClientInputSupport into zulcomp. For Example of TextboxComponent, a
	 * name=form:compid will set to zulcomp and the result in HTML will likes
	 * (&lt;input id=&quot;z_fm_2c&quot; type=&quot;text&quot;
	 * name=&quot;helloForm:txt1&quot; value=&quot;0&quot;
	 * z.type=&quot;zul.widget.Txbox&quot;/&gt; ). And then , after form submit,
	 * I can decode the submitted value from request by name "helloForm:txt1".
	 * 
	 */
	protected void afterZULComponentComposed(Component zulcomp) {
		super.afterZULComponentComposed(zulcomp);
		if (this instanceof ClientInputSupport) {
			String att = ((ClientInputSupport) this).getInputAttributeName();
			String value = ((ClientInputSupport) this).getInputAttributeValue();
			try {
				Fields.setField(zulcomp, att, value, true);
			} catch (Exception x) {
				throw new RuntimeException(x.getMessage(), x);
			}
		}
	}

	// --------------Client Input Interface----------

	/**
	 * Return ZUL Component attribute name which can handler the submition of
	 * input.
	 * 
	 * Note : Default is "name"
	 * 
	 * @see ClientInputSupport
	 */
	public String getInputAttributeName() {
		return "name";
	}

	/**
	 * Return ZUL Component attribute value which can handler the submition of
	 * input.
	 * 
	 * @see ClientInputSupport
	 * @see javax.faces.component.UIComponent#getClientId(FacesContext)
	 */
	public String getInputAttributeValue() {
		String cid = super.getClientId(getFacesContext());
		return cid;
	}

	/**
	 * Decode value in request's parameter. call by {@link #decode}
	 */
	protected void clientInputDecode(FacesContext context) {
		String clientId = this.getClientId(context);
		Map requestMap = context.getExternalContext().getRequestParameterMap();
		if (requestMap.containsKey(clientId)) {
			String newValue = (String) context.getExternalContext()
					.getRequestParameterMap().get(clientId);
			setSubmittedValue(newValue);
		}
	}

	protected void addErrorMessage(FacesContext context, UIComponent component,
			String messageId, Object[] parms) {
		FacesMessage message = MessageFactory.getMessage(context, messageId);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		context.addMessage(component.getClientId(context), message);
	}

	private static void callValidators(FacesContext context, BranchInput input,
			Object convertedValue) {
		// first invoke the list of validator components
		Validator[] validators = input.getValidators();
		for (int i = 0; i < validators.length; i++) {
			Validator validator = validators[i];
			try {
				validator.validate(context, input, convertedValue);
			} catch (ValidatorException e) {
				input.setValid(false);
				FacesMessage facesMessage = e.getFacesMessage();
				if (facesMessage != null) {
					facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
					context.addMessage(input.getClientId(context),
									facesMessage);
				}
			}
		}

		// now invoke the validator method defined as a method-binding attribute
		// on the component
		MethodBinding validatorBinding = input.getValidator();
		if (validatorBinding != null) {
			try {
				validatorBinding.invoke(context, new Object[] { context, input,
						convertedValue });
			} catch (EvaluationException e) {
				input.setValid(false);
				Throwable cause = e.getCause();
				if (cause instanceof ValidatorException) {
					FacesMessage facesMessage = ((ValidatorException) cause)
							.getFacesMessage();
					if (facesMessage != null) {
						facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
						context.addMessage(input.getClientId(context),
								facesMessage);
					}
				} else {
					throw e;
				}
			}
		}
	}

	private static Converter findBranchInputConverter(
			FacesContext facesContext, BranchInput component) {
		// Attention!
		// This code is duplicated in myfaces implementation renderkit package.
		// If you change something here please do the same in the other class!

		Converter converter = component.getConverter();
		if (converter != null)
			return converter;

		// Try to find out by value binding
		ValueBinding vb = component.getValueBinding("value");
		if (vb == null)
			return null;

		Class valueType = vb.getType(facesContext);
		if (valueType == null)
			return null;

		if (String.class.equals(valueType))
			return null; // No converter needed for String type
		if (Object.class.equals(valueType))
			return null; // There is no converter for Object class

		try {
			return facesContext.getApplication().createConverter(valueType);
		} catch (FacesException e) {
			log.debug(
					"No Converter for type " + valueType.getName() + " found",
					e);
			return null;
		}
	}

}
