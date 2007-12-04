/* EJB3VariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec 1 2007, Created by jeffliu
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.ejb3;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.Maps;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

public class EJB3VariableResolver implements VariableResolver {
	
	private String _jndiPrepend="";
	
	private Map _vars = new HashMap();
	
	/**
	 * This constructor take agruments to initialize JNDI names. First, the variable will be resolved from the key-value pairs. 
	 * If not found, the variable will be look up as a sessionBean.
	 * </p>
	 * <ol>
	 * <li>prepend - The prepended part of JNDI name</li>
	 * <li>vars - The key-value pairs for JNDI name and its corresponding variable name</li>
	 * </ol>
	 * For example:
	 * <p>
	 * By default, session beans will bind to JNDI in the form ejbName/remote for remote interfaces and ejbName/local in the case of local interfaces. When the EJBs are deployed in an .ear file, the default jndi binding will be prepended by the name of the .ear file.
	 * As a result, if the ear file name is foo.ear, prepend is: foo
	 * </p>
	 * </br>
	 * <p>
	 * If you define your own jdni binding, the string should be in key-value pairs format as</br>
	 * "a=custom/MySession,b=custom/MySession2,emf=java:/EntityManagerFactory"
	 * </p>
	 * 
	 * @author Jeff
	 *
	 * @param prepend prepended part of JNDI name
	 * @param vars key-value pairs for JNDI name and its corresponding variable name
	 */
	public EJB3VariableResolver(String prepend, Map vars) {
		_jndiPrepend = prepend;
		_vars = vars;
	}
	
	public EJB3VariableResolver(Map vars) {
		_vars = vars;
	}

	/**
	 * Get EJB3 SessionBean or EntityManagerFactory by variable name
	 * @param var name of EJB3 Bean
	 * @return bean of context
	 */
	public Object resolveVariable(String var) throws XelException {
		Object variable= null;
		/*
		 * First, find the variable, var in JNDI key-value map. If
		 * not found, look for the variable as a sessionBean
		 */
		if(!_vars.isEmpty()){	
			Object JNDI_pattern = _vars.get(var);
			if(JNDI_pattern == null){
				//var not found
			}else{
			variable = JNDIlookup(JNDI_pattern.toString());
			}
		}
		if(variable == null){
			variable = defaultBean(var);
		}
		return variable;
	}
	
	private Object defaultBean(String name){
		Object variable= null;
		
		variable = JNDIlookup(_jndiPrepend+"/"+name+"/local");
		//If not found in local, lookup remote
		if(variable == null){
			variable = JNDIlookup(_jndiPrepend+"/"+name+"/remote");
		}
		return variable;
	}
	
	private Object JNDIlookup(String JNDI_pattern){
		Object obj = null;
		try{
			Context ctx = new InitialContext();
			obj = ctx.lookup(JNDI_pattern);
		}catch (NamingException e)
		{
			//Not found, do nothing
			System.out.println("lookup not found:"+JNDI_pattern); //for debug
		}
		return obj;
	}

}
