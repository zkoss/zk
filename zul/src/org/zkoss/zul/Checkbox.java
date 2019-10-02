/* Checkbox.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 23:45:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.CheckEvent is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Checkbox extends LabelImageElement implements org.zkoss.zk.ui.ext.Disable {
	private Object _value;
	/** The name. */
	private String _name;
	/** Whether it is checked. */
	/*package*/ boolean _checked;
	private boolean _disabled;
	private String _autodisable;
	private boolean _indeterminate;
	private boolean _init = true;

	static {
		addClientEvent(Checkbox.class, Events.ON_CHECK, CE_IMPORTANT | CE_REPEAT_IGNORE);
		addClientEvent(Checkbox.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Checkbox.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
	}

	public Checkbox() {
	}

	public Checkbox(String label) {
		super(label);
	}

	public Checkbox(String label, String image) {
		super(label, image);
	}

	/**
	 * Return whether checkbox is in indeterminate state.
	 * Default: false.
	 *
	 * @return true if checkbox is indeterminate
	 * @since 8.6.0
	 */
	public boolean isIndeterminate() {
		return _indeterminate;
	}

	/**
	 * Set whether checkbox is in indeterminate state.
	 *
	 * @param indeterminate whether checkbox is indeterminate
	 * @since 8.6.0
	 */
	public void setIndeterminate(boolean indeterminate) {
		String mold = getMold();
		if ("switch".equals(mold) || "toggle".equals(mold))
			throw new UiException("Checkbox switch/toggle mold does not support indeterminate yet." + this);
		if (_indeterminate != indeterminate) {
			_indeterminate = indeterminate;
			smartUpdate("indeterminate", _indeterminate);
		}
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this checkbox.
	 * @since 6.0.0
	 */
	public String getAutodisable() {
		return _autodisable;
	}

	/** Sets a list of component IDs that shall be disabled when the user
	 * clicks this checkbox.
	 *
	 * <p>To represent the checkbox itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;checkbox id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;checkbox id="ok" autodisable="ok,cancel"/></code>
	 * that will disable
	 * both the ok and cancel checkboxes when an user clicks it.
	 *
	 * <p>The checkbox being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a checkbox is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>&lt;checkbox id="ok" autodisable="+self,+cancel"/></code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 *</code></pre>
	 *
	 * <p>Default: null.
	 * @since 5.0.0
	 */
	public void setAutodisable(String autodisable) {
		if (autodisable == null)
			autodisable = "";
		if (!Objects.equals(_autodisable, autodisable)) {
			_autodisable = autodisable;
			smartUpdate("autodisable", _autodisable);
		}
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	public boolean isChecked() {
		return _checked;
	}

	/** Sets whether it is checked,
	 * changing checked will set indeterminate to false.
	 */
	public void setChecked(boolean checked) {
		if (_checked != checked) {
			_checked = checked;
			smartUpdate("checked", _checked);
			if (!_init && _indeterminate) {
				setIndeterminate(false);
			}
		}
	}
	
	/** Returns the current state according to isIndeterminate() and isChecked().
	 *
	 * @return CHECKED, UNCHECKED or INDETERMINATE
	 * @since 9.0.0
	 */
	public State getState() {
		if (isIndeterminate())
			return State.INDETERMINATE;
		else
			return isChecked() ? State.CHECKED : State.UNCHECKED;
	}

	/** Returns the value.
	 * <p>Default: null. (since 6.5.0)
	 * @since 5.0.4
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) _value;
	}

	/** Sets the value.
	 * @param value the value;
	 * @since 5.0.4
	 */
	public <T> void setValue(T value) {
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	public String getName() {
		return _name;
	}

	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0)
			name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	//-- super --//
	/** Default: not childable.
	 */
	protected boolean isChildable() {
		return false;
	}

	public void setMold(String mold) {
		if (this._indeterminate && ("switch".equals(mold) || "toggle".equals(mold)))
			throw new UiException("Checkbox switch/toggle mold does not support indeterminate yet." + this);
		super.setMold(mold);
	}

	/** Returns the Style of checkbox label
	 *
	 * <p>Default: "z-checkbox"
	 * <p>Since 3.5.1
	 * 
	 */
	public String getZclass() {
		return _zclass == null ? "z-checkbox" : _zclass;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (_init)
			_init = false;
		if (_value != null)
			render(renderer, "value", _value);
		if (_autodisable != null)
			render(renderer, "autodisable", _autodisable);

		render(renderer, "disabled", _disabled);
		render(renderer, "name", _name);
		render(renderer, "indeterminate", _indeterminate);
		if (_checked)
			render(renderer, "checked", _checked);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onCheck.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			if ("tristate".equals(getMold())) {
				Boolean state = evt.getState();
				_checked = (state != null) && state;
				_indeterminate = state == null;
			} else {
				_checked = evt.isChecked();
				_indeterminate = false;
			}
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	protected void updateByClient(String name, Object value) {
		if ("disabled".equals(name))
			setDisabled(value instanceof Boolean ? ((Boolean) value).booleanValue()
					: "true".equals(Objects.toString(value)));
		else
			super.updateByClient(name, value);
	}

	/**
	 * This class is the return state for getState()
	 *
	 * @see #getState()
	 * @since 9.0.0
	 */
	public enum State {
		CHECKED, UNCHECKED, INDETERMINATE
	}
}
