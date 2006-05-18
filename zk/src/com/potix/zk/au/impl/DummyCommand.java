/* DummyCommand.java

{{IS_NOTE
	$Id: DummyCommand.java,v 1.2 2006/02/27 03:54:45 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 23:44:45     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import com.potix.zk.au.AuRequest;

/**
 * A dummy command that does nothing but triggers an execution
 * to prcess all pending request, if any.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:45 $
 */
public class DummyCommand extends AuRequest.Command {
	public DummyCommand(String evtnm, boolean skipIfEverError) {
		super(evtnm, skipIfEverError);
	}
	protected void process(AuRequest request) {
	}
}
