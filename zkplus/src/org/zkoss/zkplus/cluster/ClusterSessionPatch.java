/* ClusterSessionPatch.java

	Purpose:
		
	Description:
		
	History:
		Jun 27, 2011 4:00:01 PM, Created by jimmy

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkplus.cluster;

import java.util.List;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.util.ExecutionCleanup;

/**
 * <p>
 * This listener is used with Weblogic clustering server 
 * or cloud environment (such as Google App Engine).
 * <p>then you have to add following lines in application's WEB-INF/zk.xml:</p>
 * <pre><code>
 * 	&lt;listener>
 *		&lt;listener-class>org.zkoss.zkplus.cluster.ClusterSessionPatch&lt;/listener-class>
 *	&lt;/listener>
 * </code></pre>
 * @author jimmy
 * @since 5.0.8
 *
 */
public class ClusterSessionPatch implements ExecutionCleanup {

	public void cleanup(Execution exec, Execution parent, List errs)
			throws Exception {
		Session sess = exec.getSession();
		//enforce GAE to write session
		//enforce Weblogic to sync session
		if (sess != null)
			sess.setAttribute(Attributes.ZK_SESSION, 
					sess.getAttribute(Attributes.ZK_SESSION));
	}

}
