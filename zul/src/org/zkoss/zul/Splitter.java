/* Splitter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  8 14:54:05     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zul.impl.XulElement;

/**
 * An element which should appear before or after an element inside a box
 * ({@link Box}, {@link Vbox} and {@link Hbox}).
 *
 * <p>When the splitter is dragged, the sibling elements of the splitter are
 * resized. If {@link #getCollapse} is true, a grippy in placed
 * inside the splitter, and one sibling element of the splitter is collapsed
 * when the grippy is clicked.
 *
 * <p>Events: onOpen
 *
 *  <p>Default {@link #getZclass} as follows: (since 3.5.0)
 *  <ol>
 *  	<li>Case 1: If {@link #getOrient()} is vertical, "z-splitter-ver" is assumed</li>
 *  	<li>Case 2: If {@link #getOrient()} is horizontal, "z-splitter-hor" is assumed</li>
 *  </ol>
 * 
 * @author tomyeh
 */
public class Splitter extends XulElement implements org.zkoss.zul.api.Splitter {
	private String _collapse = "none";
	private boolean _open = true;

	public Splitter() {
	}	
	
	/** Returns the orientation of the splitter.
	 * It is the same as the parent's orientation ({@link Box#getOrient}.
	 */
	public String getOrient() {
		final Box box = (Box)getParent();
		return box != null ? box.getOrient(): "vertical";
	}

	/** Returns which side of the splitter is collapsed when its grippy
	 * is clicked. If this attribute is not specified, the splitter will
	 * not cause a collapse.
	 * If it is collapsed, {@link #isOpen} returns false.
	 *
	 * <p>Default: none.
	 *
	 * <p>The returned value can be one ofthe following.
	 *
	 * <dl>
	 * <dt>none</dt>
	 * <dd>No collpasing occurs.</dd>
	 * <dt>before</dt>
	 * <dd>When the grippy is clicked, the element immediately
	 * before the splitter in the same parent is collapsed so that
	 * its width or height is 0.</dd>
	 * <dt>after</dt>
	 * <dd>When the grippy is clicked, the element immediately
	 * after the splitter in the same parent is collapsed so that
	 * its width or height is 0.</dd>
	 * </dl>
	 *
	 * <p>Unlike XUL, you don't have to put a so-called grippy component
	 * as a child of the spiltter.
	 */
	public String getCollapse() {
		return _collapse;
	}
	/** Sets which side of the splitter is collapsed when its grippy
	 * is clicked. If this attribute is not specified, the splitter will
	 * not cause a collapse.
	 *
	 * @param collapse one of none, before and after.
	 * If null or empty is specified, none is assumed.
	 * @see #getCollapse
	 */
	public void setCollapse(String collapse) throws WrongValueException {
		if (collapse == null || collapse.length() == 0)
			collapse = "none";
		else if (!"none".equals(collapse) && !"before".equals(collapse)
		&& !"after".equals(collapse))
			throw new WrongValueException("Unknown collpase: "+collapse);

		if (!Objects.equals(_collapse, collapse)) {
			_collapse = collapse;
			smartUpdate("z.colps", collapse);
		}
	}

	/** Returns whether it is opne (i.e., not collapsed.
	 * Meaningful only if {@link #getCollapse} is not "none".
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Opens or collapses the splitter.
	 * Meaningful only if {@link #getCollapse} is not "none".
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("z.open", open);
		}
	}

	//super//
	public String getZclass() {
		return _zclass == null ? "z-splitter" +
				("vertical".equals(getOrient()) ? "-ver" : "-hor") : _zclass;
	}
	
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_OPEN);

		if ("vertical".equals(getOrient()))
			HTMLs.appendAttribute(sb, "z.vert", "true");
		
		if (!"none".equals(_collapse))
			HTMLs.appendAttribute(sb, "z.colps", _collapse);
		if (!_open) sb.append(" z.open=\"false\"");
		return sb.toString();
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Box))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	/** Not allow any children.
	 */
	public boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Openable {
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;
		}
	}
}
