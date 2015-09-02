/* SimpleBindXelContext.java

	Purpose:
		
	Description:
		
	History:
		3:58 PM 8/31/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.Binder;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.util.SimpleXelContext;
import org.zkoss.zk.ui.Component;

/**
 * Internal use only.
 * This implementation is used to boost the resolver's lookup time, as long as
 * the resolving variable matches <code>self</code> or the name of the VM's ID.
 * @author jumperchen
 * @since 8.0.0
 */
public class SimpleBindXelContext extends SimpleXelContext {
	private static final String VM = "vm";
	private Component _cmp;
	private Binder _binder;
	private boolean inited;
	public SimpleBindXelContext(Component cmp, Binder binder,
			VariableResolver resolver, FunctionMapper mapper) {
		super(resolver, mapper);
		_cmp = cmp;
		_binder = binder;
	}
	public Component getSelf() {
		return _cmp;
	}
	public Binder getBinder() {
		if (_binder == null && !inited) {
			inited = true;
			_binder = BinderUtil.getBinder(_cmp);
		}
		return _binder;
	}
	public String getViewModelName() {
		if (getBinder() == null)
			return null;
		String vmName = (String) _binder.getView().getAttribute(BindComposer.VM_ID);
		if (vmName == null && _binder.getView().hasAttribute(VM))
			return VM;
		return vmName;
	}
	public Object getViewModel() {
		if (getBinder() != null)
			return _binder.getViewModel();
		return null;
	}
}
