/* BindUiLifeCycle.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2011 1:19:14 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.tracker.impl;

import java.util.Collection;
import java.util.Iterator;

import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.UiLifeCycle;

/**
 * Track Binding CRUD and dependent tracking management. 
 * @author henrichen
 * @since 6.0.0
 */
public class BindUiLifeCycle implements UiLifeCycle {
	public void afterComponentAttached(Component comp, Page page) {
		if (comp.getDesktop() != null) {
			//check if this component already binded
			final Binder selfBinder = BinderUtil.getBinder(comp);
			if (selfBinder == null) {
				//check if parent exists any binder
				final Binder binder = BinderUtil.getBinder(comp,true);
				
				if (binder != null && (binder instanceof BinderImpl)) {
					//ZK-603, ZK-604, ZK-605
					//register internal ON_BIND_INIT event listener to delay the timing of init and loading bindings
					comp.addEventListener(10000, BinderImpl.ON_BIND_INIT, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							final Component comp = event.getTarget();
							comp.removeEventListener(BinderImpl.ON_BIND_INIT, this);
							//ZK-611 have wrong binding on a removed treecell in a template
							//if it was detached, ignore it
							if(comp.getPage()==null){
								return;
							}
							
							final Binder innerBinder = BinderUtil.getBinder(comp);
							if(innerBinder!=null){//it was already handled by innerBinder, ignore it								
								return;
							}
							
							if(binder instanceof AnnotateBinder){
								new AnnotateBinderHelper(binder).initComponentBindings(comp);
							}
								
							
							binder.loadComponent(comp,true);
							
							//[Dennis,20120925], this code was added when fixing issue zk-739, 
							//but , inside binder.initComponentBindings, it shall do this already, I am not sure why.
							if (comp.getAttribute(BinderImpl.VAR) != null)
								BinderUtil.markHandling(comp, binder);
						}
					});
					//post ON_BIND_INIT event
					Events.postEvent(new Event(BinderImpl.ON_BIND_INIT, comp));
				}
			}
		}
	}

	public void afterComponentDetached(Component comp, Page prevpage) {
		removeBindings(comp);
	}

	public void afterComponentMoved(Component parent, Component child,
			Component prevparent) {
		//do nothing
	}

	public void afterPageAttached(Page page, Desktop desktop) {
		//do nothing
	}

	public void afterPageDetached(Page page, Desktop prevdesktop) {
		final Collection<Component> comps = page.getRoots();
		for(final Iterator<Component> it = comps.iterator(); it.hasNext();) {
			final Component comp = it.next();
			removeBindings(comp); 
		}
	}
	private void removeBindings(Component comp) {
		removeBindings0(comp);
		for(final Iterator<Component> it = comp.getChildren().iterator(); it.hasNext();) {
			final Component kid = it.next();
			if (kid != null) {
				removeBindings(kid); //recursive
			}
		}
	}
	
	private void removeBindings0(Component comp) {
		//A component with renderer; might need to remove $MODEL$
		final Object installed = comp.removeAttribute(BinderImpl.RENDERER_INSTALLED); 
		if (installed != null) { 
			BindELContext.removeModel(comp);
		}
		final Binder binder = BinderUtil.getBinder(comp);
		if (binder != null) {
			binder.removeBindings(comp);
		}
	}
	
}
