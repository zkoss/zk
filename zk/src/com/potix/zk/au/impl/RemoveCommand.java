/* RemoveCommand.java

{{IS_NOTE
	$Id: RemoveCommand.java,v 1.3 2006/02/27 03:54:46 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 14:24:50     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the remove command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:54:46 $
 */
public class RemoveCommand extends AuRequest.Command {
	public RemoveCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp != null) comp.detach();
	}
}
