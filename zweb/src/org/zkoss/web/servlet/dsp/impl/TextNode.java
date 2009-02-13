/* TextNode.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 14:11:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.web.servlet.dsp.DspException;

/**
 * Represents a node holding a plain text.
 *
 * @author tomyeh
 */
class TextNode extends Node {
	private final String _text;
	TextNode(String text) {
		_text = text;
	}

	/** Returns the text.
	 * @since 3.0.0
	 */
	public String getText() {
		return _text;
	}

	//-- super --//
	void interpret(InterpretContext ic)
	throws DspException, IOException {
		ic.dc.getOut().write(_text);
	}
	void addChild(Node node) {
		throw new IllegalStateException("No child allowed");
	}

	public String toString() {
		return "TextNode["+
			(_text.length() > 20 ? _text.substring(0, 20): _text)+']';
	}
}
