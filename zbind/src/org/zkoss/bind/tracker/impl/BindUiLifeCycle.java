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

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
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
 */
public class BindUiLifeCycle implements UiLifeCycle {
	private static final String ON_BIND_INIT = "onBindInit";
	
	public void afterComponentAttached(Component comp, Page page) {
		if (comp.getDesktop() != null) {
			//check if this component already binded
			final Binder selfBinder = (Binder) comp.getAttribute(BinderImpl.BINDER);
			if (selfBinder == null) {
				//check if parent exists any binder
				final Binder binder = (Binder) comp.getAttribute(BinderImpl.BINDER, true);
				if (binder != null) {
					//ZK-603, ZK-604, ZK-605
					//register internal ON_BIND_INIT event listener to delay the timing of init and loading bindings
					comp.addEventListener(10000, ON_BIND_INIT, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							final Component comp = event.getTarget();
							comp.removeEventListener(ON_BIND_INIT, this);
							//ZK-611 have wrong binding on a removed treecell in a template
							//if it was detached, ignore it
							if(comp.getParent()==null || comp.getPage()==null){
								return;
							}
							
							final Binder innerBinder = (Binder) comp.getAttribute(BinderImpl.BINDER);
							final BinderImpl binder = (BinderImpl) event.getData();

							if(innerBinder!=null && innerBinder!=binder){//it was already handled by innerBinder, ignore it								
								return;
							}
							
							new AnnotateBinderHelper(binder).initComponentBindings(comp);
							binder.loadComponent(comp);
						}
					});
					//post ON_BIND_INIT event
					Events.postEvent(new Event(ON_BIND_INIT, comp, binder));
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
		if (prevdesktop != null) {
			final Collection<Component> comps = prevdesktop.getComponents();
			for(final Iterator<Component> it = comps.iterator(); it.hasNext();) {
				final Component comp = it.next();
				removeBindings0(comp); 
			}
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
		final Binder binder = (Binder) comp.getAttribute(BinderImpl.BINDER);
		if (binder != null) {
			binder.removeBindings(comp);
		}
	}
	
}
