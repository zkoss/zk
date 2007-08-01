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

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;

import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Radiogroup;
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
	private Map _eventListenerMap = new LinkedHashMap();
    private String _use;

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
	 *
	 * @param use the use component  
	 * @return A zul Component
	 * @throws Exception
	 */
	abstract protected Component newComponent(Class use)throws Exception;

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
		afterComposeComponent();//finish compose the component
		writeComponentMark(); //write a special mark denoting the component
		
	}
	
	/** Creates and registers the component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void initComponent() throws JspException {
		try {//TODO: use-class initial works...
			_comp = newComponent(_use!=null ? Classes.forNameByThread(_use) : null);
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
			throw new JspException("if, unless, use is static method!!!");
		if(localName.startsWith("on"))
			_eventListenerMap.put(localName, value);
		else _attrMap.put(localName, value);
	}
	
	
	/** Writes a special mark to the output to denote the location
	 * of this component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void writeComponentMark() throws IOException {
		Utils.writeComponentMark(getJspContext().getOut(), _comp);
	}
	
	/** after children creation do dynamic attributes setter work and registers event handler.
	 * Called by {@link #doTag}.
	 * @throws JspException 
	 */
	/*package*/void afterComposeComponent() throws JspException{
			if (_comp instanceof AfterCompose)
			((AfterCompose)_comp).afterCompose();
	
		if (Events.isListened(_comp, Events.ON_CREATE, false))
			Events.postEvent(
				new CreateEvent(Events.ON_CREATE, _comp, Executions.getCurrent().getArg()));
		
		if (_comp == null)
			throw new JspTagException("newComponent() returns null");
	
		try {
			invokeSetterMethods(_comp);
		} catch (Exception e) {
			throw new JspException(e);
		}
		//add event handle ...
		
		for(Iterator itor = _eventListenerMap.entrySet().iterator();itor.hasNext();)
		{
			Map.Entry entry = (Map.Entry)itor.next();
			final ZScript zscript = ZScript.parseContent((String)entry.getValue(), null);
			((ComponentCtrl)_comp).addEventHandler(
					(String)entry.getKey(), new EventHandler(zscript,null));
		}
		
	}
    /**
     * get use class full name.
     * @return customized component class name
     */
    public String getUse()
    {
        return _use;
    }
    /**
     * set used class's full name.
     * @param use customized component class name
     */
    public void setUse(String use)
    {
        this._use = use;
    }
}
