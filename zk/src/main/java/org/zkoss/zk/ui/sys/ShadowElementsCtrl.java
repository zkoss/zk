/** ShadowElementsCtrl.java.

	Purpose:
		
	Description:
		
	History:
		2:24:33 PM Oct 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ShadowElementCtrl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Template;

/**
 * Utilities for implementing components. (Internal use only)
 *
 * @author jumperchen
 * @since 8.0.0
 */
public class ShadowElementsCtrl {
	private static final ThreadLocal<Object> _shadowInfo = new ThreadLocal<Object>();
	private static final ThreadLocal<Object> _distributedIndexInfo = new ThreadLocal<Object>();

	/** Sets the current shadow element, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.HtmlShadowElement}.
	 * <p>Used only internally.
	 */
	public static final void setCurrentInfo(Object shadowInfo) {
		_shadowInfo.set(shadowInfo);
	}

	/** Returns the current shadow element, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.HtmlShadowElement}.
	 * <p>Used only internally.
	 */
	public static final Object getCurrentInfo() {
		return _shadowInfo.get();
	}

	/** Sets the current distributed index info, which is used only by
	 * {@link org.zkoss.zk.ui.HtmlShadowElement}.
	 * <p>Used only internally.
	 */
	public static final void setDistributedIndexInfo(Object indexMapInfo) {
		_distributedIndexInfo.set(indexMapInfo);
	}

	/** Returns the current distributed index info, which is used only by
	 * {@link org.zkoss.zk.ui.HtmlShadowElement}.
	 * <p>Used only internally.
	 */
	public static final Object getDistributedIndexInfo() {
		return _distributedIndexInfo.get();
	}

	/**
	 * Returns the component array filter out shadows if any. (Most often used
	 * for the component developer to invoke
	 * {@link Template#create(Component, Component, org.zkoss.xel.VariableResolver, org.zkoss.zk.ui.util.Composer)}
	 * by themselves and invoke this to filter out shadows.
	 */
	public static final Component[] filterOutShadows(Component[] shadows) {
		if (shadows == null || shadows.length == 0)
			return shadows;
		int length = shadows.length;

		// fixed ZK-3046, ZK-3871
		//to force init and load
		doBindChildrenInFilter(shadows);

		if (length == 1) {
			if (shadows[0] instanceof ShadowElement) {
				ShadowElement se = ((ShadowElement) shadows[0]);
				return se.getDistributedChildren().toArray(new Component[0]);
			}
		} else {
			Component parent = null;
			if (!(shadows[0] instanceof ShadowElementCtrl)) {
				parent = shadows[0].getParent();
			}

			if (parent instanceof ComponentCtrl) {
				ComponentCtrl pCtrl = (ComponentCtrl) parent;
				if (pCtrl.getShadowRoots().isEmpty())
					return shadows; // no shadow available here, we can skip the filter.
			}

			// the following code will filter the shadow element if any.
			// in multiple shadows case, some of nextInsertion would point to the useless component, which would detach
			// later. So here rewrite the list add method.
			LinkedList<Component> list = new LinkedList<Component>();
			for (Component comp : shadows) {
				if (comp instanceof HtmlShadowElement) {
					HtmlShadowElement hse = (HtmlShadowElement) comp;
					list.addAll(hse.getDistributedChildren());
				} else {
					list.add(comp);
				}
			}
			return list.toArray(new Component[0]);
		}
		return shadows;
	}

	private static void doBindChildrenInFilter(Component[] shadows) {
		for (Component shadow : shadows) {
			if (shadow instanceof ShadowElement) {
				ShadowElement se = (ShadowElement) shadow;
				if (!se.getDistributedChildren().isEmpty() || !((ShadowElementCtrl) se).isDynamicValue())
					continue;
				List<Component> children = shadow.getChildren();
				if (children.size() == 0) {
					Events.sendEvent(new Event("onBindInit", (Component) se));
					Events.sendEvent(new Event("onBindingReady", (Component) se));
					children = shadow.getChildren();
				}
				if (children.size() > 0) 
					doBindChildrenInFilter(children.toArray(new Component[children.size()]));
			}
		}
	}
}
