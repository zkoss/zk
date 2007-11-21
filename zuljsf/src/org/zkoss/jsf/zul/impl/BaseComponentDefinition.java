/* BaseComponentDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/11/21 , Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

/**
 * A Implement of component definition tag<br/>
 * @author Dennis.Chen
 *
 */
public class BaseComponentDefinition extends  AbstractComponent {
	
	private String _macroURI;

	private String _extends;

	private String _useClass;

	private String _name;

	private boolean _inline;

	private String _moldName;

	private String _moldURI;

	private Map _compAttrMap;
	
	public String getMacroURI() {
		return _macroURI;
	}

	public void setMacroURI(String macroURI) {
		this._macroURI = macroURI;
	}

	public String getExtends() {
		return _extends;
	}

	public void setExtends(String _extends) {
		this._extends = _extends;
	}

	public String getUseClass() {
		return _useClass;
	}

	public void setUseClass(String useClass) {
		this._useClass = useClass;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public boolean isInline() {
		return _inline;
	}

	public void setInline(boolean inline) {
		this._inline = inline;
	}

	public String getMoldName() {
		return _moldName;
	}

	public void setMoldName(String moldName) {
		this._moldName = moldName;
	}

	public String getMoldURI() {
		return _moldURI;
	}

	public void setMoldURI(String moldURI) {
		this._moldURI = moldURI;
	}
	
	/**
	 * Set dynamic attribute
	 * @param map the dynamic attributes.
	 */
	public void setDynamicAttribute(Map map) {
		_compAttrMap = map;
	}

	// ----------------------------------------------------- StateHolder Methods

	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[10];
		values[0] = super.saveState(context);
		Object m[] = saveAttachedMapState(context, _compAttrMap);
		values[1] = m[0];
		values[2] = m[1];
		values[3] = _macroURI;
		values[4] = _extends;
		values[5] = _useClass;
		values[6] = _name;
		values[7] = _inline?Boolean.TRUE:Boolean.FALSE;
		values[8] = _moldName;
		values[9] = _moldURI;
		return (values);

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_compAttrMap = (Map) restoreAttachedMapState(context, values[1], values[2]);
		_macroURI = (String)values[3];
		_extends = (String)values[4];
		_useClass = (String)values[5];
		_name = (String)values[6];
		_inline = ((Boolean)values[7]).booleanValue();
		_moldName = (String)values[8];
		_moldURI = (String)values[9];
	}
	
	public void encodeBegin(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		super.encodeBegin(context);
		Map requestMap = (Map)context.getExternalContext().getRequestMap();
		Map macros = (Map)requestMap.get(BaseComponentDefinition.class.getName());
		if(macros ==null)
			requestMap.put(BaseComponentDefinition.class.getName(), macros = new LinkedHashMap());
		macros.put(_name,this);
	}

	/** Helper method, check a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	
	/**
	 * register component definition to page.
	 * @param page a zul page instance
	 */
	/*public*/ void registerComponentDefinition(Page page){
		ComponentDefinition compdef;
		if (_macroURI != null) {
			compdef = page.getLanguageDefinition().getMacroDefinition(_name, _macroURI, _inline, null);
			if(!isEmpty(_useClass)){
				if (_inline)
					throw new UiException("class not allowed with inline macros, ");
				compdef.setImplementationClass(_useClass);
			}
		}else if(_extends != null){
			final ComponentDefinition ref = page.getLanguageDefinition()
			.getComponentDefinition(_extends);
			if (ref.isMacro())
				throw new UiException("Unable to extend from a macro component, "+_extends);
			compdef = ref.clone(null, _name);
			if (!isEmpty(_useClass)) {
				compdef.setImplementationClass(_useClass);
				//Resolve later since might defined in zscript
			}
		}else{
			if (isEmpty(_useClass))
				throw new UiException(" The Macro-define's property: 'use' cannot be empty! ");
			final ComponentDefinitionImpl cdi =
				new ComponentDefinitionImpl(page.getLanguageDefinition(), null, _name, (Class)null);
			cdi.setCurrentDirectory(null);
				//mold URI requires it
			compdef = cdi;
			compdef.setImplementationClass(_useClass);
		}
		
		if (!isEmpty(_moldURI))
			compdef.addMold(isEmpty(_moldName) ? "default": _moldName, _moldURI );
		for (Iterator e = _compAttrMap.entrySet().iterator(); e.hasNext();) {
			final Map.Entry me = (Map.Entry)e.next();
			compdef.addProperty((String)me.getKey(), (String)me.getValue());
		}
		
		page.getComponentDefinitionMap().add(compdef);
	}

}
