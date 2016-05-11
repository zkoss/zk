/** ProxyNode.java.

	Purpose:
		
	Description:
		
	History:
 		Tue May 10 18:44:32 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.util.Callback;

/**
 * A proxy object node (For caching SavePropertyBinding)
 *
 * @author jameschu
 * @since 8.0.2
 */
/* package */class ProxyNode implements Serializable {
	private String _property;
	private ProxyNode _parent;
	private Set<Pair<String, SavePropertyBinding>> _cachedSavePropertyBinding;
	private transient Callback _onDirtyChangeCallback;
	private transient Callback _onDataChangeCallback;

	public ProxyNode(String property, ProxyNode parent) {
		this._property = property;
		this._parent = parent;
		_cachedSavePropertyBinding = new HashSet<Pair<String, SavePropertyBinding>>(2);
	}

	public String getProperty() {
		return _property;
	}

	public void setProperty(String property) {
		this._property = property;
	}

	public ProxyNode getParent() {
		return _parent;
	}

	public void setParent(ProxyNode parent) {
		this._parent = parent;
	}

	public Set<Pair<String, SavePropertyBinding>> getCachedSavePropertyBinding() {
		return _cachedSavePropertyBinding;
	}

	public Callback getOnDirtyChangeCallback() {
		return _onDirtyChangeCallback;
	}

	public void setOnDirtyChangeCallback(Callback onDirtyChangeCallback) {
		if (_parent != null)
			throw new UnsupportedOperationException("Not support!");
		_onDirtyChangeCallback = onDirtyChangeCallback;
	}

	public Callback getOnDataChangeCallback() {
		return _onDataChangeCallback;
	}

	public void setOnDataChangeCallback(Callback onDataChangeCallback) {
		if (_parent != null)
			throw new UnsupportedOperationException("Not support!");
		_onDataChangeCallback = onDataChangeCallback;
	}
}