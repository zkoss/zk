/* InitTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed August 08 15:30:37     2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.jsp.impl.AbstractTag;

/**
 * 
 * A Tag map to the ZK {@link Initiator}.
 * Implemented by an initiator that will be invoked if it is specified
 * in the init directive.
 *
 * <p>&lt;z:init class="MyInit" /&gt;
 *
 * <p>Once specified, an instance inside this tag is created and {@link #doInit} is called
 * before the page is evaluated. Then, {@link #doAfterCompose} is called
 * after all components are created, and before any event is processed.
 * In additions, {@link #doFinally} is called
 * after the page has been evaluated. If an exception occurs, {@link #doCatch}
 * is called.
 *
 * <p>A typical usage: starting a transaction in doInit, rolling back it
 * in {@link #doCatch} and commit it in {@link #doFinally}
 * (if {@link #doCatch} is not called).
 *
 * @author Ian Tsai
 *
 */
public class InitTag extends AbstractTag implements DynamicAttributes{
	private static final Log log = Log.lookup(InitTag.class);
	private List _args = new ArrayList(5);
	private String _class;
	/**
	 *   Called when a tag declared to accept dynamic attributes is passed an 
	 *   attribute that is not declared in the Tag Library Descriptor.<br>
	 *   
	 *   In this InitTag implementation, this method is used to "add" args.<br>
	 *   For Example:<br>
	 *   <p>&lt;z:init use="demo.MyInit" arg0="an arg" arg1="another arg " ...../&gt;
	 *   
	 * @param uri the namespace of the attribute. Ignored in this version.
	 * @param localName the name of the attribute being set.
	 * @param value  the value of the attribute
	 */
	public void setDynamicAttribute(String uri, String localName, Object value)
	throws JspException {
		if(!localName.startsWith("arg"))
			throw new IllegalArgumentException("Declared attribute:"+localName+
					" is illegal. Please use arg[int] instead.");
		_args.add(Integer.parseInt(localName.substring(3)), value);
	}
	
	/**
	 *  Add this Initiator into HttpRequest, this will be processed by Component 
	 *  container:{@link RootTag}.   
	 */
	public void doTag() throws JspException, IOException {
		Exception ex = null;
		 try {
			 Initiator init = (Initiator) Classes.forNameByThread(_class).newInstance();
			 this.storeInitiator(init,_args);
			 
		} catch (ClassNotFoundException e) {
			ex=e;log.error(e);
		} catch (InstantiationException e) {
			ex=e;log.error(e);
		} catch (IllegalAccessException e) {
			ex=e;log.error(e);
		}
		if(ex!=null)throw new JspException(" doTag failed!");
	}
	
	/**
	 * Store initiator into  HttpRequest.
	 * @param init the initiator need to be stored.
	 */
	private void storeInitiator(Initiator init, List args)
	{
		Initiators inits   = (Initiators) this.getJspContext().
			getAttribute(Initiators.class.getName());
		if(inits==null)getJspContext().setAttribute(
				Initiators.class.getName(), inits = new Initiators());
		inits.addInitiator(init, args);
	}
	/**
	 * set Initiator class .
	 * @param clazz a class name  with derived class which is implements {@link Initiator}  
	 */
	public void setInitClass(String clazz)
	{
		_class = clazz;
	}
	
	public String getInitClass()
	{
		return _class;
	}
	

}
