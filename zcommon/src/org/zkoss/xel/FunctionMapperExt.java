/* FunctionMapperExt.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 17:32:09 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xel;

import java.util.Collection;

/**
 * Implemented with {@link FunctionMapper} to resolve the class.
 * The default evaluator ignores this interface. It is used only with
 * special evaulators such as MVEL and OGNL.
 * In other words, you rarely need to implement this interface.
 * @author tomyeh
 * @since 6.0.0
 */
public interface FunctionMapperExt {
	/** Returns a readonly collection of the logic names of the class
	 * (never null).
	 * Note: it is the name to resolve class, not the real class name.
	 * In other words, it is the logical name maintained by this
	 * function mapper.
	 */
	public Collection<String> getClassNames();
	/** Resolves a class with the specified logic name,
	 * or null if not found.
	 *
	 * <p>Note: not all EL evaluator support {@link #resolveClass}.
	 * JSP 2.0/2.1 EL-based expression factories don't support
	 * this method.
	 * You can check {@link ExpressionFactory#isSupported} for this
	 * support.
	 *
	 * @return the class of the specified logic name.
	 */
	public Class<?> resolveClass(String name) throws XelException;
}
