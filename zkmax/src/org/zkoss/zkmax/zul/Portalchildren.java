/* Portalchildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2008 4:19:52 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

/**
 * The column of Portallayout. <br> 
 * Child of Portalchildren can only be Panel.
 * 
 * <p>Default {@link #getZclass}: z-portal-children.
 * @author jumperchen
 * @since 3.5.0
 */
public class Portalchildren extends XulElement implements org.zkoss.zkmax.zul.api.Portalchildren {
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;
	
	public Portalchildren() {
	}

	public String getZclass() {
		return _zclass == null ? "z-portal-children" : _zclass;
	}
	
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Portallayout))
			throw new UiException("Wrong parent: " + parent);
		super.setParent(parent);
	}
	
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Panel))
			throw new UiException("Unsupported child for Portalchildren: "
					+ child);
		return super.insertBefore(child, insertBefore);
	}
	
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		final Portallayout layout = (Portallayout)getParent();
		if (!_noSmartUpdate && layout != null) {
			layout.smartUpdate("z.reset", true);
		}
	}
	
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		final Portallayout layout = (Portallayout)getParent();
		if (!_noSmartUpdate && layout != null) {
			layout.smartUpdate("z.reset", true);
		}
	}
	
	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Updatable {
		
		public void setResult(Object result) {
			_noSmartUpdate = ((Boolean) result).booleanValue();
		}
	}
}
