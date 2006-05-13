/* Groupbox.java

{{IS_NOTE
	$Id: Groupbox.java,v 1.8 2006/05/08 02:36:40 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 16:55:24     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.au.AuScript;
import com.potix.zk.ui.ext.Openable;

import com.potix.zul.html.impl.XulElement;

/**
 * Groups a set of child elements to have a visual effect.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/08 02:36:40 $
 */
public class Groupbox extends XulElement implements Openable {
	private Caption _caption;
	private boolean _open = true, _closable = true;

	/** Returns the caption of this groupbox.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns whether this groupbox is open.
	 *
	 * <p>Note: the default mold ({@link #getMold}) doesn't support
	 * the open attribute.
	 *
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Sets whether this groupbox is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			response("open",
				new AuScript(this, "zkGrbox.open('"+getUuid()+"',"+_open+')'));
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
			smartUpdate("zk_closable", closable);
		}
	}

	//-- Openable --//
	public void setOpenByClient(boolean open) {
		_open = open;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final boolean onOpen = isAsapRequired("onOpen");
		if (!onOpen && _closable) return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		if (onOpen)
			sb.append(" zk_onOpen=\"true\"");
		if (!_closable)
			sb.append(" zk_closable=\"false\"");
		return sb.toString();
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
			if (!getChildren().isEmpty())
				insertBefore = (Component)getChildren().get(0);
				//always makes caption as the first child
			_caption = (Caption)child;
			invalidate(INNER);
		} else if (insertBefore instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate(INNER);
		}
		super.onChildRemoved(child);
	}
}
