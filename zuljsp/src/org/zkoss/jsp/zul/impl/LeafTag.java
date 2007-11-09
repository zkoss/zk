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
package org.zkoss.jsp.zul.impl;

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
import org.zkoss.zk.ui.metainfo.impl.AnnotationHelper;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

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
    private String _use, _forward;

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
	
	/** 
	 * Creates and registers the component.
	 * Called by {@link #doTag}.
	 */
	/*package*/ void initComponent() throws JspException {
		if(_roottag==null)
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		try {//TODO: use-class initial works...
			_comp = newComponent(_use!=null ? Classes.forNameByThread(_use) : null);
		} catch (Exception e) {
			throw new JspException(e);
		}
		
		if (_parenttag != null)_parenttag.addChildTag(this);
		else _roottag.addChildTag(this);
		_comp.getDefinition().applyProperties(_comp);
	}
	
	/**
	 * Test if the attributes are annotation or component attributes.<br>
	 * If(is Component attributes)Invokes setter methods to update all 
	 * assigned attributes.
	 * If(is annotations)
	 * @param target the target component
	 * @throws NoSuchMethodException 
	 * @throws ModificationException 
	 */
	private void evaluateDynaAttributes(final Component target) 
	throws ModificationException, NoSuchMethodException{
		AnnotationHelper helper = null;
		for(Iterator itor = _attrMap.entrySet().iterator();itor.hasNext();){
			//TODO: add annotation judgment...
			Map.Entry entry= (Entry) itor.next();
			String attnm = (String)entry.getKey();
			String attval = (String)entry.getValue();
			final int len = attval.length();
			
			// test if this attribute is an annotation...no need to detect if attnm==use,
			if (len >= 3 && attval.charAt(0) == '@' && 
					attval.charAt(1) == '{' && 
					attval.charAt(len-1) == '}') { //annotation
				
				if(helper == null) helper = new AnnotationHelper();
				helper.addByCompoundValue(attval.substring(2, len -1));
				helper.applyAnnotations(_comp, 
						"self".equals(attnm) ? null: attnm, true);
			}
			else Fields.setField(target, attnm, entry.getValue(), true);
		}
	}
	/**
	 *   Called when a tag declared to accept dynamic attributes is passed an 
	 *   attribute that is not declared in the Tag Library Descriptor.<br>
	 *   
	 * @param uri the namespace of the attribute, always null currently.
	 * @param localName the name of the attribute being set.
	 * @param value  the value of the attribute
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
			if (_comp instanceof AfterCompose)//safty check...
				((AfterCompose)_comp).afterCompose();
	
		if (Events.isListened(_comp, Events.ON_CREATE, false))//send onCreate event...
			Events.postEvent(
				new CreateEvent(Events.ON_CREATE, _comp, Executions.getCurrent().getArg()));
		
		if (_comp == null)
			throw new JspTagException("newComponent() returns null");
	
		try {
			evaluateDynaAttributes(_comp);
		} catch (ModificationException e) {
			throw new JspException(e);
		} catch (NoSuchMethodException e) {
			throw new JspException(e);
		}

		//process the forward condition
		ComponentsCtrl.applyForward(_comp, _forward);

		//add event handle ...		
		for(Iterator itor = _eventListenerMap.entrySet().iterator();itor.hasNext();) {
			Map.Entry entry = (Map.Entry)itor.next();
			final ZScript zscript = ZScript.parseContent((String)entry.getValue());
			((ComponentCtrl)_comp).addEventHandler(
					(String)entry.getKey(), new EventHandler(zscript,null));
		}
		
	}
    /**
     * Returns the class name that is used to implement the component
     * associated with this tag.
     *
     * <p>Default: null
     *
     * @return the class name used to implement the component, or null
     * to use the default
     */
    public String getUse() {
        return _use;
    }
    /**
     * Sets the class name that is used to implement the component
     * associated with this tag.
     *
     * @param use the class name used to implement the component, or null
     * to use the default
     */
    public void setUse(String use) {
        this._use = use;
    }
	/** Returns the forward condition that controls how to forward
	 * an event, that is received by the component created
	 * by this info, to another component.
	 *
	 * <p>Default: null.
	 *
	 * <p>If not null, when the component created by this
	 * info receives the event specified in the forward condition,
	 * it will forward it to the target component, which is also
	 * specified in the forward condition.
	 *
	 * @see #setForward
	 */
	public String getForward() {
		return _forward;
	}
	/** Sets the forward condition that controls when to forward
	 * an event receiving by this component to another component.
	 *
	 * <p>The basic format:<br/>
	 * <code>onEvent1=id1/id2.onEvent2</code>
	 *
	 * <p>It means when onEvent1 is received, onEvent2 will be posted
	 * to the component with the specified path (id1/id2).
	 *
	 * <p>If onEvent1 is omitted, it is assumed to be onClick (and
	 * the equal sign need not to be specified.
	 * If the path is omitted, it is assumed to be the space owner
	 * {@link Component#getSpaceOwner}.
	 *
	 * <p>For example, "onOK" means "onClick=onOK".
	 *
	 * <p>You can specify several forward conditions by separating
	 * them with comma as follows:
	 *
	 * <p><code>onChanging=onChanging,onChange=onUpdate,onOK</code>
	 *
	 * @param forward the forward condition. There are several forms:
	 * "onEvent1", "target.onEvent1" and "onEvent1(target.onEvent2)",
	 * where target could be "id", "id1/id2" or "${elExpr}".
	 * The EL expression must return either a path or a reference to
	 * a component.
	 */
	public void setForward(String forward) {
		_forward = forward != null && forward.length() > 0 ? forward: null;
	}
}
