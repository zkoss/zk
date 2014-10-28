/** HtmlShadowElement.java.

	Purpose:
		
	Description:
		
	History:
		12:47:42 PM Oct 22, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ShadowElementCtrl;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A skeleton of shadow element that represents as a <i>shadow</i> tree.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class HtmlShadowElement extends AbstractComponent implements
		ShadowElement, ShadowElementCtrl {
	private static final Logger log = LoggerFactory.getLogger(HtmlShadowElement.class);
	private static final long serialVersionUID = 20141022145906L;
	private transient Map<String, Object> _props;
	protected Component _host;
	public HtmlShadowElement() {
		init();
	}
	private void init() {
		_props = new LinkedHashMap<String, Object>();
	}

	public void setShadowHost(Component host) {
		if (getParent() != null) {
			throw new UiException("As a shadow child cannot be a shadow root. [" + this + "]");
		}
		if (host == null) {
			throw new UiException("The shadow host cannot be null. [" + this + "], please use detach() method instead!.");
		}
		_host = host;
		((ComponentCtrl) host).addShadowRoot(this);
	}
	
	public void setParent(Component parent) {
		if (_host != null && parent != null) {
			throw new UiException("As a shadow root cannot be a child of a shadow element.");
		}
		super.setParent(parent);
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof ShadowElement))
			throw new UiException("Unsupported parent for pseudo element: "
					+ parent);
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof ShadowElement))
			throw new UiException("Unsupported child for pseudo element: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
	
	//Cloneable//
	public Object clone() {
		final HtmlShadowElement clone = (HtmlShadowElement)super.clone();
		clone.init();
		clone._props.putAll(_props);
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


	public Component getShadowHost() {
		return _host;
	}
}