/* BaseUi.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/11/21    2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;

/**
 * The Base implementation of ZScript. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 */
public class BaseUi extends BranchComponent {

	private String _tag;

	private ComponentDefinition _compDef;
	
	/**
	 * Components of this component,
	 * if the definition of _tag set to inline, then _comps is the created components base on macroURI
	 * if desn't inline, then _comps is the created HtmlMacroComponent.
	 */
	private Component[] _comps;
	
	public void setTag(String tag){
		_tag = tag;
	}
	
	public String getTag(){
		return _tag;
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = _tag;
		return (values);
	}

	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_tag = ((String) values[1]);
	}
	

	void initComponent(org.zkoss.zk.ui.Page page) {
		if(_rootcomp==null)
			throw new IllegalStateException("Must be nested inside the page component: "+this);
		if(_zulcomp!=null){
			_zulcomp.detach();
			_zulcomp = null;
		}
		
		try {
			_compDef = page.getComponentDefinition(_tag, true);
			if(_compDef ==null){
				throw new RuntimeException("Component Definition not found :"+_tag);
			}
			final Object useClass = _compDef.getImplementationClass();
			final String use = getUse();
			
			if (_compDef.isInlineMacro()) {// the tag holds multiple components.
				final Map props = new HashMap();
				BranchComponent parent = this.getParentComponent();
				Component comp = null; 
				if(parent!=null){
					comp = parent.getZULComponent();
					props.put("includer", comp);
				}else{
					props.put("includer", page);
				}
				_compDef.evalProperties(props, page, comp);
				props.putAll(_compAttrMap);
				if(use!=null)props.put("use", this.getUse());
				_comps = page.getDesktop().getExecution().
					createComponents(_compDef.getMacroURI(), props);
			}else {// the tag hold only one component. 
				if(use!=null){
					_zulcomp=(Component)Class.forName(use).newInstance();
				}else if(useClass instanceof Class){
					//Class clazz = (Class)useClass;
					//_zulcomp=(Component)clazz.newInstance();
					_zulcomp=_compDef.newInstance(page,((Class)useClass).getName());
				}else{
					_zulcomp=_compDef.newInstance(page,useClass.toString());
				}
				
				if(_zulcomp instanceof org.zkoss.zk.ui.AbstractComponent){
					((org.zkoss.zk.ui.AbstractComponent)_zulcomp).setComponentDefinition(_compDef);
				}
				
				_comps = new Component []{_zulcomp};
				composer.doBeforeComposeChildren(_zulcomp);
				_zulcomp.getDefinition().applyProperties(_zulcomp);
			}
		} catch (Exception e) {
			if(!_compDef.isInlineMacro()){
				if(!composer.doCatch(e)){
					throw new RuntimeException(e.getMessage(),e);						
				}
			}else{
				throw new RuntimeException(e.getMessage(),e);
			}
		}finally{
			if(!_compDef.isInlineMacro()){
				try {
					composer.doFinally();
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		}
		
		if(_idSet && _zulcomp!=null){
			_zulcomp.setId(getId());
		}
		
		if (_parentcomp != null) {
			_parentcomp.addChildZULComponent(this);
		}else {
			_rootcomp.addChildZULComponent(this);
		}
	}
	
	void afterComposeComponent() {
		if(!_compDef.isInlineMacro()){
			//it is HtmlMacroComponent, so we need to call afterComposeComponent,
			//but we must skip evaluateDynaAttributes in afterComposeComponent
			super.afterComposeComponent();
		}
	}

	public Component[] getComponent(){
		return _comps;
	}
	
	
	
}
