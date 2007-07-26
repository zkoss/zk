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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspTag;
import org.zkoss.lang.reflect.Fields;

import org.zkoss.util.ModificationException;
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
abstract public class LeafTag extends AbstractTag implements DynamicAttributes {
	private Component _comp;
	private RootTag _roottag;
	private BranchTag _parenttag;
	private Map _attrMap = new LinkedHashMap();

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
	/**
	 * Creates a component that is associated with this tag,
	 * and returns the new component (never null).
	 * The deriving class must implement this method to create
	 * the proper component, initialize it and return it.
	 * @param use the use component  
	 * @return A zul Component 
	 */
	abstract protected Component newComponent(Component use);

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
		if (!isEffective())return; //nothing to do
		
		initComponent(); //creates and registers the component
		writeComponentMark(); //write a special mark denoting the component
	}
	
	/** Creates and registers the component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void initComponent() throws JspException {
		try {//TODO: use-class initial works...
			String use = null;
			_comp = newComponent(((use = (String) _attrMap.remove("use"))!=null)? 
					(Component)( Class.forName(use).newInstance()) : null);
		} catch (Exception e) {
			throw new JspException(e);
		}
		
		if (_comp == null)
			throw new JspTagException("newComponent() returns null");

		try {
			invokeSetterMethods(_comp);
		} catch (Exception e) {
			throw new JspException(e);
		}
		
		if (_parenttag != null)_parenttag.addChildTag(this);
		else _roottag.addChildTag(this);
	}
	
	/**
	 * Do Methods invokation automatically.
	 * @param target
	 * @throws NoSuchMethodException 
	 * @throws ModificationException 
	 */
	private void invokeSetterMethods(final Component target) 
	throws ModificationException, NoSuchMethodException{
		for(Iterator itor = _attrMap.entrySet().iterator();itor.hasNext();)
		{
			Map.Entry entry= (Entry) itor.next();
			Fields.setField(target, (String)entry.getKey(),entry.getValue(), true);
		}
	}
	/**
	 * 
	 * @param uri
	 * @param localName
	 * @param value
	 * @throws JspException
	 */
	public void setDynamicAttribute(String uri, String localName, Object value) 
	throws JspException {
		if("if".equals(localName)||"unless".equals(localName))
			System.out.println(this.getClass().getName()+" : if & unless is triggerd!!!");
		if ("if".equals(localName)) 
			this.setIf(((Boolean)value).booleanValue());// TODO: change to throw exception...
		else if ("unless".equals(localName)) 
			this.setUnless(((Boolean)value).booleanValue());// TODO: change to throw exception...
		else _attrMap.put(localName, value);
	}
	
	
	/** Writes a special mark to the output to denote the location
	 * of this component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void writeComponentMark() throws IOException {
		Utils.writeComponentMark(getJspContext().getOut(), _comp);
	}
}
