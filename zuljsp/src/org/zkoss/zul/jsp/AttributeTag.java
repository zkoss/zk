/**
 * 
 */
package org.zkoss.zul.jsp;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.jsp.impl.AbstractTag;
import org.zkoss.zul.jsp.impl.BranchTag;

/**
 * @author ian
 *
 */
public class AttributeTag extends AbstractTag {
	
	
	private BranchTag _parent;
	private String _name;
	
	
	public void doTag() throws JspException, IOException {
		
		StringWriter out = new StringWriter();
		getJspBody().invoke(out);
		_parent.setDynamicAttribute(null, _name, out.toString());
	}

	//SimpleTagSupport//
	/** Sets the parent tag.
	 * Deriving class rarely need to invoke this method.
	 */
	public void setParent(JspTag parent) {
		super.setParent(parent);
		final AbstractTag pt =
		(AbstractTag)findAncestorWithClass(this, AbstractTag.class);
		if (pt instanceof BranchTag) {
			_parent = (BranchTag)pt;
		} else {
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		}
	}

	public void setName(String name) {
		this._name = name;
	}

}
