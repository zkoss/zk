/* Text.java

{{IS_NOTE

Purpose: 
Description: 
History:
C2001/10/22 20:48:21, reate, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.zkoss.idom.impl.*;

/**
 * The iDOM Text.
 *
 * @author tomyeh
 * @see CData
 */
public class Text extends AbstractTextual implements org.w3c.dom.Text {
	/** Constructor.
	 */
	public Text(String text) {
		super(text);
	}
	/** Constructor.
	 */
	public Text() {
	}

	//-- AbstractTextual --//
	/**
	 * Always returns true to denote it allows to be coalesced
	 * with its siblings with the same type (class).
	 */
	public final boolean isCoalesceable() {
		return true;
	}

	//-- Item --//
	public final String getName() {
		return "#text";
	}

	//-- Node --//
	public final short getNodeType() {
		return TEXT_NODE;
	}
}
