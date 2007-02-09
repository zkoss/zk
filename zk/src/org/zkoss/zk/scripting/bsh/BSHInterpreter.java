/* BSHInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:28:43     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsh;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.BshMethod;
import bsh.Variable;
import bsh.Primitive;
import bsh.EvalError;
import bsh.UtilEvalError;

import org.zkoss.lang.Classes;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.Method;
import org.zkoss.zk.scripting.util.GenericInterpreter;
import org.zkoss.zk.scripting.SerializableInterpreter;

/**
 * The interpreter that uses BeanShell to interpret zscript codes.
 *
 * @author tomyeh
 */
public class BSHInterpreter extends GenericInterpreter
implements SerializableInterpreter {
	private bsh.Interpreter _ip;
	private NS _bshns;

	public BSHInterpreter() {
	}

	/** Returns the native interpretor. */
	/*package*/ bsh.Interpreter getNativeInterpreter() {
		return _ip;
	}

	//GenericInterpreter//
	protected void exec(String script) {
		try {
			_ip.eval(script);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	protected Object get(String name) {
		try {
			return Primitive.unwrap(_ip.get(name));
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	protected void set(String name, Object val) {
		try {
			_ip.set(name, val);
				//unlike NameSpace.setVariable, _ip.set() handles null
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	protected void unset(String name) {
		try {
			_ip.unset(name);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	//-- Interpreter --//
	public void init(Page owner) {
		super.init(owner);

		_ip = new bsh.Interpreter();
		_ip.setClassLoader(Thread.currentThread().getContextClassLoader());

		_bshns = new NS(_ip.getClassManager(), "global");
		_ip.setNameSpace(_bshns);
	}

	public Class getClass(String clsnm) {
		try {
			return _bshns.getClass(clsnm);
		} catch (UtilEvalError ex) {
			throw new UiException("Failed to load class "+clsnm, ex);
		}
	}
	public Method getMethod(String name, Class[] argTypes) {
		if (argTypes == null)
			argTypes = new Class[0];

		try {
		 	final BshMethod m = _bshns.getMethod(name, argTypes, false);
		 	return m != null ? new BSHMethod(m): null;
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	//supporting classes//
	/** NameSpace. */
	private class NS extends NameSpace {
		private boolean _inGet;

		/** Don't call this method. */
	    private NS(BshClassManager classManager, String name) {
	    	super(classManager, name);
	    }

		//super//
		protected Variable getVariableImpl(String name, boolean recurse)
		throws UtilEvalError {
			//Tom M Yeh: 20060606:
			//We cannot override getVariable because BeanShell use
			//getVariableImpl to resolve a variable recusrivly
			//
			//setVariable will callback this method,
			//so use _inGet to prevent dead loop
			Variable var = super.getVariableImpl(name, recurse);
			if (!_inGet && var == null) {
				Object v = getFromNamespace(name);
				if (v != null) {
			//Variable has no public/protected contructor, so we have to
			//store the value back (with setVariable) and retrieve again
					_inGet = true;
					try {
						this.setVariable(name,
							v != null ? v: Primitive.NULL, false);
						var = super.getVariableImpl(name, false);
						this.unsetVariable(name); //restore
					} finally {
						_inGet = false;
					}
				}
			}
			return var;
		}
	}

	//SerializableInterpreter//
	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException {
		//1. variables
		final String[] vars = _bshns.getVariableNames();
		for (int j = vars != null ? vars.length: 0; --j >= 0;) {
			final String nm = vars[j];
			if (nm != null && !"bsh".equals(nm)) {
				final Object val = get(nm);
				if ((val == null || (val instanceof java.io.Serializable)
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

			set(nm, s.readObject());
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
}
