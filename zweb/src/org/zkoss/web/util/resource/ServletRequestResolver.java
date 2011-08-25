/* ServletRequestResolver.java

	Purpose:
		
	Description:
		
	History:
		Sun Mar 27 01:58:07 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.util.resource;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;

/**
 * Used to resolve the variables defined in the request
 * (such as DSP context and ZK's execution).
 * It is designed to work {@link org.zkoss.util.resource.Labels},
 * such that XEL expressions specified in labels could reference
 * to the variables defined in the request.
 *
 * @author tomyeh
 * @since 5.0.7
 */
public class ServletRequestResolver implements VariableResolver, java.io.Serializable {
	public ServletRequestResolver() {
	}

	//VariableResolver//
	public Object resolveVariable(String name) throws XelException {
		final RequestContext jc = RequestContexts.getCurrent();
		if (jc != null) {
			final VariableResolver parent = jc.getVariableResolver();
			if (parent != null)
				return parent.resolveVariable(name);
		}
		return null;
	}
}
