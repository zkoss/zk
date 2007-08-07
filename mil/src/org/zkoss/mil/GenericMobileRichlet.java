/* GenericMobileRichlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 20:45:13     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mil;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * Defines a genric richlet for ZK Mobile. Developers can use it as a skeleton to implement
 * an application-specific ZK Mobile richlet.
 *
 * @author henrichen
 */
abstract public class GenericMobileRichlet extends GenericRichlet {
	/** Returns the language defintion that this richlet belongs to.
	 * Don't return null.
	 *
	 * <p> It is called when creating a new page for this richlet to serve.
	 *
	 * <p>Default: return the language definition called "mil".
	 */
	public LanguageDefinition getLanguageDefinition() {
		return LanguageDefinition.lookup("mil");
	}
}
