/* FailoverManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 28 14:39:26     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

/**
 * Represents a class that is able to handle fail-over in the
 * application specific way.
 *
 * <p>Desktops, pages and components are serializables, so you can use
 * the clustering feature supported by the Web server without implementing
 * this interface. In other words, you need to implement this interface
 * only if you want to provide an application-specific way to re-create
 * back.
 *
 * <p>If you want to use the Web server's clustering feature,
 * what you need to do is to specify {@link org.zkoss.zk.ui.http.SerializableUiFactory}
 * as the UI factory in zk.xml as follows (and forget {@link FailoverManager}).
 *
 * <pre><code>
&lt;system-config&gt;
 &lt;ui-factory-class&gt;org.zkoss.zk.ui.http.SerializableUiFactory&lt;/ui-factory-class&gt;
&lt;/system-config&gt;
</code></pre>
 *
 * @author tomyeh
 */
public interface FailoverManager {
}
