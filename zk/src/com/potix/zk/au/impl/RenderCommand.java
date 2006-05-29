/* RenderCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 14:24:42     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import java.util.Set;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ext.Render;
import com.potix.zk.au.AuRequest;

/**
 * Used only by {@link AuRequest} to implement the render
 * relevant command.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class RenderCommand extends AuRequest.Command {
	public RenderCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);

		final Set items = Commands.convertToItems(request);
		((Render)comp).renderItems(items);
	}
}
