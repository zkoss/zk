/* Richlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  5 11:56:22     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

/**
 * A richlet must implement this interface.
 *
 * <p>To activate it, it must be added to {@link org.zkoss.ui.util.Configuration}
 * by use of {@org.zkoss.ui.util.Configuration#addRichlet}, or specify
 * &lt;richlet&gt; in zk.xml.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Richlet {
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being placed into service.
	 */
	public void init(RichletConfig config);
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being taken out of service.
	 */
	public void destroy();
}
