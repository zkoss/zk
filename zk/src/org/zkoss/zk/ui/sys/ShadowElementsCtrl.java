/** ShadowElementsCtrl.java.

	Purpose:
		
	Description:
		
	History:
		2:24:33 PM Oct 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

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
}
