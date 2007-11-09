/* LeafComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2007 5:56:04 PM     2007, Created by Dennis.Chen
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
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.metainfo.impl.AnnotationHelper;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * The skeletal class used to implement the ZULJSF Component for ZK components
 * that don't accept any child.
 * @author Dennis.Chen
 *
 */
abstract public class LeafComponent extends AbstractComponent{

	//ZUL Component for RenderPhase Only.
	private Component _zulcomp;
	
	//ZUL JSF component
	private RootComponent _rootcomp;
	private BranchComponent _parentcomp;
	
	//original attribute of this ZULJSF component. 
	private Map _compAttrMap;
	
	//attribute set by custom-attributes
	private Map _compCustomAttributes;
	
	private String _use;
	
	private String _forward;
	
	//if id attribute is seted, when we must set id to zul component. 
	private boolean _idSet = false;
	
	
	//map for zul component attribute
	private Map _zulAttrMap = new LinkedHashMap();
	//map for zul component listener
	private Map _eventListenerMap = new LinkedHashMap();
    

	/** Returns the RootComponent that this Component belongs to.
	 */
	/*package*/ RootComponent getRootComponent() {
		return _rootcomp;
	}
	/** Returns the Parent Component
	 */
	/*package*/ BranchComponent getParentComponent() {
		return _parentcomp;
	}

	

	/**
	 * Creates a ZUL Component that is associated with this ZULJSF Component,
	 * and returns the new ZUL Component (never null).
	 * The deriving class must implement this method to create
	 * the proper component, initialize it and return it.
	 *
	 * @param use the use ZUL Component class  
	 * @return A ZUL Component
	 * @throws Exception
	 */
	abstract protected Component newComponent(Class use)throws Exception;


	/** 
	 * Override method,
	 * We Construct ZUL JSF Component tree here.
	 * This method is called by JSF implementation, deriving class rarely need to invoke this method.
	 */
	public void encodeBegin(FacesContext context) throws IOException{
		super.encodeBegin(context);
		final AbstractComponent ac =
		(AbstractComponent)findAncestorWithClass(this, AbstractComponent.class);
		if (ac instanceof RootComponent) { //root component tag
			_rootcomp = (RootComponent)ac;
			_parentcomp = null;
		} else if (ac instanceof BranchComponent) {
			_parentcomp = (BranchComponent)ac;
			_rootcomp = _parentcomp.getRootComponent();
		} else {
			throw new IllegalStateException("Must be nested inside the page component: "+this);
		}
		
		//keep component tree structure for ZULJSF Component
		ComponentInfo cinfo = getComponentInfo();
		if(cinfo!=null){
			if(_parentcomp!=null){
				cinfo.addChildInfo(_parentcomp, this);
			}else if(_rootcomp!=null){
				cinfo.addChildInfo(_rootcomp, this);
			}
		}
	}
	
	/**
	 * Get ComponentInfo for current Component Tree.<br/> 
	 * it always return RootComponent's getComponentInfo(), if RootComponent doesn't exist return null;
	 */
	protected ComponentInfo getComponentInfo(){
		if(_rootcomp!=null){
			return _rootcomp.getComponentInfo();
		}
		return null;
	}
	
	

	/**
	 * Override Method, don't render children my self. return false;
	 */
	public boolean getRendersChildren() {
		if(isSuppressed()){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public void encodeChildren(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		if(isSuppressed()){
			StringWriter sw = new StringWriter();
			
			ResponseWriter ow = context.getResponseWriter();
			
			context.setResponseWriter(ow.cloneWithWriter(sw));
			
			List children = this.getChildren();
			for(Iterator iter = children.iterator();iter.hasNext();){
				Utils.renderComponent((UIComponent)iter.next(), context);
			}
			
			context.setResponseWriter(ow);
			sw.close();
			String content = sw.toString();
			
			sw = null;
			
			this.setBodyContent(content);
			
		}
	}
	/**
	 * Override Method, write a special mark denoting the component in this metohd 
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		if(isSuppressed()){
			//TODO implement it.
			//throw new UnsupportedOperationException("don't put component inside a suppressed parent component "+this.getParent()+", This feature doen't implement yet");
			StringWriter writer = new StringWriter();
			writeComponentMark(writer);
			/*if(_parentcomp!=null){
				_parentcomp.setBodyContent(_suppressedContent+writer.toString());
			}else{
				_rootcomp.setBodyContent(_suppressedContent+writer.toString());
			}*/
			context.getResponseWriter().write(writer.toString());
		}else{
			writeComponentMark(context.getResponseWriter()); //write a special mark denoting the component
		}
	}
	
	
	
	
	
	/**
	 * Call by RootComponent or BranchComponent to load zk stuff. 
	 */
	protected void loadZULTree(StringWriter writer) throws IOException{
		if (!isRendered() || !isEffective())
			return; //nothing to do
		initComponent();
		afterComposeComponent();//finish compose the component
		setBodyContent(null); //clear
	}
	
	/** 
	 * Create ZUL Component, and then associate it to it's Parent Component or Page Component
	 * Called by {@link #loadZULTree}.
	 */
	/*package*/ void initComponent() {
		
		if(_rootcomp==null)
			throw new IllegalStateException("Must be nested inside the page component: "+this);
		
		//maybe user binding this ZULJSF Component in session , so clear associated zulcomp before init 
		if(_zulcomp!=null){
			_zulcomp.detach();
			_zulcomp = null;
		}
		
		try {//TODO: use-class initial works...
			_zulcomp = newComponent(_use!=null ? Classes.forNameByThread(_use) : null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if(_idSet){
			_zulcomp.setId(getId());
		}
		
		if (_parentcomp != null) {
			_parentcomp.addChildZULComponent(this);
		}else {
			_rootcomp.addChildZULComponent(this);
		}
		//apply definition
		_zulcomp.getDefinition().applyProperties(_zulcomp);
		
	}
	
	/** 
	 * Returns the ZUL Component associated with this ZUL JSF Component
	 * the associated ZUL Component is only exist after {@link #loadZULTree}
	 */
	protected Component getZULComponent() {
		return _zulcomp;
	}
	
	
	/** after children creation do dynamic attributes setter work and registers event handler.
	 * Called by {@link #loadZULTree}.
	 */
	/*package*/void afterComposeComponent(){
		

		if (_zulcomp == null)
			throw new RuntimeException("newComponent() returns null");

		//setup EventListener and zul attribute from component attribute;
		if(_compAttrMap!=null){
			Iterator iter = _compAttrMap.keySet().iterator();
			while(iter.hasNext()){
				String localName = (String)iter.next();
				Object value = _compAttrMap.get(localName);
				if(localName.startsWith("on")){
					_eventListenerMap.put(localName, value);
				}else {
					_zulAttrMap.put(localName, value);
				}
			}
		}
		
		try {
			evaluateDynaAttributes(_zulcomp);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		//process the forward condition
		if(_forward!=null){
			ComponentsCtrl.applyForward(_zulcomp, _forward);
		}
		
		//add event handle ...
		for(Iterator itor = _eventListenerMap.entrySet().iterator();itor.hasNext();)
		{
			Map.Entry entry = (Map.Entry)itor.next();
			final ZScript zscript = ZScript.parseContent((String)entry.getValue());
			((ComponentCtrl)_zulcomp).addEventHandler(
					(String)entry.getKey(), new EventHandler(zscript,null));
		}
		
		//do afterCompose
		if (_zulcomp instanceof AfterCompose)
			((AfterCompose)_zulcomp).afterCompose();
		
		//fire onCreate
		if (Events.isListened(_zulcomp, Events.ON_CREATE, false))
			Events.postEvent(
				new CreateEvent(Events.ON_CREATE, _zulcomp, Executions.getCurrent().getArg()));
		
		
		//process JSF attribute after dynamic attribute is assigend.
		afterZULComponentComposed(_zulcomp);
		
	}
	
	/**
	 * Test if the attributes are annotation or component attributes.<br>
	 * If is Component attributes then invokes setter methods to update all assigned attributes.
	 * If is annotations then add annotation to component 
	 * @param target the target component
	 * @throws NoSuchMethodException 
	 * @throws ModificationException 
	 */
	private void evaluateDynaAttributes(final Component target) 
	throws ModificationException, NoSuchMethodException{

		AnnotationHelper helper = null;
		boolean hitann = false;
		for(Iterator itor = _zulAttrMap.entrySet().iterator();itor.hasNext();)
		{
			
			Map.Entry entry= (Entry) itor.next();
			
			String attnm = (String)entry.getKey();
			Object value = _zulAttrMap.get(attnm);
			if(value instanceof ValueBinding){
				value = ((ValueBinding)value).getValue(this.getFacesContext());
			}
			
			hitann = false;
			
			if(value instanceof String){
				String attval = (String)value;
				final int len = attval.length();
				if (len >= 3 && attval.charAt(0) == '@' && 
						attval.charAt(1) == '{' && 
						attval.charAt(len-1) == '}') { //annotation
					
					if(helper == null) {
						helper = new AnnotationHelper();
					}
					helper.addByCompoundValue(attval.substring(2, len -1));
					helper.applyAnnotations(target, 
							"self".equals(attnm) ? null: attnm, true);
					hitann = true;
				}
			}
			
			if(!hitann){
				Fields.setField(target, attnm, value, true);
			}
		}
		
		if(_compCustomAttributes!=null){
			for(Iterator iter = _compCustomAttributes.keySet().iterator();iter.hasNext();){
				String att = (String)iter.next();
				Object value = _compCustomAttributes.get(att);
				target.setAttribute(att, value);
			}
		}
	}
	
	/**
	 * This method give the delivering class a chance to handle ZUL Component just after it is composed.<br/>
	 * This method will be invoked after ZUL component is composed and listener, attribute of ZUL component is setted.
	 * Note: Default implementation do nothing.
	 */
	protected void afterZULComponentComposed(Component zulcomp){
		//do nothing.
	}
	
	/** Writes a special mark to the output to denote the location
	 * of this component.
	 */
	/*package*/ void writeComponentMark(Writer writer) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		Utils.writeComponentMark(writer, this);
	}
	
	/**
	 * Get value of attribute of Component. if value of attribute is a ValueBinding, then Binding result will return.
	 * @param att attribute name
	 * @return value of attribute
	 */
	public Object getAttributeValue(String att){
		Object value = _compAttrMap.get(att);
		if(value instanceof ValueBinding){
			value = ((ValueBinding)value).getValue(this.getFacesContext());
		}
		return value;
	}
	
	/**
	 * set value to attribute of Component, if a ValueBinding is associated with attribute , then it will set value to Binding Object<br/>
	 * 
	 * Note: set attribute after {@link #loadZULTree} doesn't affect the ZUL Component which is associated to this component.
	 * 
	 * @param att attribute name
	 * @param value attribute value;
	 */
	public void setAttributeValue(String att,Object value){
		Object oldvalue = _compAttrMap.get(att);
		if(oldvalue instanceof ValueBinding){
			((ValueBinding)value).setValue(this.getFacesContext(),value);
		}else{
			_compAttrMap.put(att,value);
		}
	}
	
    /**
     * Returns the class name that is used to implement the ZUL Component
     * associated with this ZULJSF Component.
     *
     * <p>Default: null
     *
     * @return the class name used to implement the ZUL Component, or null
     * to use the default
     */
    public String getUse()
    {
        return _use;
    }
    /**
     * Sets the class name that is used to implement the ZUL Component
     * associated with this ZULJSF Component.
     *
     * @param use the class name used to implement the ZUL Component, or null
     * to use the default
     */
    public void setUse(String use)
    {
        this._use = use;
    }
	
    
    /**
     * Set dynamic Attribute of this ZULJSF Component, 
     * This method is called by LeafTag only, developer should not call this method directly.
     * 
     * @param map the dynamic attributes.
     */
	public void setZULDynamicAttribute(Map map) {
		_compAttrMap = map;
	}
	
	/**
	 * set custom attributes of this ZULJSF Component,
	 * This method is called by BaseCustomeAttribute only,
	 * @param map
	 */
	/*package*/ void setZULCustomAttribute(Map map){
		_compCustomAttributes = map;
	}
	
	/**
	 * set the attribute(name) of this component to value, the difference between this method and {@link #setAttributeValue}
	 * is this method directly replace the attribute whether the attribute associate to a ValueBinding.
	 *  Note: set attribute after {@link #loadZULTree(StringWriter)} doesn't affect the ZUL Component which is associated to this component.
	 * @param name attribute name of component
	 * @param value value of attribute
	 */
	protected void addZULDynamicAttribute(String name,Object value){
		if(_compAttrMap!=null){
			_compAttrMap.put(name,value);
		}else{
			throw new NullPointerException();
		}
	}
	
	
	
	
	
//	 ----------------------------------------------------- StateHolder Methods

	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[5];
		values[0] = super.saveState(context);
		values[1] = _use;
		Object m[] = saveAttachedMapState(context, _compAttrMap);
		values[2] = m[0];
		values[3] = m[1];
		values[4] = _idSet?Boolean.TRUE:Boolean.FALSE; 
		return (values);

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_use = (String)values[1];
		_compAttrMap = (Map)restoreAttachedMapState(context,values[2],values[3]);
		_idSet = ((Boolean)values[4]).booleanValue();
	}
	
	/**
	 * Override Method, remember the id is setted.
	 */
	public void setId(String id) {
		super.setId(id);
		_idSet = true;
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
	
	
	/**
	 * get parent UI Form of this component.
	 * @return the parent component which type is UIForm.
	 */
	/*package*/ UIForm getUIForm() {
		UIComponent comp = getParent();
		while(comp!=null){
			if(comp instanceof UIForm){
				return (UIForm)comp;
			}
			comp = comp.getParent();
		}
		return null;
	}
	
	
}
