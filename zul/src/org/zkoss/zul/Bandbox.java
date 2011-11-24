/* Bandbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:14:46     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.html.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zul.impl.Utils;

/**
 * A band box. A bank box consists of an input box ({@link Textbox} and
 * a popup window {@link Bandpopup}.
 * It is similar to {@link Combobox} except the popup window could have
 * any kind of children. For example, you could place a textbox in
 * the popup to let user search particular items.
 *
 * <p>Default {@link #getZclass}: z-bandbox.(since 3.5.0)
 *
 * <p>Events: onOpen<br/>
 * Developers can listen to the onOpen event and initializes it
 * when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or
 * clean up if false.
 *
 * <p>Note: to have better performance, onOpen is sent only if
 * a non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 *
 * @author tomyeh
 */
public class Bandbox extends Textbox {
	private boolean _autodrop, _btnVisible = true, _open;

	static {
		addClientEvent(Bandbox.class, Events.ON_OPEN, CE_DUPLICATE_IGNORE);
	}

	public Bandbox() {
	}
	public Bandbox(String value) throws WrongValueException {
		this();
		setValue(value);
	}
	
	/** Returns the dropdown window belonging to this band box.
	 */
	public Bandpopup getDropdown() {
		return (Bandpopup)getFirstChild();
	}

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}
	/** Sets whether to automatically drop the list if users is changing
	 * this text box.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("autodrop", autodrop);
		}
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}
	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("buttonVisible", visible);
		}
	}

	/** Returns whether this bandbox is open.
	 *
	 * <p>Default: false.
	 * @since 6.0.0
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Drops down or closes the child.
	 *
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			if (open) open();
			else close();
		}
	}
	/** Drops down the child.
	 * The same as setOpen(true).
	 *
	 * @since 3.0.1
	 */
	public void open() {
		response("open", new AuInvoke(this, "setOpen", true)); //don't use smartUpdate
	}
	/** Closes the child if it was dropped down.
	 * The same as setOpen(false).
	 *
	 * @since 3.0.1
	 */
	public void close() {
		response("open", new AuInvoke(this, "setOpen", false)); //don't use smartUpdate
	}

	/**
	 * Bandbox can't be enabled the multiline functionality.
	 */
	public void setMultiline(boolean multiline) {
		if (multiline)
			throw new UnsupportedOperationException("Bandbox doesn't support multiline");
	}

	/**
	 * Bandbox can't be enabled the rows functionality.
	 */
	public void setRows(int rows) {
		if (rows != 1)
			throw new UnsupportedOperationException("Bandbox doesn't support multiple rows, "+rows);
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-bandbox" : _zclass;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "autodrop", _autodrop);
		if (!_btnVisible)
			renderer.render("buttonVisible", false);
	}
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link Textbox#service},
	 * it also handles onOpen and onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (!(newChild instanceof Bandpopup))
			throw new UiException("Unsupported child for Bandbox: "+newChild);
		if (getFirstChild() != null)
			throw new UiException("At most one bandpopup is allowed, "+this);
		super.beforeChildAdded(newChild, refChild);
	}
	/** Childable. */
	protected boolean isChildable() {
		return true;
	}
}
