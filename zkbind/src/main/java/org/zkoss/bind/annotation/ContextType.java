/* ContextType.java

	Purpose:
		
	Description:
		
	History:
		2011/12/15 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;

/**
 * To define the context object type for {@link ContextParam}
 * @author dennis
 * @see ContextParam
 * @since 6.0.0
 */
public enum ContextType {
	/**  
	 * The current bind context, the type is {@link BindContext}
	 */
	BIND_CONTEXT,
	/**  
	 * The current binder, the type is {@link Binder}
	 */
	BINDER,
	/**  
	 * The current execution, the type is {@link Execution}
	 */
	EXECUTION,
	/**  
	 * The current component, the type is subclass of the {@link Component} 
	 */
	COMPONENT,
	/**  
	 * The space owner of the current component, the type is {@link IdSpace} 
	 */
	SPACE_OWNER,
	/**  
	 * The view of binder, the type is {@link Component}  
	 */
	VIEW,
	/**  
	 * The page of the current component, the type is {@link Page} 
	 */
	PAGE,
	/**  
	 * The desktop of the current component, the type is {@link Desktop} 
	 */
	DESKTOP,
	/**  
	 * The session, the type is {@link Session} 
	 */
	SESSION,
	/**  
	 * The web application, the type is {@link WebApp} 
	 */
	APPLICATION,
	/**
	 * The trigger event of a command
	 * @since 6.0.1
	 */
	TRIGGER_EVENT,
	/**
	 * The command name
	 * @since 6.0.1
	 */
	COMMAND_NAME
}
