/* RhinoEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 17:13:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf.engines.javascript;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.engines.javascript.JavaScriptEngine;
import org.mozilla.javascript.Context;

/**
 * ZK's extension of Rhino engine.
 *
 * @author tomyeh
 */
public class RhinoEngine extends JavaScriptEngine {
    public void declareBean(BSFDeclaredBean bean) throws BSFException {
		final boolean ctxRequired = !(bean.bean instanceof Number) &&
		!(bean.bean instanceof String) && !(bean.bean instanceof Boolean);

		if (ctxRequired) {
			Context.enter();
			try {
				super.declareBean(bean);
			} finally {
				Context.exit();
			}
		} else {
			super.declareBean(bean);
		}
	}
}
