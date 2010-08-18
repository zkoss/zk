/* Menu.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:57:58     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 * 
 * <p>Default {@link #getZclass}: z-mean. (since 3.5.0)
 * @author tomyeh
 */
public class Menu extends LabelImageElement implements org.zkoss.zul.api.Menu {
	private Menupopup _popup;
	private String _content = "";
	private boolean _noSmartUpdate = false;
	
	static {
		addClientEvent(Menu.class, Events.ON_CLICK, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
		addClientEvent(Menu.class, Events.ON_CHANGE, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
	}
	
	public Menu() {
	}
	public Menu(String label) {
		super(label);
	}
	public Menu(String label, String src) {
		super(label, src);
	}

	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 */
	public boolean isTopmost() {
		return !(getParent() instanceof Menupopup);
	}

	/** Returns the {@link Menupopup} it owns, or null if not available.
	 */
	public Menupopup getMenupopup() {
		return _popup;
	}
	/** Returns the {@link Menupopup} it owns, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Menupopup getMenupopupApi() {
		return getMenupopup();		
	}
	
	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: empty ("").
	 *
	 * @since 5.0.0
	 */
	public String getContent() {
		return _content;
	}
	
	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 * 
	 * <p>There is a way to create Colorbox automatically by using
	 * #color=#RRGGBB, usage example <code>setContent("#color=FFFFFF")</code>
	 *
	 * @since 5.0.0
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			if (!_noSmartUpdate)
				smartUpdate("content", content);
		}
	}
	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-menu" : _zclass;
	}

	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		render(renderer, "content", _content);
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Menubar)
		&& !(parent instanceof Menupopup))
			throw new UiException("Unsupported parent for menu: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Menupopup) {
			if (_popup != null && _popup != child)
				throw new UiException("Only one menupopup is allowed: "+this);
		} else {
			throw new UiException("Unsupported child for menu: "+child);
		}
		super.beforeChildAdded(child, refChild);
	}
	
	public void onChildRemoved(Component child) {
		_popup = null;
		super.onChildRemoved(child);
	}
	
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Menupopup) {
			if (super.insertBefore(child, refChild)) {
				_popup = (Menupopup)child;
				return true;
			}
		} else {
			return super.insertBefore(child, refChild);
				//impossible but make it more extensible
		}
		return false;
	}

	//Cloneable//
	public Object clone() {
		final Menu clone = (Menu)super.clone();
		if (clone._popup != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		_popup = (Menupopup)getFirstChild();
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (!getChildren().isEmpty()) afterUnmarshal();
	}

	//--ComponentCtrl--//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onClick.
	 * @since 5.0.0
	 */
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CLICK)) {
			Events.postEvent(MouseEvent.getMouseEvent(request));
		} else if (cmd.equals(Events.ON_CHANGE)) {
			final Map data = request.getData();
			if (getContent().indexOf("#color") == 0) {
				_noSmartUpdate = true;
				setContent("#color=" + (String)data.get("color"));
				_noSmartUpdate = false;
				Events.postEvent(InputEvent.getInputEvent(request, _content));
			}
		} else
			super.service(request, everError);
	}
}