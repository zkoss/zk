/* AuMetaCommand.java

{{IS_NOTE
	$Id: AuMetaCommand.java,v 1.2 2006/02/27 03:54:43 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 10:39:48     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;

/**
 * A response to invoke a method of the metainfo of a component.
 *
 * <p>Note: to have a metainfo, a component must generate and handles
 * {@link AuInit} properly.
 *
 * <p>data[0]: the uuid of the component/page as the parent<br>
 * data[1]: the method name<br>
 * data[2..n]: optional arguments
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:43 $
 */
public class AuMetaCommand extends AuResponse {
	/**
	 * @param args optional arguments. Ignored if null.
	 */
	public AuMetaCommand(Component comp, String method, String[] args) {
		super("meta", comp, toData(comp.getUuid(), method, args));
	}
	/** Constructs a meta command without argument. */
	public AuMetaCommand(Component comp, String method) {
		super("meta", comp, new String[] {comp.getUuid(), method});
	}
	/** Constructs a meta command with single argument. */
	public AuMetaCommand(Component comp, String method, String arg) {
		super("meta", comp, new String[] {comp.getUuid(), method, arg});
	}
	private static String[] toData(String uuid, String method, String[] args) {
		if (args == null || args.length == 0)
			return new String[] {uuid, method};

		final String[] as = new String[args.length + 2];
		as[0] = uuid;
		as[1] = method;
		for (int j = args.length; --j >= 0;)
			as[j + 2] = args[j];
		return as;
	}
}
