/* EJB3VariableInit.java

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

import org.zkoss.util.Maps;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * This is iniator for EJB3VariableResolver
 * <p>
 * This initiator take agruments to initialize JNDI names. First, the variable will be resolved from the key-value pairs. 
 * If not found, the variable will be look up as a sessionBean.
 * </p>
 * <p>Put the init PI as follows:</p>
 * <pre>
 * &lt;?init class="org.zkoss.ejb.EJBVariableResolver" [arg0="prepended_name"] [arg1="bean=a\b\c,bean2=java:\env:a"]?>
 * </pre>
 * <ol>
 * <li>agr0 - The prepended part of JNDI name</li>
 * <li>agr1 - The key-value pairs for JNDI name and its corresponding variable name</li>
 * </ol>
 * For example:
 * By default, session beans will bind to JNDI in the form ejbName/remote for remote interfaces and ejbName/local in the case of local interfaces. When the EJBs are deployed in an .ear file, the default jndi binding will be prepended by the name of the .ear file.
 * As a result, if the ear file name is foo.ear, the initiator should be declared as:
 * 
 * <pre>
 * &lt;?init class="org.zkoss.ejb.EJBVariableResolver" arg0="foo" ?>
 * </pre>
 * 
 * If you define your own jdni binding, you should write the variable name and jdni binding as key-value pairs as</br>
 * a=custom/MySession
 * 
 * <pre>
 * &lt;?init class="org.zkoss.ejb.EJBVariableResolver" arg0="foo" arg1="a=custom/MySession" ?>
 * </pre>
 * 
 * @author Jeff
 *
 */
public class EJB3VariableInit implements Initiator {

	public void doAfterCompose(Page arg0) throws Exception {
		//do nothing
	}

	public boolean doCatch(Throwable arg0) throws Exception {
		//do nothing
		return false;
	}

	public void doFinally() throws Exception {
		//do nothing
	}

	public void doInit(Page page, Object[] args) throws Exception {
		String prepend = "";
		Map jndiMap = new HashMap();
		if(args.length>0)
			prepend = (String)args[0];
		if(args.length>1)
			Maps.parse(jndiMap , (String)args[1], ',', '=');
		EJB3VariableResolver evr = new EJB3VariableResolver(prepend,jndiMap);
		page.addVariableResolver(evr);
	}

}
