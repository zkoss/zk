/* Init.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/08/16  18:10:17 , Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul;

import org.zkoss.jsf.zul.impl.BaseInit;
import org.zkoss.zk.ui.util.Initiator;

/**
 * Init is a JSF component implementation for initial of page <br/>
 *
 * <p>Once this is specified, an instance inside this tag is created and {@link Initiator#doInit} is called
 * before the page is evaluated. Then, {@link Initiator#doAfterCompose} is called
 * after all components are created, and before any event is processed.
 * In additions, {@link Initiator#doFinally} is called
 * after the page has been evaluated. If an exception occurs, {@link Initiator#doCatch}
 * is called.
 *
 * <p>A typical usage: starting a transaction in doInit, rolling back it
 * in {@link Initiator#doCatch} and commit it in {@link Initiator#doFinally}
 * (if {@link Initiator#doCatch} is not called).
 * 
 * <p/>
 * Usage:<br/>
 * <pre>
 * &lt;z:init useClass="my.Init" arg0="hello"/&gt;
 * </pre>
 * 
 * <p/>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.<br/>
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *   
 * @author Dennis.Chen
 *
 */
public class Init extends  BaseInit{
	
	
}
