/* Drawer.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 30 11:19:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.XulElement;

/**
 * A drawer.
 * <p>Only support browsers that support Flex and CSS Transitions. (IE10+, Edge, Chrome, Firefox, Safari)
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class Drawer extends XulElement {
	static {
		addClientEvent(Drawer.class, Events.ON_OPEN, CE_IMPORTANT | CE_DUPLICATE_IGNORE);
	}

	private String _title = null;
	private String _position = "right";
	private boolean _mask = true;
	private boolean _closable = false;
	private boolean _autodrop = false;

	public Drawer() {
		setVisibleDirectly(false);
	}

	/**
	 * Returns the title of this drawer. {@code null} means no title.
	 * <p>Default: null.
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Sets the title of this drawer. {@code null} means no title.
	 * @param title the title.
	 */
	public void setTitle(String title) {
		if (!Objects.equals(_title, title)) {
			_title = title;
			smartUpdate("title", _title);
		}
	}

	/**
	 * Returns the position of this drawer. Valid values are "left", "right", "top" and "bottom".
	 * <p>Default: right.
	 */
	public String getPosition() {
		return _position;
	}

	/**
	 * Sets the position of this drawer. Valid values are "left", "right", "top" and "bottom".
	 * @param position the position of this drawer.
	 * @throws WrongValueException if value is not valid.
	 */
	public void setPosition(String position) throws WrongValueException {
		if ("left".equals(position) || "right".equals(position) || "top".equals(position) || "bottom".equals(position)) {
			if (!Objects.equals(_position, position)) {
				_position = position;
				smartUpdate("position", _position);
			}
		} else {
			throw new WrongValueException(
					"position cannot be " + position
					+ ". Should be \"left\", \"right\", \"top\" or \"bottom\".");
		}
	}

	/**
	 * Returns whether it is masked when opened.
	 * <p>Default: true.
	 */
	public boolean isMask() {
		return _mask;
	}

	/**
	 * Sets whether it is masked when opened.
	 * @param mask mask enabled?
	 */
	public void setMask(boolean mask) {
		if (_mask != mask) {
			_mask = mask;
			smartUpdate("mask", _mask);
		}
	}

	/**
	 * Returns whether it is closeable by user (a button).
	 * <p>Default: false.
	 */
	public boolean isClosable() {
		return _closable;
	}

	/**
	 * Sets whether it is closeable by user (a button).
	 * @param closable closable enabled?
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("closable", _closable);
		}
	}

	/**
	 * Returns whether it is opened automatically when the mouse cursor is near the page edge.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}

	/**
	 * Sets whether it is opened automatically when the mouse cursor is near the page edge.
	 * @param autodrop autodrop enabled?
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("autodrop", _autodrop);
		}
	}

	@Override
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);

		render(renderer, "title", _title);
		render(renderer, "closable", _closable);
		render(renderer, "autodrop", _autodrop);
		if (!"right".equals(_position))
			render(renderer, "position", _position);
		if (!_mask)
			renderer.render("mask", false);
	}

	@Override
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (Events.ON_OPEN.equals(cmd)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			setVisibleDirectly(evt.isOpen());
			Events.postEvent(evt);
		} else super.service(request, everError);
	}

	@Override
	public String getZclass() {
		return this._zclass != null ? this._zclass : "z-drawer";
	}

	@Override
	public void setHflex(String flex) {
		throw new UnsupportedOperationException("not supported in drawer");
	}

	@Override
	public void setVflex(String flex) {
		throw new UnsupportedOperationException("not supported in drawer");
	}

	/**
	 * Opens the drawer.
	 */
	public void open() {
		setVisible(true);
	}

	/**
	 * Closes the drawer.
	 */
	public void close() {
		setVisible(false);
	}
}