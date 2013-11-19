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
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
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
	
	static final Log log = Log.lookup(BindUiLifeCycle.class);
	private static final String ON_ZKBIND_LATER = "onZKBindLater";
	private static final String REMOVE_MARK = "$$RemoveMark$$";
	private static Extension _ext;
	
	public void afterComponentAttached(Component comp, Page page) {
		handleComponentAttached(comp);
	}
	
	protected void handleComponentAttached(Component comp){
		//ZK-2022, check if this component is in queue for removal 
		//if yes, then post and do processing later
		boolean removeMark = Boolean.TRUE.equals(comp.getAttribute(REMOVE_MARK));
		if(removeMark){
			//handle later
			comp.addEventListener(10000, ON_ZKBIND_LATER, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					final Component comp = event.getTarget();
					comp.removeEventListener(ON_ZKBIND_LATER, this);
					handleComponentAttached(comp);
				}
			});
			Events.postEvent(new Event(ON_ZKBIND_LATER, comp));
			return;
		}
		if (comp.getDesktop() != null) {
			//check if this component already binded
			Binder selfBinder = BinderUtil.getBinder(comp);
			if (selfBinder == null) {
				//check if parent exists any binder
				Binder parentBinder = BinderUtil.getBinder(comp,true);
				
				//post event to let the binder to handle binding later
				if (parentBinder != null && (parentBinder instanceof BinderImpl)) {
					//ZK-603, ZK-604, ZK-605
					//register internal ON_BIND_INIT event listener to delay the timing of init and loading bindings
					comp.addEventListener(10000, BinderImpl.ON_BIND_INIT, new EventListener<Event>() {
						
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
							
							//ZK-1640 command send 2 wrong ViewModel
							//check if there any parent binder again, don't use out-side parentBinder, it is not correct
							Binder binder = BinderUtil.getBinder(comp,true);
							if(binder == null){
								return;
							}
							
							//ZK-1699 Performance issue ZK-Bind getters are called multiple times
							//check if it is handling, if yes then skip to evaluate it.
							if(getExtension().isLifeCycleHandling(comp)){
								return;
							}
							
							if(binder instanceof AnnotateBinder){
								new AnnotateBinderHelper(binder).initComponentBindings(comp);
							}
							
							//ZK-1699, mark the comp and it's children are handling.
							//note:mark handing before load, because of some load will change(create or reset) the children structure
							//(consider F00769.zul if you bind to tree open , it will load children in loadComponent)
							getExtension().markLifeCycleHandling(comp);
							
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
		handleComponentDetached(comp);
	}
	protected void handleComponentDetached(Component comp) {
		//ZK-1887 should post the remove as well in detach
		comp.addEventListener(10000, BinderImpl.ON_BIND_CLEAN, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				final Component comp = event.getTarget();
				comp.removeAttribute(REMOVE_MARK);
				comp.removeEventListener(BinderImpl.ON_BIND_CLEAN, this);
				removeBindings(comp);
			}
		});
		//ZK-2022, make it is in queue of remove.
		comp.setAttribute(REMOVE_MARK, Boolean.TRUE);
		// post ON_BIND_INIT event
		Events.postEvent(new Event(BinderImpl.ON_BIND_CLEAN, comp));		
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
		
		getExtension().removeLifeCycleHandling(comp);
	}
	
	private static Extension getExtension() {
		if (_ext == null) {
			synchronized (BindUiLifeCycle.class) {
				if (_ext == null) {
					String clsnm = Library.getProperty("org.zkoss.bind.tracker.impl.extension");
					if (clsnm != null) {
						try {
							_ext = (Extension)Classes.newInstanceByThread(clsnm);
						} catch (Throwable ex) {
							log.realCauseBriefly("Unable to instantiate "+clsnm, ex);
						}
					}
					if (_ext == null)
						_ext = new DefaultExtension();
				}
			}
		}
		return _ext;
	}
	/**
	 * Internal use only.
	 * Mark a component and it's children are handling already in current execution.
	 * So, if the component attach to component tree(cause {@code #afterComponentAttached(Component, Page)}, 
	 * BindUiLifeCycle will not process it again.
	 */
	public static void markLifeCycleHandling(Component comp) {
		getExtension().markLifeCycleHandling(comp);
	}
	
	/** An interface used to extend the {@code BindUiLifeCycle}.
	 * The class name of the extension shall be specified in
	 * the library properties called org.zkoss.bind.tracker.impl.extension.
	 * <p>Notice that it is used only internally.
	 * @since 6.5.3
	 */
	public static interface Extension {
		public void markLifeCycleHandling(Component comp);
		public boolean isLifeCycleHandling(Component comp);		
		public void removeLifeCycleHandling(Component comp);
	}
	private static class DefaultExtension implements Extension {
		public void markLifeCycleHandling(Component comp) {}

		public boolean isLifeCycleHandling(Component comp) {
			return false;
		}
		public void removeLifeCycleHandling(Component comp) {}
	}
}
