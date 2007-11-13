/**
 * 
 */
package org.zkoss.jsp.zul.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.lang.Classes;
import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.Macro;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Composer;

/**
 * @author Ian Tsai
 *
 */
public class InstanceTag extends BranchTag {

	
	private String name;
	private ComponentDefinition compDef;
	private Component[] comps;
	
	protected Component newComponent(Class use)throws Exception{
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		
	}

	/* */
	void initComponent() throws JspException  {
		if(_roottag==null)
			throw new IllegalStateException("Must be nested inside the page tag: "+this);	
		
		composeHandle = new ComposerHandler(_attrMap.remove("apply")); 
		try {//TODO: use-class initial works...
			//add composer to intercept creation...
			//TODO: composerExt.doBeforeCompose(page, parentComponent, compInfo); 
			
			Page page = this._roottag.getPage();
			compDef = page.getComponentDefinition(name, true);
			if(compDef==null)
				throw new JspException("can't find this Component's definition:"+name);
			Object useClass = compDef.getImplementationClass();
			
			if (compDef.isInlineMacro()) {// can't support at this time...
				final Map props = new HashMap();
				Component parent = this._parenttag.getComponent();
				props.put("includer", parent);
				
				compDef.evalProperties(props, page, parent);
				props.putAll(_attrMap);
				comps = parent.getDesktop().getExecution().
					createComponents(compDef.getMacroURI(), props);
			}
			else {
				if(useClass instanceof String)
					comps = new Component []{_comp=compDef.newInstance(page,useClass.toString())};
				else
				{
					Class clazz = (Class)useClass;
					comps = new Component []{_comp=(Component)clazz.newInstance()};
				}
			}
			
			
			composeHandle.doBeforeComposeChildren(_comp);
		} catch (Exception e) {
			composeHandle.doCatch(e);
			throw new JspException(e);
		}
		finally
		{
			composeHandle.doFinally();
		}
		if (_parenttag != null)_parenttag.addChildTag(this);
		else _roottag.addChildTag(this);
		
		for(int i=comps.length-1;i>=0;i--)
			comps[i].getDefinition().applyProperties(comps[i]);
		
	}
	
	
	void afterComposeComponent() throws JspException{
		
		if(compDef.isInlineMacro())// how to while is InlineMacro?
		{
			if ( comps==null)
				throw new JspTagException("newComponent() returns null");
		}
//		else if(compDef.isMacro())// how to while is Macro?
//		{
//			Macro macro = (Macro)_comp;
//		}
		else
		{
			super.afterComposeComponent();
		}
	}
	/**
	 * 
	 */
	public boolean isInlineMacro() {
		return compDef.isInlineMacro();
	}
	/**
	 * 
	 */
	public Component[] getComponents() {
		return comps;
	}
	

}//end of class...
