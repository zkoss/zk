/* Splitter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  8 14:54:05     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.html.impl.XulElement;

/**
 * An element which should appear before or after an element inside a box
 * ({@link Box}, {@link Vbox} and {@link Hbox}).
 *
 * <p>When the splitter is dragged, the sibling elements of the splitter are
 * resized. If {@link #getCollapse} is true, a grippy in placed
 * inside the splitter, and one sibling element of the splitter is collapsed
 * when the grippy is clicked.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Splitter extends XulElement {
	private String _collapse = "none";

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
			smartUpdate("zk_colps", collapse);
		}
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		if ("none".equals(_collapse)) return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		HTMLs.appendAttribute(sb, "zk_colps", _collapse);
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
}
