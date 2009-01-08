/* HtmlMacroComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Apr 14 13:54:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.ext.Macro;

/**
 * The implemetation of a macro component for HTML-based clients.
 *
 * <p>Generally, a macro component is created automatically by ZK loader.
 * If a developer wants to create it manually, it has to instantiate from
 * the correct class, and then invoke {@link #afterCompose}.
 *
 * @author tomyeh
 */
public class HtmlMacroComponent extends HtmlBasedComponent implements Macro {
	private transient Map _props;
	private String _uri;
	/** An array of components created by this inline macro.
	 * It is used only if {@link #isInline}
	 */
	private Component[] _inlines;

	public HtmlMacroComponent() {
		init();
	}
	private void init() {
		_props = new LinkedHashMap();
		_props.put("includer", this);
	}

	//-- Macro --//
	/** Creates the child components after apply dynamic properties
	 * {@link #setDynamicProperty}.
	 *
	 * <p>The second invocation is ignored. If you want to recreate
	 * child components, use {@link #recreate} instead.
	 *
	 * <p>If a macro component is created by ZK loader, this method is invoked
	 * automatically. Developers need to invoke this method only if they create
	 * a macro component manually.
	 *
	 * <p>If this is an line macro, this method is invoked automatically
	 * if {@link #setParent} or {@link #setPage} called
	 */
	public void afterCompose() {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("No execution available.");

		if (isInline()) {
			if (_inlines != null)
				return; //don't do twice

			_inlines = exec.createComponents(
				_uri != null ? _uri: getDefinition().getMacroURI(), _props);
				//Note: it doesn't belong to any page/component
		} else {
			if (!getChildren().isEmpty())
				return; //don't do twice (silently)

			exec.createComponents(
				_uri != null ? _uri: getDefinition().getMacroURI(), this, _props);
		}
	}
	public String getMacroURI() {
		return _uri != null ? _uri: getDefinition().getMacroURI();
	}
	public void setMacroURI(String uri) {
		if (!Objects.equals(_uri, uri)) {
			if (uri != null && uri.length() == 0)
				throw new IllegalArgumentException("empty uri");
			_uri = uri;
			if (getParent() != null)
				recreate();
		}
	}
	public void recreate() {
		if (_inlines != null) {
			for (int j = 0; j < _inlines.length; ++j)
				_inlines[j].detach();
			_inlines = null;
		} else {
			getChildren().clear();
		}
		afterCompose();
	}
	public boolean isInline() {
		return getDefinition().isInlineMacro();
	}

	//Component//
	/** Changes the parent.
	 *
	 * <p>Note: if this is an inline macro ({@link #isInline}),
	 * this method actually changes the parent of all components created
	 * from the macro URI.
	 * In other word, an inline macro behaves like a controller of
	 * the components it created. It doesn't belong to any page or parent.
	 * Moreover, {@link #afterCompose} is called automatically if
	 * it is not called (and this is an inline macro).
	 */
	public void setParent(Component parent) {
		if (isInline()) {
			if (_inlines == null)
				afterCompose(); //autocreate

			for (int j = 0; j < _inlines.length; ++j)
				_inlines[j].setParent(parent);
		} else {
			super.setParent(parent);
		}
	}
	/** Changes the page.
	 *
	 * <p>Note: if this is an inline macro ({@link #isInline}),
	 * this method actually changes the page of all components created
	 * from the macro URI.
	 * In other word, an inline macro behaves like a controller of
	 * the components it created. It doesn't belong to any page or parent.
	 * Moreover, {@link #afterCompose} is called automatically if
	 * it is not called (and this is an inline macro).
	 */
	public void setPage(Page page) {
		if (isInline()) {
			if (_inlines == null)
				afterCompose(); //autocreate

			for (int j = 0; j < _inlines.length; ++j)
				_inlines[j].setPage(page);
		} else {
			super.setPage(page);
		}
	}
	public boolean isChildable() {
		return !isInline();
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		_props.remove("includer");
		Serializables.smartWrite(s, _props);
		_props.put("includer", this);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
		Serializables.smartRead(s, _props);
	}

	//Cloneable//
	public Object clone() {
		final HtmlMacroComponent clone = (HtmlMacroComponent)super.clone();
		clone.init();
		clone._props.putAll(_props);
		clone._props.put("includer", this);

		if (_inlines != null) { //deep clone
			clone._inlines = new Component[_inlines.length];
			for (int j = 0; j < _inlines.length; ++j)
				clone._inlines[j] = (Component)_inlines[j].clone();
		}
		return clone;
	}

	//-- DynamicPropertied --//
	public boolean hasDynamicProperty(String name) {
		return _props.containsKey(name);
	}
	public Object getDynamicProperty(String name) {
		return _props.get(name);
	}
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		_props.put(name, value);
	}
}
