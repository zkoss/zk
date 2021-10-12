package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.LoadTemplateBinding;
import org.zkoss.zk.ui.Component;

public abstract class LoadTemplateBindingImpl extends BindingImpl implements LoadTemplateBinding {
	private static final long serialVersionUID = 1L;

	public LoadTemplateBindingImpl(Binder binder, Component comp, Map<String, Object> args) {
		super(binder, comp, args);
	}
}
