/* Combobutton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed May 18 09:00:00     2011, Created by Benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.au.out.AuInvoke;

/**
 * A combo button. A combo button consists of a button {@link Combobutton} and
 * a popup window {@link Popup}.
 * It is similar to {@link Bandbox} except the input box is changed
 * to button.
 *
 * <p>Default {@link #getZclass}: z-combobutton.(since 6.0.0)
 *
 * <p>Events: onClick<br/>
 * Developers can listen to the onClick event.
 *
 * <p>Events: onOpen<br/>
 * Developers can listen to the onOpen event and initializes it
 * when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or
 * clean up if false.
 *
 * <p>Note: to have better performance, onOpen is sent only if
 * a non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * @since 6.0.0
 * @author benbai
 */
public class Combobutton extends Button {
	private boolean _autodrop, _open;

	static {
		addClientEvent(Combobutton.class, Events.ON_CLICK, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
		addClientEvent(Combobutton.class, Events.ON_OPEN, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
	}

	public Combobutton() {
	}
	public Combobutton(String value) throws WrongValueException {
		this();
		setLabel(value);
	}
	
	/** Returns the dropdown window belonging to this combo button.
	 */
	public Popup getDropdown() {
		return (Popup)getFirstChild();
	}
	
	/** Returns whether to automatically drop the popup if user hovers on this 
	 * Combobutton.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}
	
	/** Sets whether to automatically drop the popup if user hovers on this 
	 * Combobutton.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("autodrop", autodrop);
		}
	}

	/** Drops down or closes the child, 
	 * only works while visible.
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (open != _open) {
			_open = open;
			if (isVisible()) {
				if (open) open();
				else close();
			}
		}
	}
	/** Returns whether this combobutton is open.
	 *
	 * <p>Default: false.
	 * @since 6.0.0
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Drops down the child.
	 * The same as setOpen(true).
	 */
	public void open() {
		response("open", new AuInvoke(this, "setOpen", true)); //don't use smartUpdate
	}
	/** Closes the child if it was dropped down.
	 * The same as setOpen(false).
	 */
	public void close() {
		response("open", new AuInvoke(this, "setOpen", false)); //don't use smartUpdate
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-combobutton" : _zclass;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "autodrop", _autodrop);
	}
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CLICK)) {
			Events.postEvent(MouseEvent.getMouseEvent(request));
		} else if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	
	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (!(newChild instanceof Popup))
			throw new UiException("Unsupported child for Combobutton: "+newChild);
		if (getFirstChild() != null)
			throw new UiException("At most one popup is allowed, "+this);
		super.beforeChildAdded(newChild, refChild);
	}
	protected boolean isChildable() {
		return true;
	}

	//Override//
	/** 
	 * Combobutton does not support file upload
	 */
	public void setUpload(String upload) {
		throw new UnsupportedOperationException("Combobutton do not support file upload.");
	}
	
}
