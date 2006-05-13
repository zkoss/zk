/* InterpretContext.java

{{IS_NOTE
	$Id: InterpretContext.java,v 1.4 2006/03/09 08:40:18 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 16:59:44     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.impl;

import java.io.Writer;
import javax.servlet.jsp.el.FunctionMapper;

import com.potix.web.servlet.dsp.*;
import com.potix.web.servlet.dsp.action.Action;

/**
 * Holds the context for interpreting an {@link Interpretation}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/03/09 08:40:18 $
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
