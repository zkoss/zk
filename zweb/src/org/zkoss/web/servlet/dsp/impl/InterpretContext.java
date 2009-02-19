/* InterpretContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 16:59:44     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.io.Writer;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.util.SimpleXelContext;

import org.zkoss.web.servlet.dsp.*;
import org.zkoss.web.servlet.dsp.action.Action;

/**
 * Holds the context for interpreting an {@link Interpretation}.
 *
 * @author tomyeh
 */
class InterpretContext {
	final DspContext dc;
	final InterpretResolver resolver;
	XelContext xelc;
	/** The action being processing, or null if no such action. */
	Action action;

	/** Constructs an interpret context.
	 */
	InterpretContext(DspContext dc) {
		this.dc = dc;
		this.resolver = new InterpretResolver(dc.getVariableResolver());
	}
	void init(FunctionMapper mapper) {
		if (this.xelc != null)
			throw new IllegalArgumentException();
		this.xelc = new SimpleXelContext(this.resolver, mapper);
	}
}
