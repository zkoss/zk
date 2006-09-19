/* ComponentDefinitionMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  4 20:20:36     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A map of component definitions.
 * Used with {@link PageDefinition#getComponentDefinitionMap}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ComponentDefinitionMap implements java.io.Serializable {
	/** A map of component definition defined in this page. */
	private transient Map _compdefs;
	/** Map(String clsnm, ComponentDefinition compdef). */
	private transient Map _compdefsByClass;

	/** Returns a collection of component definitions, {@link ComponentDefinition},
	 *  defined in this map.
	 */
	public Collection getAll() {
		return _compdefs != null ? _compdefs.values(): Collections.EMPTY_LIST;
	}
	/** Adds a component definition to this map.
	 *
	 * <p>Thread safe.
	 */
	public void add(ComponentDefinition compdef) {
		if (compdef == null)
			throw new IllegalArgumentException("null");

		if (_compdefs == null) {
			synchronized (this) {
				if (_compdefs == null) {
					_compdefsByClass = new HashMap(5);
					_compdefs = new HashMap(5);
				}
			}
		}
		synchronized (_compdefs) {
			_compdefs.put(compdef.getName(), compdef);
		}

		final Object implcls = compdef.getImplementationClass();
		synchronized (_compdefsByClass) {
			if (implcls instanceof Class)
				_compdefsByClass.put(((Class)implcls).getName(), compdef);
			else //String
				_compdefsByClass.put(implcls, compdef);
		}
	}
	/** Returns the component definition of the specified name, or null if not
	 * not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 */
	public ComponentDefinition get(String name) {
		if (_compdefs == null) return null;

		synchronized (_compdefs) {
			return (ComponentDefinition)_compdefs.get(name);
		}
	}
	/** Returns the component definition of the specified class, or null if not
	 * found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 */
	public ComponentDefinition get(Class cls) {
		if (_compdefsByClass == null) return null;

		synchronized (_compdefsByClass) {
			for (; cls != null; cls = cls.getSuperclass()) {
				final ComponentDefinition compdef =
					(ComponentDefinition)_compdefsByClass.get(cls.getName());
				if (compdef != null) return compdef;
			}
		}
		return null;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_compdefs != null ? _compdefs.values(): null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final Collection c = (Collection)s.readObject();
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				add((ComponentDefinition)it.next());
	}
}
