/* ComponentCtrl.java

{{IS_NOTE
	$Id: ComponentCtrl.java,v 1.5 2006/04/10 09:19:40 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:06:56     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import java.util.Iterator;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.metainfo.ComponentDefinition;

/**
 * An addition interface to {@link Component} that is used for
 * implementation.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/04/10 09:19:40 $
 */
public interface ComponentCtrl {
	/** Returns the namespace to store variables and functions belonging
	 * to the ID space of this component.
	 */
	public Namespace getNamespace();

	/** Sets the component definition.
	 */
	public void setDefinition(ComponentDefinition compdef);

	//-- event utilities --//
	/** Returns whether the event listener is available.
	 * @param asap whether to check only ASAP listener.
	 * See {@link Component#addEventListener} for more description.
	 */
	public boolean isListenerAvailable(String evtnm, boolean asap);
	/** Returns an iterator for iterating listener for the specified event.
	 */
	public Iterator getListenerIterator(String evtnm);
}
