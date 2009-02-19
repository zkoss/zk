/* DetachDesktopCleanup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 18 12:35:59     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import java.util.Collection;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * Detaches when desktop is cleaned up.
 * Used with B30-1753712.zul.
 *
<pre><code>
&lt;listener&gt;
  &lt;listener-class&gt;org.zkoss.zkdemo.test2.DetachDesktopCleanup&lt;/listener-class&gt;
&lt;/listener&gt;
</pre></code>
 *
 * @author tomyeh
 */
public class DetachDesktopCleanup implements DesktopCleanup {
	public static Component reuse;

	public void cleanup(Desktop desktop) throws Exception {
		final Collection c = desktop.getPages();
		if (c.size() > 0)
		reuse = ((Page)c.iterator().next()).getFellow("reuse");
		reuse.detach();
	}
}
