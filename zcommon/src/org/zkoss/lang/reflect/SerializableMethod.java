/* SerializableMethod.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 17:57:05     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.lang.reflect;

import java.lang.reflect.Method;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
/**
 * A wrapper of java.lang.reflect.Method to make it serializable.
 *
 * @author tomyeh
 */
public class SerializableMethod implements Serializable, Cloneable {
    private static final long serialVersionUID = 20060622L;
	private transient Method _m;

	public SerializableMethod(Method method) {
		_m = method;
	}

	/** Returns the method being encapsulated.
	 */
	public final Method getMethod() {
		return _m;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(ObjectOutputStream s)
	throws IOException {
		s.defaultWriteObject();

		if (_m == null) {
			s.writeObject(null);
		} else {
			s.writeObject(_m.getDeclaringClass());
			s.writeObject(_m.getName());

			final Class[] argTypes = _m.getParameterTypes();
			s.writeInt(argTypes.length);
			for (int j = 0; j < argTypes.length; ++j)
				s.writeObject(argTypes[j]);
		}
	}
	private synchronized void readObject(ObjectInputStream s)
	throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		final Class<?> cls = (Class<?>)s.readObject();
		if (cls != null) {
			final String nm = (String)s.readObject();
			final int sz = s.readInt();
			final Class[] argTypes = new Class[sz];
			for (int j = 0; j < sz; ++j)
				argTypes[j] = (Class)s.readObject();
			try {
				_m = cls.getMethod(nm, argTypes);
			} catch (NoSuchMethodException ex) {
				throw new SystemException("Method not found: "+nm+" with "+Objects.toString(argTypes));
			}
		}
	}

	//-- cloneable --//
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	//Object//
	public int hashCode() {
		return Objects.hashCode(_m);
	}
	public boolean equals(Object o) {
		return (o instanceof SerializableMethod)
			&& Objects.equals(_m, ((SerializableMethod)o)._m);
	}
	public String toString() {
		return Objects.toString(_m);
	}
}
