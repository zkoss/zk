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
import java.io.Writer;
import java.io.IOException;

import org.zkoss.xel.FunctionMapper;

import org.zkoss.util.logging.Log;
import org.zkoss.web.el.ELContexts;
import org.zkoss.web.servlet.dsp.*;

/**
 * The root node for the parsed result.
 *
 * @author tomyeh
 */
class RootNode extends Node implements Interpretation {
//	private static final Log log = Log.lookup(RootNode.class);

	private final FunctionMapper _mapper;

	RootNode(FunctionMapper fm) {
		_mapper = fm;
	}

	//-- Node --//
	void interpret(InterpretContext ic)
	throws javax.servlet.ServletException, IOException {
		if (_children == null)
			return;
		for (Iterator it = _children.iterator(); it.hasNext();) {
			((Node)it.next()).interpret(ic);
		}
	}

	//-- Interpretation --//
	public void interpret(DSPContext dc)
	throws javax.servlet.ServletException, IOException {
		final Writer out = dc.getOut();
		if (out == null)
			throw new IllegalArgumentException("dc.getOut() cannot be null");

		ELContexts.push(dc);
		try {
			interpret(new InterpretContext(dc, _mapper));
/*		} catch (javax.servlet.ServletException ex) {
			log.realCauseBriefly(ex); //in case: ex might be eaten
			throw ex;
		} catch (IOException ex) {
			log.realCauseBriefly(ex); //in case: ex might be eaten
			throw ex;
*/		} finally {
			ELContexts.pop();
		}
	}
}
