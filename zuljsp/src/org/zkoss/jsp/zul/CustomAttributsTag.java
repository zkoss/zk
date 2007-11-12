/**
 * 
 */
package org.zkoss.jsp.zul;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspTag;

import org.zkoss.jsp.zul.impl.AbstractTag;
import org.zkoss.jsp.zul.impl.BranchTag;
import org.zkoss.zk.ui.Component;

/**
 *  * Same meanings as zul <code>&slt;custom-attributes></code> Tag.<br> 
 * it's parent must be a Component Tag.
 * @author ian
 *
 */
public class CustomAttributsTag extends AbstractTag  implements DynamicAttributes  {

	private BranchTag _parent;
	private String _name;
	private HashMap _customMap = new HashMap();
	/**
	 *  Add self contents to parent's dynamic attribute. 
	 */
	public void doTag() throws JspException, IOException {
		
//		StringWriter out = new StringWriter();
//		getJspBody().invoke(out);
		//_parent.setDynamicAttribute(null, _name, out.toString());
		Component comp = _parent.getComponent();
		comp.getAttributes().putAll(_customMap);
	}
	//SimpleTagSupport//
	/** Sets the parent tag.
	 * Deriving class rarely need to invoke this method.
	 */
	public void setParent(JspTag parent) {
		super.setParent(parent);
		if (parent instanceof BranchTag) {
			_parent = (BranchTag)parent;
		} else {
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		}
	}
	/*
	 * current version ignor uri but if one day need to support mutiple Component set then we shall reopen it.
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute
	 * (java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String localName, Object value) 
	throws JspException {
		_customMap.put(localName, value);
	}
	
	
}//end of class...
