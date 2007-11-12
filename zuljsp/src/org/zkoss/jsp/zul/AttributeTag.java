/**
 * 
 */
package org.zkoss.jsp.zul;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import org.zkoss.jsp.zul.impl.AbstractTag;
import org.zkoss.jsp.zul.impl.BranchTag;

/**
 * Same meanings as zul attribute Tag.<br> 
 * it's parent must be a Component Tag.
 * @author ian
 */
public class AttributeTag extends AbstractTag {
	
	private BranchTag _parent;
	private String _name;
	
	/**
	 *  Add self contents to parent's dynamic attribute. 
	 */
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
//		final AbstractTag pt =
//		(AbstractTag)findAncestorWithClass(this, AbstractTag.class);
		if (parent instanceof BranchTag) {
			_parent = (BranchTag)parent;
		} else {
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		}
	}
	/**
	 * set attribute's key property 
	 * @param name
	 */
	public void setName(String name) {
		this._name = name;
	}

}
