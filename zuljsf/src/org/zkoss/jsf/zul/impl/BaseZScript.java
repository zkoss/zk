/* BaseZScript.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
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
import javax.faces.context.FacesContext;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * The Base implementation of ZScript. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 */
public class BaseZScript extends AbstractComponent {

	private boolean _deferred;
	private String _lang = null;
	
	private RootComponent _rootcomp;
	private BranchComponent _parentcomp;
	

	public void loadZULTree(org.zkoss.zk.ui.Page page,StringWriter writer) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		
		String content = getBodyContent();
		setBodyContent(null);//clear it,
		if(content==null) return;
		final ZScript zscript = ZScript.parseContent(content);
		
		if (zscript.getLanguage() == null)
			zscript.setLanguage(_lang != null ? _lang: _rootcomp.getZScriptLanguage());
		
		Component _parentzulcomp = null;
		if(_parentcomp!=null){
			_parentzulcomp = _parentcomp.getZULComponent();
		}
		
		_rootcomp.processZScript(_parentzulcomp, zscript);
	}
	
	
	/** 
	 * Override method,
	 * We Construct ZUL JSF Component tree here.
	 * This method is called by JSF implementation, deriving class rarely need to invoke this method.
	 */
	public void encodeBegin(FacesContext context) throws IOException{
		if (!isRendered() || !isEffective())
			return; //nothing to do
		super.encodeBegin(context);
		final AbstractComponent ac =
		(AbstractComponent)findAncestorWithClass(this, AbstractComponent.class);
		if (ac instanceof RootComponent) { //root component tag
			_rootcomp = (RootComponent)ac;
		} else if (ac instanceof BranchComponent) {
			_parentcomp = (BranchComponent)ac;
			_rootcomp = _parentcomp.getRootComponent();
		} else {
			throw new IllegalStateException("Must be nested inside the page component: "+this);
		}
		
		//keep component tree structure for zul jsf component
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
	 * return ComponentInfo of RootComponent
	 */
	protected ComponentInfo getComponentInfo(){
		if(_rootcomp!=null){
			return _rootcomp.getComponentInfo();
		}
		return null;
	}
	
	/**
	 * Returns whether to defer the execution of this zscript.
	 * <p>Default: false.
	 */
	public boolean isDeferred() {
		return _deferred;
	}
	/**
	 * Sets whether to defer the execution of this zscript.
	 * @param deferred whether to defer the execution.
	 */
	public void setDeferred(boolean deferred) {
		this._deferred  = deferred;
	}

	/** Returns the name of the scripting language in this ZScript tag.
	 *
	 * <p>Default: null (use what is defined in {@link org.zkoss.jsf.zul.tag.PageTag}).
	 * @return the name of the scripting language in this ZScript tag. 
	 */
	public String getLanguage() {
		return _lang;
	}
	/**
	 * Sets the name of the scripting language in this ZScript tag.
	 *
	 * <p>Default: Java.
	 *
	 * @param lang the name of the scripting language, such as
	 * Java, Ruby and Groovy.
	 */
	public void setLanguage(String lang) {
		this._lang = lang;
	}

	
	public Object saveState(FacesContext context) {
		Object values[] = new Object[3];
		values[0] = super.saveState(context);
		values[1] = _lang;
		values[2] = _deferred?Boolean.TRUE:Boolean.FALSE;
		return (values);
	}

	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_lang = ((String) values[1]);
		_deferred = ((Boolean)values[2]).booleanValue();
	}
	
	
}
