/* InterpretContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 16:59:44     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.io.Writer;
import org.zkoss.xel.FunctionMapper;

import org.zkoss.web.servlet.dsp.*;
import org.zkoss.web.servlet.dsp.action.Action;

/**
 * Holds the context for interpreting an {@link Interpretation}.
 *
 * @author tomyeh
 */
class InterpretContext {
	final DSPContext dc;
	final InterpretResolver resolver;
	final FunctionMapper mapper;
	/** The action being processing, or null if no such action. */
	Action action;

	/** Constructs an interpret context.
	 */
	InterpretContext(DSPContext dc, FunctionMapper mapper) {
		this.dc = dc;
		this.resolver = new InterpretResolver(dc.getVariableResolver());
		this.mapper = mapper;
	}
}
