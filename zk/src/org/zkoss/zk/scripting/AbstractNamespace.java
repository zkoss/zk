/* AbstractNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 18:23:27     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.zkoss.util.logging.Log;

/**
 * A skeletal class providing the implementation of {@link #backupVariable},
 * {@link #restoreVariables}.
 * @author tomyeh
 */
abstract public class AbstractNamespace implements Namespace {
	private static final Log log = Log.lookup(AbstractNamespace.class);

	/** A stack of backup-ed variables List(Map(String name, Object val)). */
	private List _backups;

	protected AbstractNamespace() {
	}

	public void backupVariable(String name, boolean newBlock) {
		if (newBlock || _backups == null || _backups.isEmpty()) {
			if (_backups == null) _backups = new LinkedList();
			_backups.add(0, new HashMap());
		}
		if (name != null) {
			final Map map = (Map)_backups.get(0);
			map.put(name, getVariable(name, true));
		}
	}
	public void restoreVariables() {
		if (_backups == null || _backups.isEmpty()) {
			log.warning("restore without any block of backup-ed variables, "+this);
		} else {
			final Map map = (Map)_backups.remove(0);
			for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object val = me.getValue();
				//if (D.ON && log.finerable()) log.finer("Restore "+name+"="+val);
				if (val != null) setVariable(name, val, true);
				else unsetVariable(name);
			}
		}
	}
}
