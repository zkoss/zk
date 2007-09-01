/* TextInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep  1 01:14:10     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.xel.ExValue;
import org.zkoss.zk.ui.xel.Evaluator;

/**
 * Represents a text.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class TextInfo {
	private final ExValue _text;

	public TextInfo(String text) {
		_text = text != null ? new ExValue(text, String.class): null;
	}

	/** Returns the raw value (text).
	 */
	public String getRawValue() {
		return _text.getRawValue();
	}

	/** Returns the value after evaluation.
	 */
	public String getValue(Evaluator eval, Page page) {
		return _text != null ? (String)_text.getValue(eval, page): null;
	}
	/** Returns the value after evaluation.
	 */
	public String getValue(Evaluator eval, Component comp) {
		return _text != null ? (String)_text.getValue(eval, comp): null;
	}
}
