/* BindUiLifeCycle.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2011 1:19:14 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.tracker.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.BindComposer;
import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.UiLifeCycle;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

/**
 * Track Binding CRUD and dependent tracking management. 
 * @author henrichen
 * @author jumperchen
 * @since 6.0.0
 */
public class BindUiLifeCycle implements UiLifeCycle {

	static final Logger log = LoggerFactory.getLogger(BindUiLifeCycle.class);
	public static final String ON_ZKBIND_LATER = "onZKBindLater";
	public static final String REMOVE_MARK = "$$RemoveMark$$";
	//F80: Speed up render, check component's subBinderAnnotation
	public static final String SKIP_BIND_INIT = "$$SkipBindInitMark$$";
	private static Extension _ext;

	public void afterComponentAttached(Component comp, Page page) {
		handleComponentAttached(comp);
	}

	protected void handleComponentAttached(Component comp) {
		//ZK-2022, check if this component is in queue for removal 
		//if yes, then post and do processing later
		boolean removeMark = Boolean.TRUE.equals(comp.getAttribute(REMOVE_MARK));
		if (removeMark) {
			//handle later
			comp.addEventListener(10000, ON_ZKBIND_LATER, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					final Component comp = event.getTarget();
					//remove mark to prevent zkbind1(B36-1968616) cause it not removed in detach handling listener
					comp.removeAttribute(REMOVE_MARK);
					comp.removeEventListener(ON_ZKBIND_LATER, this);
					handleComponentAttached(comp);
				}
			});
			Events.postEvent(new Event(ON_ZKBIND_LATER, comp));
			return;
		} else if (!Boolean.FALSE.equals(comp.removeAttribute(SKIP_BIND_INIT))) {
			//F80: Speed up render, check component's subBinderAnnotation
			if (comp instanceof ComponentCtrl && !((ComponentCtrl) comp).hasSubBindingAnnotation()
					&& !(comp instanceof ShadowElement))
				return;
		}
		if (comp.getDesktop() != null || comp instanceof ShadowElement) {
			//ZK-603, ZK-604, ZK-605
			//register internal ON_BIND_INIT event listener to delay the timing of init and loading bindings
			comp.addEventListener(10000, BinderImpl.ON_BIND_INIT, new EventListener<Event>() {

				public void onEvent(Event event) throws Exception {
					final Component comp = event.getTarget();
					comp.removeEventListener(BinderImpl.ON_BIND_INIT, this);
					getExtension().removeLifeCycleHandling(comp);
					reInitBinder(comp);
				}
			});
			//post ON_BIND_INIT event
			Events.postEvent(new Event(BinderImpl.ON_BIND_INIT, comp));
		}
	}

	private void reInitBinder(Component comp) {
		boolean recursive = reInitBinder0(comp);
		if (recursive && !(comp instanceof ShadowElement)) {
			for (final Component kid : comp.getChildren()) {
				if (kid != null) {
					reInitBinder(kid);
				}
			}
		}
	}

	/**
	 * @return true if need to continue tracking children
	 */
	private boolean reInitBinder0(Component comp) {
		//ZK-611 have wrong binding on a removed treecell in a template
		//if it was detached, ignore it
		if (comp.getPage() == null && !(comp instanceof ShadowElement)) {
			return false;
		}

		//ZK-4791
		Desktop desktop = comp.getDesktop();
		if (desktop != null) {
			String vmId = (String) comp.getAttribute(BindComposer.VM_ID);
			if (!Strings.isEmpty(vmId)) {
				Map<String, Binder> relationMap = (Map<String, Binder>) desktop.getAttribute(BinderCtrl.VIEWMODELID_BINDER_MAP_KEY);
				if (relationMap == null) {
					relationMap = new HashMap<>(4);
					desktop.setAttribute(BinderCtrl.VIEWMODELID_BINDER_MAP_KEY, relationMap);
				}
				relationMap.put(vmId, (Binder) comp.getAttribute((String) comp.getAttribute(BindComposer.BINDER_ID)));
			}
		}

		final Binder innerBinder = BinderUtil.getBinder(comp);
		if (innerBinder != null) { //it was already handled by innerBinder, ignore it
			return false;
		}

		//ZK-1640 command send 2 wrong ViewModel
		//check if there any parent binder again, don't use out-side parentBinder, it is not correct
		Binder binder = null;
		String bid = (String) comp.getAttribute(BindComposer.BINDER_ID);
		if (bid != null) {
			// comp is the binder owner.
			// fixed for B01887DetachAttach issue since 8.0.0 optimised some part of code.
			binder = (Binder) comp.getAttribute(bid);
		} else {
			binder = BinderUtil.getBinder(comp, true);
		}
		if (binder == null) {
			if (comp instanceof ShadowElement) {
				Component shadowHost = ((ShadowElement) comp).getShadowHost();
				if (shadowHost != null)
					binder = BinderUtil.getBinder(shadowHost, true);
			}
			if (binder == null)
				return true;
		}

		//ZK-1699 Performance issue ZK-Bind getters are called multiple times
		//check if it is handling, if yes then skip to evaluate it.
		if (BindUiLifeCycle.getExtension().isLifeCycleHandling(comp)) {
			return false;
		}

		if (binder instanceof AnnotateBinder) {
			new AnnotateBinderHelper(binder).initComponentBindings(comp);
		}

		//ZK-1699, mark the comp and it's children are handling.
		//note:mark handing before load, because of some load will change(create or reset) the children structure
		//(consider F00769.zul if you bind to tree open , it will load children in loadComponent)
		BindUiLifeCycle.getExtension().markLifeCycleHandling(comp);

		binder.loadComponent(comp, true);

		((BinderCtrl) binder).initQueue();
		((BinderCtrl) binder).initActivator();

		//[Dennis,20120925], this code was added when fixing issue zk-739,
		//but , inside binder.initComponentBindings, it shall do this already, I am not sure why.
		if (comp.hasAttribute(BinderImpl.VAR) || bid != null)
			BinderUtil.markHandling(comp, binder);
		return false;
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

				// Bug ZK-3045, we need to handle the detached component
				// to remove all its references in a tracker.
				if (comp.hasAttribute(BinderImpl.VAR)) {
					Object ref = comp.getAttribute((String) comp.getAttribute(BinderImpl.VAR));
					if (ref instanceof ReferenceBinding) {
						BinderUtil.markHandling(comp, ((ReferenceBinding) ref).getBinder());
					}
				}
				removeBindings(comp);
			}
		});
		//ZK-2022, make it is in queue of remove.
		comp.setAttribute(REMOVE_MARK, Boolean.TRUE);
		//ZK-2545 - Children binding support list model
		if (comp.removeAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS) != null) {
			ListModel<?> model = (ListModel<?>) comp.getAttribute(BinderCtrl.CHILDREN_BINDING_MODEL);
			ListDataListener listener = (ListDataListener) comp
					.removeAttribute(BinderCtrl.CHILDREN_BINDING_MODEL_LISTENER);
			if (model != null && listener != null) {
				model.removeListDataListener(listener);
			}
		}

		//F80: Speed up render, check component's subBinderAnnotation
		comp.setAttribute(SKIP_BIND_INIT, false);
		Events.postEvent(new Event(BinderImpl.ON_BIND_CLEAN, comp));
	}
	public void afterComponentMoved(Component parent, Component child, Component prevparent) {
		//do nothing
	}

	public void afterPageAttached(Page page, Desktop desktop) {
		//do nothing
	}

	public void afterPageDetached(Page page, Desktop prevdesktop) {
		final Collection<Component> comps = page.getRoots();
		for (final Iterator<Component> it = comps.iterator(); it.hasNext();) {
			final Component comp = it.next();
			removeBindings(comp);
		}
	}

	public void afterShadowAttached(ShadowElement shadow, Component host) {
		if (shadow instanceof Component) // just in case
			handleComponentAttached((Component) shadow);
	}

	public void afterShadowDetached(ShadowElement shadow, Component prevhost) {
		if (shadow instanceof Component) // just in case
			handleComponentDetached((Component) shadow);
	}

	private void removeBindings(Component comp) {
		//ZK-2224 batch remove component and it kids to enhance performance.
		Map<Binder, Set<Component>> batchRemove = new LinkedHashMap<Binder, Set<Component>>();
		removeBindingsRecursively(comp, batchRemove);
		for (Entry<Binder, Set<Component>> entry : batchRemove.entrySet()) {
			entry.getKey().removeBindings(entry.getValue());
		}
	}

	private void removeBindingsRecursively(Component comp, Map<Binder, Set<Component>> batchRemove) {
		removeBindings0(comp, batchRemove);
		for (final Iterator<Component> it = comp.getChildren().iterator(); it.hasNext();) {
			final Component kid = it.next();
			if (kid != null) {
				removeBindingsRecursively(kid, batchRemove); //recursive
			}
		}
		if (comp instanceof ComponentCtrl) {
			for (ShadowElement se : ((ComponentCtrl) comp).getShadowRoots()) {
				removeBindingsRecursively((Component) se, batchRemove); //recursive
			}
		}
	}

	private void removeBindings0(Component comp, Map<Binder, Set<Component>> batchRemove) {
		//A component with renderer; might need to remove $MODEL$
		final Object installed = comp.removeAttribute(BinderImpl.RENDERER_INSTALLED);
		if (installed != null) {
			BindELContext.removeModel(comp);
		}

		final Binder binder = BinderUtil.getBinder(comp, comp.hasAttribute(BinderCtrl.IS_TEMPLATE_MODEL_ENABLED_ATTR));
		if (binder != null) {
			if (batchRemove != null) {
				//ZK-2224 batch remove component and it kids to enhance performance.
				Set<Component> components = batchRemove.get(binder);
				if (components == null) {
					batchRemove.put(binder, components = new LinkedHashSet<Component>());
				}
				components.add(comp);
			} else {
				binder.removeBindings(comp);
			}
		}

		getExtension().removeLifeCycleHandling(comp);

		//ZK-4569
		Object vmIdBinderMap = Executions.getCurrent().getDesktop().getAttribute(BinderCtrl.VIEWMODELID_BINDER_MAP_KEY);
		if (vmIdBinderMap != null)
			((Map<String, Binder>) vmIdBinderMap).remove(comp.getAttribute(BindComposer.VM_ID));
	}

	private static Extension getExtension() {
		if (_ext == null) {
			synchronized (BindUiLifeCycle.class) {
				if (_ext == null) {
					String clsnm = Library.getProperty("org.zkoss.bind.tracker.impl.extension");
					if (clsnm != null) {
						try {
							_ext = (Extension) Classes.newInstanceByThread(clsnm);
						} catch (Throwable ex) {
							log.error("Unable to instantiate " + clsnm, ex);
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
		public void markLifeCycleHandling(Component comp) {
		}

		public boolean isLifeCycleHandling(Component comp) {
			return false;
		}

		public void removeLifeCycleHandling(Component comp) {
		}
	}
}
