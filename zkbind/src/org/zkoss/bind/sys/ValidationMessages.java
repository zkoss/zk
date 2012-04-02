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
 * @since 6.0.0
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
	 * get validation messages of a component and special attribute
	 * @return null if no message in component and attribute
	 */
	String[] getMessages(Component comp,String attr);
	
	/**
	 * get validation messages of a component
	 * @return null if no message of component
	 */
	String[] getMessages(Component comp);
	
	/**
	 * get all validation messages
	 * @return null if no messages
	 * @since 6.0.1
	 */
	String[] getMessages();
	
	/**
	 * get validation message of component and a special key
	 * @return null if no message of key
	 */
	String[] getKeyMessages(Component comp,String key);
	
	/**
	 * get validation message of a special key
	 * @return null if no message of key
	 */
	String[] getKeyMessages(String key);
	
	/**
	 * set validation messages to component, it will replace previous messages
	 * @param comp the component refers to the messages
	 * @param attr the attr refers to the messages
	 * @param key the custom key refers to this messages, nullable
	 * @param messages the messages
	 */
	void setMessages(Component comp, String attr, String key, String[] messages);
	
	/**
	 * add validation messages to component
	 * @param comp the component refers to the messages
	 * @param attr the attr refers to the messages
	 * @param key the custom key refers to this messages, nullable
	 * @param messages the messages
	 */
	void addMessages(Component comp, String attr, String key, String[] messages);
}
