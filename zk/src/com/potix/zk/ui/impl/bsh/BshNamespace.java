/* BshNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 10 16:08:23     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.impl.bsh;

import bsh.NameSpace;
import bsh.UtilEvalError;
import bsh.Primitive;
import bsh.BshMethod;

import com.potix.lang.Objects;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Namespace;

/**
 * An implementation of {@link Namespace} on top of BeanShell.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class BshNamespace implements Namespace {//not a good idea to serialize it
	private Namespace _parent;
	private NameSpace _ns;
	/** Constructs a name space of a component (ID space owner).
	 */
	public BshNamespace(String id) {
		_ns = new LiteNameSpace(null, id);
	}
	/** Constructs a name space for a page.
	 */
	public BshNamespace(NameSpace ns) {
		if (ns == null)
			throw new IllegalArgumentException("null");
		_ns = ns;
	}
 
 	//-- Namespace --//
	public Class getClass(String clsnm) throws ClassNotFoundException {
		try {
			final Class cls = _ns.getClass(clsnm);
			if (cls == null)
				throw new ClassNotFoundException("Class not found: "+clsnm);
			return cls;
		} catch (UtilEvalError ex) {
			throw new ClassNotFoundException("Failed to load class "+clsnm);
		}
			
	}
 	public Object getVariable(String name, boolean local) {
		final NameSpace oldp = local ? _ns.getParent(): null;
		if (oldp != null) _ns.setParent(null); //to avoid calling parent's getVariable
 		try {
	 		return Primitive.unwrap(_ns.getVariable(name));
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (oldp != null) _ns.setParent(oldp); //restore
		}
 	}
	public void setVariable(String name, Object value, boolean local) {
		final NameSpace oldp = local ? _ns.getParent(): null;
		if (oldp != null) _ns.setParent(null); //to avoid calling parent's setVariable
		try {
			_ns.setVariable(name, value, false);
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (oldp != null) _ns.setParent(oldp); //restore
		}
	}
	public void unsetVariable(String name) {
		_ns.unsetVariable(name);
	}
	public Namespace getParent() {
		return _parent;
	}
	public void setParent(Namespace parent) {
		if (!Objects.equals(_parent, parent)) {
			_parent = parent;
			_ns.setParent(parent != null ? 
				(NameSpace)parent.getNativeNamespace(): null);
		}
	}

	public Object getNativeNamespace() {
		return _ns;
	}

	public void copy(Namespace nsfrom) {
		if (!(nsfrom instanceof BshNamespace))
			throw new UnsupportedOperationException("BshNamespace to BshNamespace only: "+nsfrom);

		final NameSpace from = ((BshNamespace)nsfrom)._ns;

		//variables
		try {
			final String[] vars = from.getVariableNames();
			for (int j = vars != null ? vars.length: 0; --j >= 0;) {
				if (_ns.getVariable(vars[j], false) == null)
					_ns.setVariable(vars[j],
						from.getVariable(vars[j], false), false);
			}

			//methods
			final BshMethod[] mtds = from.getMethods();
			for (int j = mtds != null ? mtds.length: 0; --j >= 0;)
				_ns.setMethod(mtds[j].getName(), mtds[j]);
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		//TODO: restore _parent
	}

	//-- Object --//
	public int hashCode() {
		return _ns.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof BshNamespace && ((BshNamespace)o)._ns.equals(_ns);
	}
}
