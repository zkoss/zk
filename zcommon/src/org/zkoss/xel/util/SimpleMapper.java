/* SimpleMapper.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  4 23:09:16     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import java.util.Collection;

import org.zkoss.util.DualCollection;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;

/**
 * A simmple function mapper.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class SimpleMapper extends TaglibMapper {
	private FunctionMapper _parent;
	public SimpleMapper() {
	}
	public SimpleMapper(FunctionMapper parent) {
		_parent = parent;
	}

	/** Returns the parent mapper, or null if no parent.
	 */
	public FunctionMapper getParent() {
		return _parent;
	}
	/** Sets the parent mapper.
	 *
	 * @param parent the parent mapper, or null if no parent.
	 */
	public void setParent(FunctionMapper parent) {
		_parent = parent;
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		Function m = super.resolveFunction(prefix, name);
		return m != null ? m:
			_parent != null ? _parent.resolveFunction(prefix, name): null;
	}
	public Collection getClassNames() {
		return combine(super.getClassNames(),
			_parent != null ? _parent.getClassNames(): null);
	}
	public Class resolveClass(String name) {
		Class m = super.resolveClass(name);
		return m != null ? m:
			_parent != null ? _parent.resolveClass(name): null;
	}
	private static Collection combine(Collection first, Collection second) {
		return DualCollection.combine(
			first != null && !first.isEmpty() ? first: null,
			second != null && !second.isEmpty() ? second: null);
	}
}
