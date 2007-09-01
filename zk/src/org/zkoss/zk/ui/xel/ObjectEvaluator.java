/* ObjectEvaluator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 13:39:59     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.xel;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.ObjectResolver;

/**
 * The evaluator allows the developer to access the field/method
 * of the reference object directly.
 *
 * <p>The access of the field and method of the refence object
 * is done by use of {@link ObjectResolver}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ObjectEvaluator extends SimpleEvaluator {
	private FunctionMapper _mapper;

	/**
	 * @param expfcls the class that implements the expression factory.
	 * If null, the default one is used.
	 * @param mapper the function mapper. If null, no function mapper at all.
	 */
	public ObjectEvaluator(Class expfcls, FunctionMapper mapper) {
		super(expfcls);
		_mapper = mapper;
	}

	//Super//
	public FunctionMapper getFunctionMapper(Object ref) {
		return _mapper;
	}
	public VariableResolver getVariableResolver(Object ref) {
		final VariableResolver resolver = super.getVariableResolver(ref);
		return ref != null ? new ObjectResolver(resolver, ref): resolver;
	}
}
