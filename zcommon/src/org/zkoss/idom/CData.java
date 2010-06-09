/* CData.java


	Purpose: 
	Description: 
	History:
	2001/10/22 20:52:22, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.w3c.dom.CDATASection;
import org.zkoss.idom.impl.*;

/**
 * The iDOM CDATA.
 *
 * @author tomyeh
 * @see Text
 */
public class CData extends AbstractTextual implements CDATASection {
	/** Constructor.
	 */
	public CData(String text) {
		super(text);
	}
	/** Constructor.
	 */
	public CData() {
	}

	//-- AbstractTextual --//
	protected void checkText(String text) {
		Verifier.checkCData(text, getLocator());
	}

	//-- Item --//
	public final String getName() {
		return "#cdata-section";
	}

	//-- Node --//
	public final short getNodeType() {
		return CDATA_SECTION_NODE;
	}
}
