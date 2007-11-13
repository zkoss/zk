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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsp.zul.impl.AbstractTag;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

/**
 * 
 * Used to define a macro component.
 * @author Ian Tsai
 * <macro-definition />
 */
public class ComponentDefinitionTag  extends AbstractTag implements DynamicAttributes
{
	
	private String macroURI;
	private String _extends;
	private String useClass;
	private String name;
	private boolean inline;
	private String moldName;
	private String moldURI;
	private Map params = new LinkedHashMap();
	private ComponentDefinition compdef;
	
	/*
	 * register self to JspContext.
	 * (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws JspException, IOException {
		Map macros = (Map)getJspContext().getAttribute(Const.CONTEXT_KEY);
		if(macros ==null)
			getJspContext().setAttribute(Const.CONTEXT_KEY, macros = new LinkedHashMap());
		macros.put(name,this);
	}
	
	/**
	 * 
	 * @param page
	 */
	public void registComponentDefinition(Page page)
	{
		if (macroURI != null) {
			
			compdef = page.getLanguageDefinition().getMacroDefinition(name, macroURI, inline, null);
			
			if(!isEmpty(useClass)){
				if (inline)
					throw new UiException("class not allowed with inline macros, ");
				compdef.setImplementationClass(useClass);
			}
		}else if(_extends != null){
			final ComponentDefinition ref = page.getLanguageDefinition()
			.getComponentDefinition(_extends);
			if (ref.isMacro())
				throw new UiException("Unable to extend from a macro component, "+_extends);
			compdef = ref.clone(null, name);
			if (!isEmpty(useClass)) {
				compdef.setImplementationClass(useClass);
					//Resolve later since might defined in zscript
			}
		}else{
			if (isEmpty(useClass))
				throw new UiException(" The Macro-define's property: 'use' cannot be empty! ");
			final ComponentDefinitionImpl cdi =
				new ComponentDefinitionImpl(page.getLanguageDefinition(), null, name, (Class)null);
			cdi.setCurrentDirectory(null);
				//mold URI requires it
			compdef = cdi;
			compdef.setImplementationClass(useClass);
		}
		page.getComponentDefinitionMap().add(compdef);
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

	public boolean getInline() {
		return inline;
	}

	public void setInline(boolean inline) {
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

	public String getUseClass() {
		return useClass;
	}

	public void setUseClass(String use) {
		this.useClass = use;
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
