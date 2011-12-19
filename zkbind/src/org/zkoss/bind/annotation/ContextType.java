/* ContextType.java

	Purpose:
		
	Description:
		
	History:
		2011/12/15 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.util.Map;

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
 */
public enum ContextType {
	/**  
	 * The current bind context, which type is {@link BindContext}
	 */
	BIND_CONTEXT,
	/**  
	 * The current binder, which type is {@link Binder}
	 */
	BINDER,
	/**  
	 * The current execution, which type is {@link Execution}
	 */
	EXECUTION,
	/**  
	 * The current component, which type is subclass of the {@link Component} 
	 */
	COMPONENT,
	/**  
	 * The space owner of the current component, which type is {@link IdSpace} 
	 */
	SPACE_OWNER,
	/**  
	 * The page of the current component, which type is {@link Page} 
	 */
	PAGE,
	/**  
	 * The desktop of the current component, which type is {@link Desktop} 
	 */
	DEKSTOP,
	/**  
	 * The session, which type is {@link Session} 
	 */
	SESSION,
	/**  
	 * The web application, which type is {@link WebApp} 
	 */
	APPLICATION,
	
	/**  
	 * The implicit requestScope object, which type is {@linkplain Map} 
	 */
	REQUEST_SCOPE,
	/**  
	 * The implicit componentScope object of the current component, which type is {@linkplain Map} 
	 */
	COMPONENT_SCOPE,
	/**  
	 * The implicit spaceScope object of the current component, which type is {@linkplain Map} 
	 */
	SPACE_SCOPE,
	/**  
	 * The implicit pageScope object of the current component, which type is {@linkplain Map} 
	 */
	PAGE_SCOPE,
	/**  
	 * The implicit desktopScope object of the current component, which type is {@linkplain Map} 
	 */
	DESKTOP_SCOPE,
	/**  
	 * The implicit sessionScope object, which type is {@linkplain Map} 
	 */
	SESSION_SCOPE,
	/**  
	 * The implicit applicationScope object, which type is {@linkplain Map} 
	 */
	APPLICATION_SCOPE
}
