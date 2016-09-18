/* Input.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 21:59:11     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object; //since we have org.zkoss.zhtml.Object

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;

/**
 * The input tag.
 *
 * @author tomyeh
 */
public class Input extends AbstractTag {
	private transient boolean _byClient;

	static {
		addClientEvent(Input.class, Events.ON_CHANGE, 0);
		addClientEvent(Input.class, Events.ON_CHECK, 0);
	}

	public Input() {
		this("input");
	}

	protected Input(String tagnm) {
		super(tagnm);
		setValue("");
	}

	/**
	 * Returns the value of this input.
	 */
	public String getValue() {
		return (String) getDynamicProperty("value");
	}

	/**
	 * Sets the vallue of this input.
	 */
	public void setValue(String value) throws WrongValueException {
		setDynamicProperty("value", value);
	}

	/**
	 * Returns if the input is checked (type: checkbox or radio).
	 */
	public boolean isChecked() {
		final Boolean b = (Boolean) getDynamicProperty("checked");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets if the input is checked (type: checkbox or radio).
	 */
	public void setChecked(boolean checked) {
		setDynamicProperty("checked", checked ? Boolean.valueOf(checked) : null);
	}

	/**
	 * Returns the accept of this input tag.
	 * @since 8.0.3
	 */
	public String getAccept() {
		return (String) getDynamicProperty("accept");
	}

	/**
	 * Sets the accept of this input tag
	 * @since 8.0.3
	 */
	public void setAccept(String accept) throws WrongValueException {
		setDynamicProperty("accept", accept);
	}
	/**
	 * Returns the alt of this input tag.
	 * @since 8.0.3
	 */
	public String getAlt() {
		return (String) getDynamicProperty("alt");
	}

	/**
	 * Sets the alt of this input tag
	 * @since 8.0.3
	 */
	public void setAlt(String alt) throws WrongValueException {
		setDynamicProperty("alt", alt);
	}
	/**
	 * Returns the autocomplete of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getAutocomplete() {
		return (String) getDynamicProperty("autocomplete");
	}

	/**
	 * Sets the autocomplete of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAutocomplete(String autocomplete) throws WrongValueException {
		setDynamicProperty("autocomplete", autocomplete);
	}
	/**
	 * Returns the autofocus of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isAutofocus() {
		final Boolean b = (Boolean) getDynamicProperty("autofocus");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the autofocus of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAutofocus(boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? Boolean.valueOf(autofocus) : null);
	}

	/**
	 * Returns the dirname of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getDirname() {
		return (String) getDynamicProperty("dirname");
	}

	/**
	 * Sets the dirname of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setDirname(String dirname) throws WrongValueException {
		setDynamicProperty("dirname", dirname);
	}
	/**
	 * Returns the disabled of this input tag.
	 * @since 8.0.3
	 */
	public boolean isDisabled() {
		final Boolean b = (Boolean) getDynamicProperty("disabled");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the disabled of this input tag.
	 * @since 8.0.3
	 */
	public void setDisabled(boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? Boolean.valueOf(disabled) : null);
	}
	/**
	 * Returns the height of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getHeight() {
		return (String) getDynamicProperty("height");
	}

	/**
	 * Sets the height of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setHeight(String height) throws WrongValueException {
		setDynamicProperty("height", height);
	}
	/**
	 * Returns the inputmode of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getInputmode() {
		return (String) getDynamicProperty("inputmode");
	}

	/**
	 * Sets the inputmode of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setInputmode(String inputmode) throws WrongValueException {
		setDynamicProperty("inputmode", inputmode);
	}
	/**
	 * Returns the list of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getList() {
		return (String) getDynamicProperty("list");
	}

	/**
	 * Sets the list of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setList(String list) throws WrongValueException {
		setDynamicProperty("list", list);
	}
	/**
	 * Returns the max of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getMax() {
		return (String) getDynamicProperty("max");
	}

	/**
	 * Sets the max of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setMax(String max) throws WrongValueException {
		setDynamicProperty("max", max);
	}
	/**
	 * Returns the maxlength of this input tag.
	 * @since 8.0.3
	 */
	public Integer getMaxlength() {
		return (Integer) getDynamicProperty("maxlength");
	}

	/**
	 * Sets the maxlength of this input tag
	 * @since 8.0.3
	 */
	public void setMaxlength(Integer maxlength) throws WrongValueException {
		setDynamicProperty("maxlength", maxlength);
	}
	/**
	 * Returns the min of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getMin() {
		return (String) getDynamicProperty("min");
	}

	/**
	 * Sets the min of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setMin(String min) throws WrongValueException {
		setDynamicProperty("min", min);
	}
	/**
	 * Returns the multiple of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isMultiple() {
		final Boolean b = (Boolean) getDynamicProperty("multiple");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the multiple of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setMultiple(boolean multiple) throws WrongValueException {
		setDynamicProperty("multiple", multiple ? Boolean.valueOf(multiple) : null);
	}
	/**
	 * Returns the name of this input tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this input tag
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
	/**
	 * Returns the pattern of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getPattern() {
		return (String) getDynamicProperty("pattern");
	}

	/**
	 * Sets the pattern of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setPattern(String pattern) throws WrongValueException {
		setDynamicProperty("pattern", pattern);
	}
	/**
	 * Returns the placeholder of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getPlaceholder() {
		return (String) getDynamicProperty("placeholder");
	}

	/**
	 * Sets the placeholder of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setPlaceholder(String placeholder) throws WrongValueException {
		setDynamicProperty("placeholder", placeholder);
	}
	/**
	 * Returns the readonly of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isReadonly() {
		final Boolean b = (Boolean) getDynamicProperty("readonly");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the readonly of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setReadonly(Boolean readonly) throws WrongValueException {
		setDynamicProperty("readonly", readonly ? Boolean.valueOf(readonly) : null);
	}
	/**
	 * Returns the required of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isRequired() {
		final Boolean b = (Boolean) getDynamicProperty("required");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the required of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setRequired(boolean required) throws WrongValueException {
		setDynamicProperty("required", required ? Boolean.valueOf(required) : null);
	}
	/**
	 * Returns the selectiondirection of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getSelectiondirection() {
		return (String) getDynamicProperty("selectiondirection");
	}

	/**
	 * Sets the selectiondirection of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setSelectiondirection(String selectiondirection) throws WrongValueException {
		setDynamicProperty("selectiondirection", selectiondirection);
	}
	/**
	 * Returns the size of this input tag.
	 * @since 8.0.3
	 */
	public Integer getSize() {
		return (Integer) getDynamicProperty("size");
	}

	/**
	 * Sets the size of this input tag
	 * @since 8.0.3
	 */
	public void setSize(Integer size) throws WrongValueException {
		setDynamicProperty("size", size);
	}
	/**
	 * Returns the src of this input tag.
	 * @since 8.0.3
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this input tag
	 * @since 8.0.3
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}
	/**
	 * Returns the step of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public Integer getStep() {
		return (Integer) getDynamicProperty("step");
	}

	/**
	 * Sets the step of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setStep(Integer step) throws WrongValueException {
		setDynamicProperty("step", step);
	}
	/**
	 * Returns the type of this input tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this input tag
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
	/**
	 * Returns the width of this input tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getWidth() {
		return (String) getDynamicProperty("width");
	}

	/**
	 * Sets the width of this input tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setWidth(String width) throws WrongValueException {
		setDynamicProperty("width", width);
	}

	// -- Component --//
	/**
	 * Returns the widget class, "zhtml.Input".
	 * 
	 * @since 8.0.0
	 */
	public String getWidgetClass() {
		return "zhtml.Input";
	}

	// super//
	protected void smartUpdate(String attr, Object value) {
		if (!_byClient)
			super.smartUpdate(attr, value);
	}

	/**
	 * Processes an AU request.
	 *
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHANGE)) {
			InputEvent evt = InputEvent.getInputEvent(request, getValue());

			final String value = evt.getValue();
			_byClient = true;
			try {
				setValue(value);
			} finally {
				_byClient = false;
			}

			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);

			_byClient = true;
			try {
				setChecked(evt.isChecked());
			} finally {
				_byClient = false;
			}

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
