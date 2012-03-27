/* Groupbox.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 16:55:24     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.XulElement;

/**
 * Groups a set of child elements to have a visual effect.
 * <p>Default {@link #getZclass}: "z-groupbox". If {@link #getMold()} is 3d,
 * "z-groupbox-3d" is assumed.(since 3.5.0)
 *
 * <p>Events: onOpen.
 *
 * @author tomyeh
 */
public class Groupbox extends XulElement {
	private transient Caption _caption;
	/** The style used for the content block. */
	private String _cntStyle;
	/** The style class used for the content block. */
	private String _cntSclass;
	private String _title = "";
	private boolean _open = true, _closable = true;

	static {
		addClientEvent(Groupbox.class, Events.ON_OPEN, CE_IMPORTANT);
	}

	/** Returns the caption of this groupbox.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns whether this groupbox is open.
	 *
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Opens or closes this groupbox.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("open", _open);
		}
	}

	/** Returns whether user can open or close the group box.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking on the title).
	 *
	 * <p>Default: true.
	 */
	public boolean isClosable() {
		return _closable;
	}
	/** Sets whether user can open or close the group box.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("closable", closable);
		}
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "contentStyle", _cntStyle);
		render(renderer, "contentSclass", _cntSclass);
		render(renderer, "title", _title);
		if (!_open) renderer.render("open", false);
		if (!_closable) renderer.render("closable", false);
	}
	public String getZclass() {
		return _zclass == null ? "default".equals(getMold()) ? "z-groupbox" : "z-groupbox-3d" : _zclass;
	}
	
	/** Returns the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 */
	public String getContentStyle() {
		return _cntStyle;
	}
	/** Sets the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 *
	 * <p>Default: null.
	 */
	public void setContentStyle(String style) {
		if (!Objects.equals(_cntStyle, style)) {
			_cntStyle = style;
			smartUpdate("contentStyle", _cntStyle);
		}
	}
	/** Returns the style class used for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 */
	public String getContentSclass() {
		return _cntSclass;
	}
	/** Sets the style class used for the content block.
	 *
	 * @see #getContentSclass
	 * @since 3.0.0
	 */
	public void setContentSclass(String scls) {
		if (!Objects.equals(_cntSclass, scls)) {
			_cntSclass = scls;
			smartUpdate("contentSclass", _cntSclass);
		}
	}
	/** Returns the title.
	 * Besides this attribute, you could use {@link Caption} to define
	 * a more sophisticated caption (aka., title).
	 * <p> It will be displayed before caption.
	 * <p>Default: empty.
	 * @since 6.0.0
	 */
	public String getTitle() {
		return _title;
	}
	/** Sets the title.
	 * @since 6.0.0
	 */
	public void setTitle(String title) {
		if (title == null)
			title = "";
		if (!Objects.equals(_title, title)) {
			_title = title;
			smartUpdate("title", title);
		}
	}

	/** @deprecated As of release 6.0, legend no longer used in groupbox.
	 */
	public boolean isLegend() {
		return "default".equals(getMold());
	}
	/** @deprecated As of release 6.0, legend no longer used in groupbox.
	 */
	public void setLegend(boolean legend) {
		if (legend && "3d".equals(getMold()))
			setMold("default");
		else if (!legend && "default".equals(getMold()))
			setMold("3d");
	}

	//-- Component --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
		} else if (refChild instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		super.beforeChildAdded(child, refChild);
	}
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Caption) {
			refChild = getFirstChild();
				//always makes caption as the first child
			if (super.insertBefore(child, refChild)) {
				_caption = (Caption)child;
				return true;
			}
		} else {
			return super.insertBefore(child, refChild);
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption)
			_caption = null;
		super.onChildRemoved(child);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
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

	//Cloneable//
	public Object clone() {
		final Groupbox clone = (Groupbox)super.clone();

		if (_caption != null) clone.afterUnmarshal();
		return clone;
	}
	/** @param cnt # of children that need special handling (used for optimization).
	 * -1 means process all of them
	 */
	private void afterUnmarshal() {
		for (Component child : getChildren())
			if (child instanceof Caption) {
				_caption = (Caption) child;
				break; //done
			}
	}

	//Serializable//
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
	}
}
