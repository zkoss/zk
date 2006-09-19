/* ServletLabelResovler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 12 13:58:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ELException;

import org.zkoss.util.resource.Labels;
import org.zkoss.web.el.ELContexts;
import org.zkoss.web.el.ELContext;

/**
 * Used with {@link org.zkoss.util.resource.Labels} to resolve
 * EL expressions in labels.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ServletLabelResovler implements VariableResolver {
	private final VariableResolver _parent;
	public ServletLabelResovler() {
		final ELContext jc = ELContexts.getCurrent();
		_parent = jc != null ? jc.getVariableResolver(): null;
	}
	public Object resolveVariable(String name) throws ELException {
		final Object o = Labels.getLabel(name);
		return o != null ? o:
			_parent != null ? _parent.resolveVariable(name): null;
	}
}
