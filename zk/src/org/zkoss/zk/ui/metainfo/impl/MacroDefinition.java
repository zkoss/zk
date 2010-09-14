/* MacroDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 12:31:56     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.*;

/**
 * The macro component definition.
 *
 * @author tomyeh
 * @since 3.0.0
 * @see ComponentDefinitionImpl#newMacroDefinition
 */
public class MacroDefinition extends ComponentDefinitionImpl {
	private final String _macroURI;
	/** Whether it is an inline macro. */
	private final boolean _inline;

	/*package*/ MacroDefinition(LanguageDefinition langdef,
	PageDefinition pgdef, String name,
	Class<? extends Component> cls, String macroURI, boolean inline) {
		super(langdef, pgdef, name, cls);

		if (name == null || cls == null)
			throw new IllegalArgumentException("null");
		if (macroURI == null || macroURI.length() == 0)
			throw new IllegalArgumentException("empty macroURI");

		_macroURI = macroURI;
		_inline = inline;
	}

	public boolean isMacro() {
		return true;
	}
	public String getMacroURI() {
		return _macroURI;
	}
	public boolean isInlineMacro() {
		return _inline;
	}
}
