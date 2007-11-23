/* ObjectResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 14:44:09     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import org.zkoss.lang.Classes;

/**
 * Resolves the specified object's members as variables.
 *
 * @author tomyeh
 */
public class ObjectResolver implements VariableResolver {
	/** The parent resolver. */
	protected final VariableResolver _parent;
	/** The variable. */
	protected final Object _ref;

	/** Constructs a resolver. */
	public ObjectResolver() {
		this(null, null);
	}
	/** Constructs a resolver with a parent.
	 * @param parent the parent resolver (null means ignored).
	 */
	public ObjectResolver(VariableResolver parent) {
		this(parent, null);
	}
	/** Constructs a resolver with a parent and an object.
	 * @param parent the parent resolver (null means ignored).
	 * @param ref the object whose members are considered as variables
	 * (null means ignored)
	 */
	public ObjectResolver(VariableResolver parent, Object ref) {
		_parent = parent;
		_ref = ref;
	}
	/** Constructs a resolver with an object.
	 * @param ref the object whose members are considered as variables
	 * (null means ignored)
	 */
	public ObjectResolver(Object ref) {
		this(null, ref);
	}

	//-- VariableResolver --//
	public Object resolveVariable(String name) throws XelException {
		if (_ref != null) {
			final Class refcls = _ref.getClass();
			AccessibleObject acs = null;
			try {
				acs = Classes.getAccessibleObject(refcls, name, null,
					Classes.B_GET|Classes.B_PUBLIC_ONLY);
				return 	acs instanceof Method ?
					((Method)acs).invoke(_ref, null): ((Field)acs).get(_ref);
			} catch (NoSuchMethodException ex) {
				//IGNORED
			} catch (Exception ex) {
				throw new XelException("Failed to invoke "+acs+" upon "+_ref);
			}
		}
		return _parent != null ? _parent.resolveVariable(name): null;
	}
}
