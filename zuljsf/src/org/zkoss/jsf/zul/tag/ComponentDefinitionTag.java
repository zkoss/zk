/* ComponentDefinitionTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2007 11:13:40 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.jsf.zul.tag;

/**
 * @author Dennis.Chen
 * 
 */
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.BaseComponentDefinition;
import org.zkoss.jsf.zul.tag.impl.AbstractTag;

/**
 * 
 * Used to define a ZK custom component in Jsp environment.<br>
 * 
 * @author Ian Tsai
 * 
 */
public class ComponentDefinitionTag extends AbstractTag implements DynamicAttributes {

	private String _macroURI;

	private String _extends;

	private String _useClass;

	private String _name;

	private boolean _inline;

	private String _moldName;

	private String _moldURI;

	/**
	 * Handle dynamic Attribute of this tag.
	 */
	protected Map _dynamicAttrMap = new LinkedHashMap();

	public ComponentDefinitionTag() {
		super("ComponentDefinition");
	}

	public void release() {
		super.release();
		_macroURI = null;
		_extends = null;
		_useClass = null;
		_name = null;
		_inline = false;
		_moldName = null;
		_moldURI = null;
		_dynamicAttrMap = null;
	}

	/**
	 * Called when a tag declared to accept dynamic attributes is passed an
	 * attribute that is not declared in the Tag Library Descriptor.<br>
	 * 
	 * @param uri
	 *            the namespace of the attribute
	 * @param localName
	 *            the name of the attribute being set.
	 * @param value
	 *            the value of the attribute
	 */
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		if (uri == null || ZUL_JSF_NS.equals(uri)) {
			_dynamicAttrMap.put(localName, value);
		}
	}

	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		if(!(comp instanceof BaseComponentDefinition)){
			throw new RuntimeException("not a BaseComponentDefinition");
		}
		
		((BaseComponentDefinition) comp).setMacroURI(_macroURI);
		((BaseComponentDefinition) comp).setExtends(_extends);
		((BaseComponentDefinition) comp).setUseClass(_useClass);
		((BaseComponentDefinition) comp).setName(_name);
		((BaseComponentDefinition) comp).setInline(_inline);
		((BaseComponentDefinition) comp).setMoldName(_moldName);
		((BaseComponentDefinition) comp).setMoldURI(_moldURI);
		
		((BaseComponentDefinition) comp).setDynamicAttribute(_dynamicAttrMap);
	}

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

}// end of class...

