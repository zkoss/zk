/* MacroDefinitionTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed December 09 14:50:37     2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsp.zul.impl.AbstractTag;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

/**
 * 
 * Used to define a macro component.
 * @author Ian Tsai
 * <macro-definition />
 */
public class MacroDefinitionTag  extends AbstractTag implements DynamicAttributes
{
	public static final String CONTEXT_KEY = MacroDefinitionTag.class.getName()+"!Set";
	private String macroURI;
	private String _extends;
	private String use;
	private String name;
	private String inline;
	private String moldName;
	private String moldURI;
	private Map params = new HashMap();
	
	/*
	 * register self to JspContext.
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws JspException, IOException {
		Set macros = (Set)getJspContext().getAttribute(CONTEXT_KEY);
		if(macros ==null)
			getJspContext().setAttribute(CONTEXT_KEY, macros = new HashSet());
		macros.add(this);
	}
	
	/**
	 * 
	 * @param pgdef
	 */
	public void registerMacroDefinition(PageDefinition pgdef)
	{
		ComponentDefinition compdef;
		if (macroURI != null) {
			final boolean bInline = "true".equals(inline);
			compdef = pgdef.getLanguageDefinition().getMacroDefinition(
					name, macroURI, bInline, pgdef);
			if(!isEmpty(use)){
				if (bInline)
					throw new UiException("class not allowed with inline macros, ");
				compdef.setImplementationClass(use);
			}
		}else if(_extends != null){
			final ComponentDefinition ref = pgdef.getLanguageDefinition()
			.getComponentDefinition(_extends);
			if (ref.isMacro())
				throw new UiException("Unable to extend from a macro component, "+_extends);
			compdef = ref.clone(null, name);
			if (!isEmpty(use)) {
				compdef.setImplementationClass(use);
					//Resolve later since might defined in zscript
			}
		}else{
			if (isEmpty(use))
				throw new UiException(" The Macro-define's property: 'use' cannot be empty! ");
			final ComponentDefinitionImpl cdi =
				new ComponentDefinitionImpl(null, pgdef, name, (Class)null);
			cdi.setCurrentDirectory(null);
				//mold URI requires it
			compdef = cdi;
			compdef.setImplementationClass(use);
		}
		pgdef.addComponentDefinition(compdef);
		if (!isEmpty(moldURI))
			compdef.addMold(isEmpty(moldName) ? "default": moldName, moldURI );
		for (Iterator e = params.entrySet().iterator(); e.hasNext();) {
			final Map.Entry me = (Map.Entry)e.next();
			compdef.addProperty((String)me.getKey(), (String)me.getValue());
		}
	}
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute
	 * (java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String attrName, Object value) 
	throws JspException {
		params.put(attrName, value);
	}

	public String getExtends() {
		return _extends;
	}

	public void setExtends(String extendz) {
		this._extends = extendz;
	}

	public String getInline() {
		return inline;
	}

	public void setInline(String inline) {
		this.inline = inline;
	}

	public String getMacroURI() {
		return macroURI;
	}

	public void setMacroURI(String macroURI) {
		this.macroURI = macroURI;
	}

	public String getMoldName() {
		return moldName;
	}

	public void setMoldName(String moldName) {
		this.moldName = moldName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}


	public String getMoldURI() {
		return moldURI;
	}


	public void setMoldURI(String moldURI) {
		this.moldURI = moldURI;
	}
	
	
	
	
	
}//end of class...
