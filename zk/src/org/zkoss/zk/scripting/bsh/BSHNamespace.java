/* BSHNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 10 16:08:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsh;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

import bsh.NameSpace;
import bsh.UtilEvalError;
import bsh.Primitive;
import bsh.BshMethod;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Method;

/**
 * An implementation of {@link Namespace} on top of BeanShell.
 *
 * @author tomyeh
 */
public class BSHNamespace implements Namespace {//not a good idea to serialize it
	private static final Log log = Log.lookup(BSHNamespace.class);

	private Namespace _parent;
	private NameSpace _bshns;

	/** Constructs a name space of a component (ID space owner).
	 */
	public BSHNamespace(String id) {
		_bshns = new LiteNameSpace(null, id);
	}
	/** Constructs a name space for a page.
	 */
	public BSHNamespace(NameSpace ns) {
		if (ns == null)
			throw new IllegalArgumentException("null");
		_bshns = ns;
	}
 
 	//-- Namespace --//
	public Class getClass(String clsnm) {
		try {
			return _bshns.getClass(clsnm);
		} catch (UtilEvalError ex) {
			throw new UiException("Failed to load class "+clsnm, ex);
		}
			
	}
 	public Object getVariable(String name, boolean local) {
		final NameSpace oldp = local ? _bshns.getParent(): null;
		if (oldp != null) _bshns.setParent(null); //to avoid calling parent's getVariable
 		try {
	 		return Primitive.unwrap(_bshns.getVariable(name));
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (oldp != null) _bshns.setParent(oldp); //restore
		}
 	}
	public void setVariable(String name, Object value, boolean local) {
		final NameSpace oldp = local ? _bshns.getParent(): null;
		if (oldp != null) _bshns.setParent(null); //to avoid calling parent's setVariable
		try {
			_bshns.setVariable(name, value != null ? value: Primitive.NULL, false);
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (oldp != null) _bshns.setParent(oldp); //restore
		}
	}
	public void unsetVariable(String name) {
		_bshns.unsetVariable(name);
	}
	public Method getMethod(String name, Class[] argTypes, boolean local) {
		if (argTypes == null)
			argTypes = new Class[0];

		try {
		 	final BshMethod m = _bshns.getMethod(name, argTypes, local);
		 	return m != null ? new BSHMethod(m): null;
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	 	
	}
	public Namespace getParent() {
		return _parent;
	}
	public void setParent(Namespace parent) {
		if (!Objects.equals(_parent, parent)) {
			_parent = parent;
			_bshns.setParent(parent != null ? 
				(NameSpace)parent.getNativeNamespace(): null);
		}
	}

	public Object getNativeNamespace() {
		return _bshns;
	}

	public Object clone(String id) {
		final BSHNamespace to = new BSHNamespace(id);		

		//variables
		final String[] vars = _bshns.getVariableNames();
		for (int j = vars != null ? vars.length: 0; --j >= 0;) {
			final String nm = vars[j];
			if (!"bsh".equals(nm)) {
				final Object val = this.getVariable(nm, true);
				to.setVariable(nm, val, true);
			}
		}

		try {
			//methods
			final BshMethod[] mtds = _bshns.getMethods();
			for (int j = mtds != null ? mtds.length: 0; --j >= 0;) {
				final String nm = mtds[j].getName();
				to._bshns.setMethod(nm, mtds[j]);
			}
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}

		if (_parent != null) to.setParent(_parent);
		return to;
	}
	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException {
		//1. variables
		final String[] vars = _bshns.getVariableNames();
		for (int j = vars != null ? vars.length: 0; --j >= 0;) {
			final String nm = vars[j];
			if (nm != null && !"bsh".equals(nm)) {
				final Object val = getVariable(nm, true);
				//we cannot store null value since setVariable won't accept it
				if (((val instanceof java.io.Serializable)
					|| (val instanceof java.io.Externalizable))
				&& (filter == null || filter.accept(nm, val))) {
					s.writeObject(nm);
					s.writeObject(val);
				}
			}
		}
		s.writeObject(null); //denote end-of-vars

		//2. methods
		final BshMethod[] mtds = _bshns.getMethods();
		for (int j = mtds != null ? mtds.length: 0; --j >= 0;) {
			final String nm = mtds[j].getName();
			if (filter == null || filter.accept(nm, mtds[j])) {
				//hack BeanShell 2.0b4 which cannot be serialized correctly
				Field f = null;
				boolean acs = false;
				try {
					f = Classes.getAnyField(BshMethod.class, "declaringNameSpace");
					acs = f.isAccessible();
					f.setAccessible(true);
					final Object old = f.get(mtds[j]);
					try {
						f.set(mtds[j], null);				
						s.writeObject(mtds[j]);
					} finally {
						f.set(mtds[j], old);
					}
				} catch (java.io.IOException ex) {
					throw ex;
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				} finally {
					if (f != null) f.setAccessible(acs);
				}
			}
		}
		s.writeObject(null); //denote end-of-mtds

		//3. imported class
		Field f = null;
		boolean acs = false;
		try {
			f = Classes.getAnyField(NameSpace.class, "importedClasses");
			acs = f.isAccessible();
			f.setAccessible(true);
			final Map clses = (Map)f.get(_bshns);
			if (clses != null)
				for (Iterator it = clses.values().iterator(); it.hasNext();) {
					final String clsnm = (String)it.next();
					if (!clsnm.startsWith("bsh."))
						s.writeObject(clsnm);
				}
		} catch (java.io.IOException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (f != null) f.setAccessible(acs);
		}
		s.writeObject(null); //denote end-of-cls

		//4. imported package
		f = null;
		acs = false;
		try {
			f = Classes.getAnyField(NameSpace.class, "importedPackages");
			acs = f.isAccessible();
			f.setAccessible(true);
			final Collection pkgs = (Collection)f.get(_bshns);
			if (pkgs != null)
				for (Iterator it = pkgs.iterator(); it.hasNext();) {
					final String pkgnm = (String)it.next();
					if (!pkgnm.startsWith("java.awt")
					&& !pkgnm.startsWith("javax.swing"))
						s.writeObject(pkgnm);
				}
		} catch (java.io.IOException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (f != null) f.setAccessible(acs);
		}
		s.writeObject(null); //denote end-of-cls
	}
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		for (;;) {
			final String nm = (String)s.readObject();
			if (nm == null) break; //no more

			setVariable(nm, s.readObject(), true);
		}

		try {
			for (;;) {
				final BshMethod mtd = (BshMethod)s.readObject();
				if (mtd == null) break; //no more

				//fix declaringNameSpace
				Field f = null;
				boolean acs = false;
				try {
					f = Classes.getAnyField(BshMethod.class, "declaringNameSpace");
					acs = f.isAccessible();
					f.setAccessible(true);
					f.set(mtd, _bshns);				
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				} finally {
					if (f != null) f.setAccessible(acs);
				}

				_bshns.setMethod(mtd.getName(), mtd);
			}
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}

		for (;;) {
			final String nm = (String)s.readObject();
			if (nm == null) break; //no more

			_bshns.importClass(nm);
		}

		for (;;) {
			final String nm = (String)s.readObject();
			if (nm == null) break; //no more

			_bshns.importPackage(nm);
		}
	}

	//-- Object --//
	public int hashCode() {
		return _bshns.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof BSHNamespace && ((BSHNamespace)o)._bshns.equals(_bshns);
	}
}
