/* AbstractBindingHelper.java

	Purpose:
		
	Description:
		
	History:
		2011/11/14 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zk.ui.Component;

/**
 * to help implement BinderImpl 
 * @author dennis
 *
 */
/*package */ abstract class AbstractBindingHandler implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final BinderImpl _binder;
	
	AbstractBindingHandler(BinderImpl binder){
		_binder = binder;
	}
	
	protected void doPrePhase(Phase phase, BindContext ctx) {
		final PhaseListener l = _binder.getPhaseListener(); 
		if ( l != null) {
			l.prePhase(phase, ctx);
		}
	}
	
	protected void doPostPhase(Phase phase, BindContext ctx) {
		final PhaseListener l = _binder.getPhaseListener();
		if (l != null) {
			l.postPhase(phase, ctx);
		}
	}
	
	@SuppressWarnings("unchecked")
	static protected Set<Property> getNotifys(BindContext ctx){
		return (Set<Property>)ctx.getAttribute(BinderImpl.NOTIFYS);
	}
	
	protected void clearValidationMessages(Binder binder, Component component,String attr){
		ValidationMessages vmsgs = ((BinderCtrl)binder).getValidationMessages();
		if(vmsgs!=null){
			vmsgs.clearMessages(component,attr);
		}
	}
}
