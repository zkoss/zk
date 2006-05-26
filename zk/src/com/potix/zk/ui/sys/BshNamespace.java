/* BshNamespace.java

{{IS_NOTE
	$Id: BshNamespace.java,v 1.4 2006/05/26 03:11:36 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Apr 10 16:08:23     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import bsh.NameSpace;
import bsh.UtilEvalError;
import bsh.Primitive;

import com.potix.lang.Objects;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Namespace;

/**
 * An implementation of {@link Namespace} on top of BeanShell.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/26 03:11:36 $
 */
public class BshNamespace implements Namespace {
	private Namespace _parent;
	private final NameSpace _ns;
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
		if (oldp != null) _ns.setParent(null); //to avoid recusrive
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
		if (oldp != null) _ns.setParent(null); //to avoid recusrive
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

	//-- Object --//
	public int hashCode() {
		return _ns.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof BshNamespace && ((BshNamespace)o)._ns.equals(_ns);
	}

	//-- helper --//
	private static class LiteNameSpace extends NameSpace {
		private LiteNameSpace(NameSpace parent, String id) {
			super(parent, id);
		}
	    public void loadDefaultImports() {
	    }
	}
}
