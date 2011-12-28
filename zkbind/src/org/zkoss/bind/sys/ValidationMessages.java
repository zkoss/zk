/* ValidationMessages.java

	Purpose:
		
	Description:
		
	History:
		2011/12/26 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import org.zkoss.zk.ui.Component;

/**
 * To provide the message binding between validator and binder. 
 * @author dennis
 *
 */
public interface ValidationMessages{

	/**
	 * clear validation message of component
	 */
	void clearMessages(Component comp,String attr);
	
	/**
	 * clear validation message of component
	 */
	void clearMessages(Component comp);
	
	/**
	 * get validation message of component
	 * @return null if no message in component
	 */
	String[] getMessages(Component comp,String attr);
	
	/**
	 * get validation message of component
	 * @return null if no message in component
	 */
	String[] getMessages(Component comp);
	
	/**
	 * set validation message to component, it will replace previous messages
	 */
	void setMessages(Component comp, String attr, String[] messages);
	
	/**
	 * add validation message to component
	 */
	void addMessages(Component comp, String attr, String[] messages);
}
