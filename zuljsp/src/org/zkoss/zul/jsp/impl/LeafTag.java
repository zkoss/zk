/* LeafTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 17:07:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp.impl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;

import org.zkoss.zk.ui.Component;

/**
 * The skeletal class used to implement the JSP tag for ZK components
 * that don't accept any child.
 *
 * <p>Remember to declare the following in the TLD file:
 * &lt;body-content&gt;empty&lt;/body-content&gt;
 *
 * @author tomyeh
 */
abstract public class LeafTag extends AbstractTag {
	private Component _comp;
	private RootTag _roottag;
	private BranchTag _parenttag;

	/** Returns the page tag that this tag belongs to.
	 */
	public RootTag getRootTag() {
		return _roottag;
	}
	/** Returns the parent tag.
	 */
	public BranchTag getParentTag() {
		return _parenttag;
	}

	/** Returns the component associated with this tag.
	 */
	public Component getComponent() {
		return _comp;
	}

	//Deriving class must override//
	/** Creates a component that is associated with this tag,
	 * and returns the new component (never null).
	 * The deriving class must implement this method to create
	 * the proper component, initialize it and return it.
	 */
	abstract protected Component newComponent();

	//SimpleTagSupport//
	/** Sets the parent tag.
	 * Deriving class rarely need to invoke this method.
	 */
	public void setParent(JspTag parent) {
		super.setParent(parent);

		final AbstractTag pt =
		(AbstractTag)findAncestorWithClass(this, AbstractTag.class);
		if (pt instanceof RootTag) { //root component tag
			_roottag = (RootTag)pt;
		} else if (pt instanceof BranchTag) {
			_parenttag = (BranchTag)pt;
			_roottag = _parenttag.getRootTag();
		} else {
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		}

	}
	/** To process the leaf tag.
	 * The deriving class rarely need to override this method.
	 */
	public void doTag() throws JspException, IOException {
		if (!isEffective())
			return; //nothing to do

		initComponent(); //creates and registers the component
		writeComponentMark(); //write a special mark denoting the component
	}
	/** Creates and registers the component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void initComponent() throws JspException {
		_comp = newComponent();
		if (_comp == null)
			throw new JspTagException("newComponent() returns null");

		if (_parenttag != null) {
			_parenttag.addChildTag(this);
		} else {
			_roottag.addChildTag(this);
		}
	}
	/** Writes a special mark to the output to denote the location
	 * of this component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void writeComponentMark() throws IOException {
		Utils.writeComponentMark(getJspContext().getOut(), _comp);
	}
}
