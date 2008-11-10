/* AbstractComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui.api;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentCtrl;

public interface AbstractComponent extends Component, ComponentCtrl,
		java.io.Serializable {
	/**
	 * A special smart-update that update a value in int.
	 * <p>
	 * It will invoke {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate(String,String)} to update the
	 * attribute eventually.
	 */
	public void smartUpdate(String attr, int value);

	/**
	 * A special smart-update that update a value in boolean.
	 * <p>
	 * It will invoke {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate(String,String)} to update the
	 * attribute eventually.
	 */
	public void smartUpdate(String attr, boolean value);

	/**
	 * Default: null (no propagation at all).
	 */
	public Component getPropagatee(String evtnm);

	// -- Object --//
	public String toString();

}
