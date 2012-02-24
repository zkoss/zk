/* BindActivationListener.java

	Purpose:
		
	Description:
		
	History:
		2012/2/23 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.List;

import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.DesktopActivationListener;
import org.zkoss.zk.ui.util.DesktopInit;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * To handle binder activation
 * @author dennis
 * @since 6.0.1
 */
public class BindActivationListener implements DesktopInit,Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public void init(Desktop desktop, Object request) throws Exception {
		desktop.addListener(new ActivationListener());
	}
	
	/**
	 * has to implement UiLifeCycle and DesktopActivationListener, then desktop call it when sessionDidActivate();
	 */
	private static class ActivationListener implements ExecutionInit, DesktopActivationListener, Serializable{

		private static final long serialVersionUID = 1L;

		
		@Override
		public void didActivate(Desktop desktop) {
			//do binder didActivate is not work, there is no ExecutiionResolver when didSessionActivate.
			//I delay it to first execution come in.
		}

		@Override
		public void willPassivate(Desktop desktop) {
			//for the case there is no execution come in.
			desktop.removeAttribute(BinderImpl.ACTIVATORS);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void init(Execution exec, Execution parent) throws Exception {
			List<BinderCtrl> binders = (List<BinderCtrl>)exec.getDesktop().removeAttribute(BinderImpl.ACTIVATORS);
			if(binders!=null){
				for(BinderCtrl ctrl:binders){
					ctrl.didActivate();
				}
			}
		}	
	}
}
