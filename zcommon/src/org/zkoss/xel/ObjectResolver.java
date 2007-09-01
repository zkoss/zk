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
package org.zkoss.xel;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;

import org.zkoss.lang.Classes;

/**
 * Resolves the specified object's members as variables.
 *
 * @author tomyeh
 */
public class ObjectResolver implements VariableResolver {
	/** The parent resolver. */
	protected VariableResolver _parent;
	/** The object whose memember are considered as variable. */
	protected Object _ref;

	/** Constructs a resolver. */
	public ObjectResolver() {
	}
	/** Constructs a resolver with a parent.
	 * @param parent the parent resolver (null means ignored).
	 */
	public ObjectResolver(VariableResolver parent) {
		_parent = parent;
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

	/** Sets the parent variable resolver.
	 * @param parent the parent resolver (null means ignored).
	 * @since 3.0.0
	 */
	public void setParent(VariableResolver parent) {
		_parent = parent;
	}
	/** Returns the parent variable resolver, or null if no parent.
	 */
	public VariableResolver getParent() {
		return _parent;
	}

	/** Sets the reference object.
	 * @param ref the object whose members are considered as variables
	 * (null means ignored)
	 * @since 3.0.0
	 */
	public void setReference(Object ref) {
		_ref = ref;
	}
	/** Returns the reference object, or null if no such object.
	 * @since 3.0.0
	 */
	public Object getReference() {
		return _ref;
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
				throw XelException.Aide.wrap(ex, "Failed to invoke "+acs+" upon "+_ref);
			}
		}
		return _parent != null ? _parent.resolveVariable(name): null;
	}
}
