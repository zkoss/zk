/* AbstractBindingHelper.java

	Purpose:
		
	Description:
		
	History:
		2011/11/14 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Phase;
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zk.ui.Component;

/**
 * to help implement BinderImpl 
 * @author dennis
 *
 */
public abstract class AbstractBindingHandler implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Binder _binder;

	public void setBinder(Binder binder) {
		_binder = binder;
	}

	protected void doPrePhase(Phase phase, BindContext ctx) {
		((BinderImpl) _binder).doPrePhase(phase, ctx);
	}

	protected void doPostPhase(Phase phase, BindContext ctx) {
		((BinderImpl) _binder).doPostPhase(phase, ctx);
	}

	@SuppressWarnings("unchecked")
	protected static Set<Property> getNotifys(BindContext ctx) {
		return (Set<Property>) ctx.getAttribute(BinderImpl.NOTIFYS);
	}

	protected void clearValidationMessages(Binder binder, Component component, String attr) {
		ValidationMessages vmsgs = ((BinderCtrl) binder).getValidationMessages();
		if (vmsgs != null) {
			vmsgs.clearMessages(component, attr);
		}
	}

	//ZK-2289: Futher optimize zkbind memory consumption.
	protected <K, V> void addBinding(Map<K, List<V>> bindingsMap, K bkey, V binding) {
		final List<V> bindings = bindingsMap.get(bkey);
		final List<V> bindings0 = AllocUtil.inst.addList(bindings, binding);
		if (bindings0 != bindings) { //yes, use != ; not !equals
			bindingsMap.put(bkey, bindings0);
		}
	}

	protected String getSaveBindingDebugInfo(String operation, Object... args) {
		return MessageFormat.format(operation + ":binding.save() "
				+ "comp=[{0}],binding=[{1}],command=[{2}],evt=[{3}],notifys=[{4}]", args);
	}

	protected String getLoadBindingDebugInfo(String operation, Object... args) {
		return MessageFormat.format(operation + ":binding.load() "
				+ "comp=[{0}],binding=[{1}],context=[{2}],command=[{3}]", args);
	}
}
