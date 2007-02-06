/* BSFManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 16:07:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf;

import java.util.Iterator;

import org.apache.bsf.BSFDeclaredBean;

/**
 * ZK's extension of BSF manager.
 *
 * @author tomyeh
 */
public class BSFManager extends org.apache.bsf.BSFManager {
	/** Returns the declared variable.
	 */
	public Object getDeclaredBean(String name) {
		for (Iterator it = declaredBeans.iterator(); it.hasNext();) {
			final BSFDeclaredBean db = (BSFDeclaredBean)it.next();
			if (db.name.equals(name))
				return db.bean;
		}
        return null;
	}
}
