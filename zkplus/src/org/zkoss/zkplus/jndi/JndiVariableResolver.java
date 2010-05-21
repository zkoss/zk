/* JndiVariableResolver.java

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
package org.zkoss.zkplus.jndi;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.Maps;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

/**
 * JndiVariableResolver, a jndi variableResolver 
 * @author Jeff
 *
 */
public class JndiVariableResolver implements VariableResolver {
	
	private static final Log log = Log.lookup(JndiVariableResolver.class);
	
	private String _jndiPrepend=null;
	
	private Map _jndiMapping = new HashMap();
	
	/**
	 * This constructor take agruments to initialize JNDI names. 
	 * <ul>
	 * <li>prepend - The prepended part of JNDI name</li>
	 * <li>mapping - The key-value pairs for JNDI name and its corresponding variable name</li>
	 * </ul>
	 * 
	 * The variable will be resolved in following priority
	 * <ol>
	 * <li>java:comp/env</li>
	 * <li>java:comp</li>
	 * <li>java:</li>
	 * <li>The variable will be look up as a sessionBean with prepend.</li>
	 * <li>The key-value pairs which is defined by mapping</li>
	 * </ol>
	 * </p>
	 * 
	 * <p>
	 * For example:
	 * </p>
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
	 * @param prepend prepended part of JNDI name
	 * @param mapping key-value pairs for JNDI name and its corresponding variable name
	 */
	public JndiVariableResolver(String prepend, String mapping) {
		_jndiMapping= new HashMap();
		Maps.parse(_jndiMapping , (String)mapping, ',', '=');
		_jndiPrepend = prepend;
	}
	
	public JndiVariableResolver(){
		//Do Nothing
	}

	/**
	 * Get object from JNDI binding
	 * @param var JNDI binding name
	 * @return bean of context
	 */
	public Object resolveVariable(String var) throws XelException {
		Object variable= null;
		/*
		 * First, find the variable, var in JNDI key-value map. If
		 * not found, look for the variable as a sessionBean
		 */
		
		variable = jndiLookup("java:comp/env/"+var);
		
		if(variable == null){
			variable = jndiLookup("java:comp/"+var);
		}
		if(variable == null){
			variable = jndiLookup("java:/"+var);
		}
		if(variable == null){
			variable = defaultBean(var);
		}
		if(!_jndiMapping.isEmpty()&&variable==null){	
			Object jndiPattern = _jndiMapping.get(var);
			if(jndiPattern != null){
				variable = jndiLookup(jndiPattern.toString());
			}
		}
		return variable;
	}
	
	private Object defaultBean(String name){
		Object variable= null;
		
		variable = jndiLookup(_jndiPrepend+"/"+name+"/local");
		//If not found in local, lookup remote
		if(variable == null){
			variable = jndiLookup(_jndiPrepend+"/"+name+"/remote");
		}
		return variable;
	}
	
	private Object jndiLookup(String jndiPattern){
		Object obj = null;
		try{
			Context ctx = new InitialContext();
			obj = ctx.lookup(jndiPattern);
		}catch (NamingException ex)
		{
			//Not found, logging
			if(log.debugable()){
				log.debug("JNDI binding not found: "+ex);
			}
		}
		return obj;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_jndiMapping == null) ? 0 : _jndiMapping.hashCode());
		result = prime * result
				+ ((_jndiPrepend == null) ? 0 : _jndiPrepend.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JndiVariableResolver))
			return false;
		JndiVariableResolver other = (JndiVariableResolver) obj;
		if (_jndiMapping == null) {
			if (other._jndiMapping != null)
				return false;
		} else if (!_jndiMapping.equals(other._jndiMapping))
			return false;
		if (_jndiPrepend == null) {
			if (other._jndiPrepend != null)
				return false;
		} else if (!_jndiPrepend.equals(other._jndiPrepend))
			return false;
		return true;
	}
}
