/* EvalRefStub.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep  6 12:34:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Used to simply the serializable info that contains {@link EvaluatorRef}.
 * For example, {@link EventHandler} and {@link VariablesInfo} and so on.
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class EvalRefStub implements java.io.Serializable {
	/*package*/ transient EvaluatorRef _evalr;

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();
		ComponentInfo.writeEvalRef(s, _evalr);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		_evalr = ComponentInfo.readEvalRef(s);
	}
}
