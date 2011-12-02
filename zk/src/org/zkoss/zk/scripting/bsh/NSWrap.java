/* NSWrap.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 11:58:52 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.scripting.bsh;

import bsh.NameSpace;
import org.zkoss.zk.ui.ext.Scope;

/** Non-serializabe Namespace wrapper.
 * Used to prevent to serialize NameSpace directly.
 * 
 * @author tomyeh
 */
/*package*/ class NSWrap {
	protected NameSpace _bshns;
	/*package*/ static NSWrap getInstance(NameSpace ns) {
		if (ns instanceof BSHInterpreter.NS) return new NSWrapSR(ns);
		return new NSWrap(ns);
	}
	protected NSWrap(NameSpace ns) {
		_bshns = ns;
	}
	public NSWrap() {
	}
	/** Returns the associated NameSpace. */
	public NameSpace unwrap(Scope ns) {
		return _bshns;
	}
}
