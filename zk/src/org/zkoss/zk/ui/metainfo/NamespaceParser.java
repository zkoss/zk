/** NamespaceParser.java.

	Purpose:
		
	Description:
		
	History:
		2:18:08 PM Jul 18, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui.metainfo;

import org.zkoss.idom.Attribute;

/**
 * A namespace parser to handle the different namespace for the attributes
 * @author jumperchen
 * @since 7.0.3
 */
public interface NamespaceParser {

	/**
	 * Checks whether the namespace URI is used for this Namespace Parser.
	 */
	public boolean isMatched(String nsURI);

	/**
	 * Parses the the content with the current namespace parser.
	 * 
	 * @return true to terminal the next namespace parser processing
	 */
	public boolean parse(Attribute attr, ComponentInfo compInfo,
			PageDefinition pgdef) throws Exception;

	/**
	 * Returns the namespace parser's priority, higher is the first.
	 */
	public int getPriority();
}
