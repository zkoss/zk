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
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Property;

/**
 * to help implement BinderImpl 
 * @author dennis
 *
 */
abstract class AbstractBindingHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final BinderImpl _binder;
	
	AbstractBindingHelper(BinderImpl binder){
		_binder = binder;
	}
	
	void doPrePhase(Phase phase, BindContext ctx) {
		final PhaseListener l = _binder.getPhaseListener(); 
		if ( l != null) {
			l.prePhase(phase, ctx);
		}
	}
	
	void doPostPhase(Phase phase, BindContext ctx) {
		final PhaseListener l = _binder.getPhaseListener();
		if (l != null) {
			l.postPhase(phase, ctx);
		}
	}
	
	@SuppressWarnings("unchecked")
	static Set<Property> getNotifys(BindContext ctx){
		return (Set<Property>)ctx.getAttribute(BinderImpl.NOTIFYS);
	}
}
