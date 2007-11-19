/* AbstractComponent.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 7, 2007 5:53:41 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * The skeletal class for implementing the generic ZULJSF Component.
 * This component handle _if, _unless attributes of ZUL Component.
 * This component also handle bodyContent which is set by {@link org.zkoss.jsf.zul.tag.impl.AbstractTag#doAfterBody()}
 * 
 * @author Dennis.Chen
 */
public class AbstractComponent extends UIComponentBase {

	private boolean _if = true;

	private boolean _unless = false;
	
	private boolean _suppressed = false;

	private String _bodyContent;

	/**
	 * Returns whether this component is effective. If false, this component does
	 * nothing (as if not specified at all).
	 */
	public boolean isEffective() {
		return _if && !_unless;
	}

	/**
	 * Returns the if condition. If the if condition is false, this component is
	 * ignored. If the unless condition ({@link #getUnless}) is true, this
	 * component is ignored, too. Components deriving from this class shall
	 * invoke {@link #isEffective} to know the result.
	 * 
	 * <p>
	 * Default: true.
	 */
	public boolean getIf() {
		return _if;
	}

	/**
	 * Sets the if condition.
	 */
	public void setIf(boolean ifcond) {
		_if = ifcond;
	}

	/**
	 * Returns the unless condition. If the unless condition is true, this
	 * component is ignored. If the if condition ({@link #getIf}) is true,
	 * this component is ignored, too. Components deriving from this class shall
	 * invoke {@link #isEffective} to know the result.
	 * 
	 * <p>
	 * Default: false.
	 */
	public boolean getUnless() {
		return _unless;
	}

	/**
	 * Sets the unless condition.
	 */
	public void setUnless(boolean unless) {
		_unless = unless;
	}

	/**
	 * get Component Family of ZULJSF Component.
	 */
	public String getFamily() {
		return "org.zkoss.zul";
	}

	/**
	 * A convenient method for finding ancestor of special class. 
	 * this method is use to build ZULJSF component tree under JSF component tree.
	 * 
	 * @param component
	 *            a component, usually a AbstractComponent instance
	 * @param clazz
	 *            a class, usually a AbstractComponent class
	 * @return a ancestor Component 
	 */
	protected UIComponent findAncestorWithClass(UIComponent component,
			Class clazz) {
		UIComponent parent = component.getParent();
		if (parent != null) {
			if (clazz.isAssignableFrom(parent.getClass()))
				return parent;
			return findAncestorWithClass(parent, clazz);
		}
		return null;
	}

	/**
	 * Call by RootComponent or BranchComponent to load tree structure of zk zul components. 
	 * This method is invoked from a RootComponent's encodeEnd() or a BranchComponent's loadZK().
	 * It will go through all ZULJSF Component Tree to initial ZUL Component of corresponding ZULJSFComponent. 
	 * <br/>Note: Do nothing in default implementation.
	 */
	protected void loadZULTree(org.zkoss.zk.ui.Page page,StringWriter writer) throws IOException {
		// do nothing
		
	}

	/**
	 * Set BodyContnet, this method is called by {@link org.zkoss.jsf.zul.tag.impl.AbstractTag} to setup JSP Body
	 * Content, developer rarely call this method.
	 * @param content content of this component
	 */
	public void setBodyContent(String content) {
		_bodyContent = content;
	}

	/**
	 * get the body content of this ZULJSF Component.
	 * @return the body content of JSP 
	 */
	protected String getBodyContent() {
		return _bodyContent;
	}

	/**
	 * get current FacesContext for this component, this method is protected in
	 * super class, I public it for convenient usage.
	 */
	public FacesContext getFacesContext() {
		return super.getFacesContext();
	}

	/**
	 * Get ComponentInfo for current Component Tree.<br/> In LeafComponent,
	 * it always return RootComponent's getComponentInfo(), if RootComponent doesn't exist return null;<br/> 
	 * In RootComponet , it check a existed instance and return it, if not, a new instance will be created and return.</br> 
	 * Note : return null in default implementation.
	 * @return a ComponentInfo of current Component Tree
	 */
	protected ComponentInfo getComponentInfo() {
		return null;
	}

	// ----------------------------------------------------- StateHolder Methods

	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = _if ? Boolean.TRUE : Boolean.FALSE;
		values[2] = _unless ? Boolean.TRUE : Boolean.FALSE;
		values[3] = _suppressed?Boolean.TRUE:Boolean.FALSE;
		return (values);
	}
	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_if = ((Boolean) values[1]).booleanValue();
		_unless = ((Boolean) values[2]).booleanValue();
		_suppressed = ((Boolean) values[3]).booleanValue();
	}

	
	/**
	 * a convenient to save the state of a Map.
	 * @param context FacesContext
	 * @param mapObject a Map to save
	 * @return two handled Object, Object[0] handle the state of keys of Map, Object[1] handle the state of values of Map.
	 */
	protected static Object[] saveAttachedMapState(FacesContext context,
			Map mapObject) {
		if (null == context) {
			throw new NullPointerException();
		}
		if (null == mapObject) {
			return null;
		}
		Object[] result = new Object[2];
		List keyList = new LinkedList();
		List valueList = new LinkedList();
		
		for(Iterator itor = mapObject.entrySet().iterator();itor.hasNext();)
		{
			Map.Entry entry = (Map.Entry)itor.next();
			keyList.add(entry.getKey());
			valueList.add(entry.getValue());
		}
		result[0] = saveAttachedState(context,keyList);
		result[1] = saveAttachedState(context,valueList);
		return result;
	}
	
	/**
	 * a convenient to restore the state of a Map.
	 * @param context FacesContext
	 * @param keys a Object which is create by {@link #saveAttachedMapState}[0]
	 * @param values a Object which is create by {@link #saveAttachedMapState}[1]
	 * @return restored Map instance
	 */
	protected static Map restoreAttachedMapState(FacesContext context,
			Object keys,Object values) {
		if (null == context) {
			throw new NullPointerException();
		}
		if (null == keys || null == values) {
			return null;
		}
		List keyList = (List)restoreAttachedState(context,keys);
		List valueList = (List)restoreAttachedState(context,values);
		
		
		Map result = new LinkedHashMap(keyList.size());
		int size = keyList.size();
		for(int i=0;i<size;i++){
			result.put(keyList.get(i), valueList.get(i));
		}
		return result;
	}
	
	/**
     * <p>Return <code>true</code> if rendering should be suppressed because
     * of any of the follow reasons.</p>
     * <ul>
     * <li>The component is a facet (whose rendering, if any), is always
     *     the responsibility of the owing component.</li>
     * <li>The component has its <code>rendered</code> property set
     *     to <code>false</code>.</li>
     * <li>The component is a child of a parent whose
     *     <code>rendersChildren</code> is <code>true</code>.</li>
     * <li>The component is a child of a parent whose rendering is itself
     *     suppressed.</li>
     * </ul>
     */
	protected boolean isSuppressed(){
		return _suppressed;
	}
	
	/**
	 * set suppressed by {@link org.zkoss.jsf.zul.tag.impl.AbstractTag} only, developer should not call this method.
	 * @param suppressed
	 */
	public void setSuppressed(boolean suppressed){
		this._suppressed = suppressed;
	}

}
