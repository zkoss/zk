/* RootNode.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 12:35:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.util.Iterator;
import java.io.IOException;

import org.zkoss.util.logging.Log;

import org.zkoss.xel.FunctionMapper;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.web.servlet.dsp.*;

/**
 * The root node for the parsed result.
 *
 * @author tomyeh
 */
class RootNode extends Node implements Interpretation {
//	private static final Log log = Log.lookup(RootNode.class);

	private FunctionMapper _mapper;

	RootNode() {
	}

	void setFunctionMapper(FunctionMapper mapper) {
		_mapper = mapper;
	}

	//-- Node --//
	void interpret(InterpretContext ic)
	throws DspException, IOException {
		if (_children == null)
			return;

		ic.init(_mapper);
		for (Iterator it = _children.iterator(); it.hasNext();) {
			((Node)it.next()).interpret(ic);
		}
	}

	//-- Interpretation --//
	public void interpret(DspContext dc)
	throws DspException, IOException {
		RequestContexts.push(dc);
		try {
			interpret(new InterpretContext(dc));
		} finally {
			RequestContexts.pop();
		}
	}
}
