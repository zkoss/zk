/** ShadowElementsCtrl.java.

	Purpose:
		
	Description:
		
	History:
		2:24:33 PM Oct 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.util.LinkedList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ShadowElementCtrl;
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
	 * Returns the component array in range from the give components or shadows,
	 * if possible. (Most often used for the component developer to invoke
	 * {@link Template#create(Component, Component, org.zkoss.xel.VariableResolver, org.zkoss.zk.ui.util.Composer)}
	 * by themselves.
	 */
	public static final Component[] convertToComponents(Component[] shadows) {
		if (shadows == null || shadows.length == 0)
			return shadows;
		int length = shadows.length;
		if (length == 1) {
			if (shadows[0] instanceof ShadowElement) {
				ShadowElement se = ((ShadowElement) shadows[0]);
				return se.getDistributedChildren().toArray(new Component[0]);
			}
		} else {
			Component start = null;
			if (shadows[0] instanceof ShadowElementCtrl) {
				ShadowElementCtrl se = ((ShadowElementCtrl) shadows[0]);
				start = se.getFirstInsertion();
			} else {
				start = shadows[0];
			}

			Component end = null;
			if (shadows[0] instanceof ShadowElementCtrl) {
				ShadowElementCtrl se = ((ShadowElementCtrl) shadows[length - 1]);
				start = se.getLastInsertion();
			} else {
				end = shadows[length - 1];
			}
			LinkedList<Component> list = new LinkedList<Component>();
			while (start != null) {
				list.add(start);
				if (start == end)
					break;
				start = start.getNextSibling();
			}
			return list.toArray(new Component[0]);
		}
		return shadows;
	}
}
