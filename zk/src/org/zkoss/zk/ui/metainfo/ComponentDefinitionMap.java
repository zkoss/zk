/* ComponentDefinitionMap.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep  4 20:20:36     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
 * Used with {@link PageDefinition#getComponentDefinitionMap}
 * and {@link LanguageDefinition}.
 *
 * <p>It is thread-safe (since it is used in {@link LanguageDefinition}).
 *
 * @author tomyeh
 */
public class ComponentDefinitionMap
implements Cloneable, java.io.Serializable {
	/** A map of component definition defined in this page. */
	private transient Map<String, ComponentDefinition> _compdefs;
	/** Map(String clsnm, ComponentDefinition compdef). */
	private transient Map<String, ComponentDefinition> _compdefsByClass;
	/** Whether the element name is case-insensitive. */
	private final boolean _ignoreCase;

	/** Constructor.
	 */
	public ComponentDefinitionMap(boolean ignoreCase) {
		_ignoreCase = ignoreCase;
	}

	/** Returns whether the component names are case-insensitive.
	 */
	public boolean isCaseInsensitive() {
		return _ignoreCase;
	}


	/** Returns a readonly collection of the names (String)
	 * of component definitions defined in this map.
	 */
	public Collection<String> getNames() {
		if (_compdefs != null)
			return _compdefs.keySet();
		return Collections.emptyList();
	}
	/** Returns a readonly collection of component definitions
	 * ({@link ComponentDefinition}) defined in this map.
	 * @since 3.6.3
	 */
	public Collection<ComponentDefinition> getDefinitions() {
		if (_compdefs != null)
			return _compdefs.values();
		return Collections.emptyList();
	}

	/** Adds a component definition to this map.
	 *
	 * <p>Thread safe.
	 */
	public void add(ComponentDefinition compdef) {
		if (compdef == null)
			throw new IllegalArgumentException("null");

		String name = compdef.getName();
		if (isCaseInsensitive())
			name = name.toLowerCase();

		Object implcls = compdef.getImplementationClass();
		if (implcls instanceof Class)
			implcls = ((Class)implcls).getName();

		synchronized (this) {
			if (_compdefs == null) {
				_compdefsByClass =
					Collections.synchronizedMap(new HashMap<String, ComponentDefinition>(4));
				_compdefs =
					Collections.synchronizedMap(new HashMap<String, ComponentDefinition>(4));
			}

			_compdefs.put(name, compdef);
			_compdefsByClass.put((String)implcls, compdef);
		}
	}
	/** Returns whether the specified component exists.
	 */
	public boolean contains(String name) {
		return _compdefs != null
			&& _compdefs.containsKey(
				isCaseInsensitive() ? name.toLowerCase(): name);
	}

	/** Returns the component definition of the specified name, or null if not
	 * not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 */
	public ComponentDefinition get(String name) {
		return _compdefs != null ?
			(ComponentDefinition)_compdefs.get(
				isCaseInsensitive() ? name.toLowerCase(): name):
			null;
	}
	/** Returns the component definition of the specified class, or null if not
	 * found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 */
	public ComponentDefinition get(Class cls) {
		if (_compdefsByClass != null) {
			for (; cls != null; cls = cls.getSuperclass()) {
				final ComponentDefinition compdef =
					(ComponentDefinition)_compdefsByClass.get(cls.getName());
				if (compdef != null)
					return compdef;
			}
		}
		return null;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		if (_compdefs != null) {
			synchronized (_compdefs) {
				s.writeInt(_compdefs.size());
				for (ComponentDefinition compdef: _compdefs.values())
					s.writeObject(compdef);
			}
		} else {
			s.writeInt(0);
		}
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		int cnt = s.readInt();
		while (--cnt >= 0)
			add((ComponentDefinition)s.readObject());
	}

	//Cloneable//
	public Object clone() {
		final ComponentDefinitionMap clone;
		try {
			clone = (ComponentDefinitionMap)super.clone();
			clone._compdefs =
				Collections.synchronizedMap(new HashMap<String, ComponentDefinition>(_compdefs));
			clone._compdefsByClass =
				Collections.synchronizedMap(new HashMap<String, ComponentDefinition>(_compdefsByClass));
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
		return clone;
	}
}
