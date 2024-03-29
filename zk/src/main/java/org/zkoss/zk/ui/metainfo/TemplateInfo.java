/* TemplateInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 14:30:36 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.ExValue;

/**
 * Represent a template element.
 * @author tomyeh
 * @since 6.0.0
 */
public class TemplateInfo extends BranchInfo {
	private final String _name;
	private final ExValue _src;
	private final Map<String, ExValue> _params;

	private boolean _isAnnotationNamespacedRoot = false;

	/** Creates a template.
	 *
	 * @param parent the parent node (never null)
	 * @param name the name of the template (never null)
	 * @param params the map of parameters. Igored if null.
	 */
	public TemplateInfo(NodeInfo parent, String name, String src, Map<String, String> params, ConditionImpl cond) {
		super(parent, cond);

		if (name == null) // support "" an empty string for ZK 8.0.0 shadow element
			throw new IllegalArgumentException("null");
		_name = name;
		_src = src != null ? new ExValue(src, String.class) : null;

		if (params != null && !params.isEmpty()) {
			_params = new LinkedHashMap<String, ExValue>();
			for (Map.Entry<String, String> me : params.entrySet())
				_params.put(me.getKey(), new ExValue(me.getValue(), Object.class));
		} else
			_params = null;
	}

	/** Returns the name of the template info.
	 */
	public String getName() {
		return _name;
	}

	/** Returns the URI to create the template from, or null if not specified.
	 */
	public String getSrc(Component comp) {
		return _src != null ? (String) _src.getValue(_evalr, comp) : null;
	}

	/**
	 * Returns a readonly map of the parameters that are assigned
 	 * to the template.
	 * @since 10.0.0
	 */
	public Map<String, ExValue> getParameters() {
		return _params != null
				? Collections.unmodifiableMap(_params) :
					Collections.emptyMap();
	}

	/** Evaluates and returns a readonly map of parameters assigned
	 * to this template (never null).
	 */
	public Map<String, Object> resolveParameters(Component comp) {
		if (_params == null)
			return Collections.emptyMap();

		final Map<String, Object> params = new LinkedHashMap<String, Object>(); //eval order is important
		for (Map.Entry<String, ExValue> me : _params.entrySet())
			params.put(me.getKey(), (me.getValue()).getValue(_evalr, comp));
		return params;
	}

	//Object//
	public String toString() {
		return "[template:" + _name + ']';
	}

	/**
	 * @return whether this TemplateInfo is the first TemplateInfo that has annotation namespace or binding annotation.
	 * @since 8.5.2
	 */
	public boolean isAnnotationNamespacedRoot() {
		return _isAnnotationNamespacedRoot;
	}

	/**
	 * Sets whether this TemplateInfo is the first TemplateInfo that has annotation namespace or binding annotation.
	 * @param annotationNamespacedRoot
	 * @since 8.5.2
	 */
	public void setAnnotationNamespacedRoot(boolean annotationNamespacedRoot) {
		_isAnnotationNamespacedRoot = annotationNamespacedRoot;
	}
}
