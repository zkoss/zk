/* NSWrapSR.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 11:58:12 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.scripting.bsh;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.io.Externalizable;
import java.io.IOException;

import bsh.NameSpace;
import bsh.BshMethod;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.ext.Scope;

/** Serializabe Namespace wrapper.
 * Used to prevent to serialize NameSpace directly.
 * 
 * @author tomyeh
 */
/*package*/ class NSWrapSR extends NSWrap implements Serializable {
	private static final Log log = BSHInterpreter.log;
	private Map<String, Object> _vars;
	private List<BshMethod> _mtds;
	private List<String> _clses;
	private List<String> _pkgs;

	/*package*/ NSWrapSR(NameSpace ns) {
		super(ns);
	}
	public NSWrapSR() {
	}
	/** Returns the associated NameSpace. */
	public NameSpace unwrap(Scope scope) {
		if (_bshns == null) {
			_bshns = BSHInterpreter.getInterpreter(scope).newNS(scope);
			if (_vars != null) {
				for (Map.Entry<String, Object> me: _vars.entrySet()) {
					try {
						_bshns.setVariable(me.getKey(), me.getValue(), false);
					} catch (Throwable ex) {
						log.warning("Ignored failure of set "+me.getKey(), ex);
					}
				}
				_vars = null;
			}
			if (_mtds != null) {
				for (BshMethod mtd: _mtds) {
					try {
						_bshns.setMethod(mtd.getName(), mtd);
					} catch (Throwable ex) {
						log.warning("Ignored failure of set "+mtd, ex);
					}
				}
				_mtds = null;
			}
			if (_clses != null) {
				for (String name: _clses) {
					try {
						_bshns.importClass(name);
					} catch (Throwable ex) {
						log.warning("Ignored failure of import class "+name, ex);
					}
				}
				_clses = null;
			}
			if (_pkgs != null) {
				for (String name: _pkgs) {
					try {
						_bshns.importPackage(name);
					} catch (Throwable ex) {
						log.warning("Ignored failure of import package "+name, ex);
					}
				}
				_pkgs = null;
			}
		}
		return _bshns;
	}
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws IOException {
		s.defaultWriteObject();

		s.writeBoolean(_bshns != null);
		if (_bshns != null) {
			BSHInterpreter.write(_bshns, s, new BSHInterpreter.Filter() {
				public boolean accept(String name, Object value) {
					return value == null || value instanceof Serializable || value instanceof Externalizable;
				}
			});
		}
	}
	private void readObject(java.io.ObjectInputStream s)
	throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (s.readBoolean()) {
			BSHInterpreter.read(new NameSpace(null, null, "nst") {
				public void setVariable(String name, Object value, boolean strictJava) {
					if (_vars == null) _vars = new HashMap<String, Object>();
					_vars.put(name, value);
				}
				public void setMethod(String name, BshMethod mtd) {
					if (_mtds == null) _mtds = new LinkedList<BshMethod>();
					_mtds.add(mtd);
				}
				public void importClass(String name) {
					if (_clses == null) _clses = new LinkedList<String>();
					_clses.add(name);
				}
				public void importPackage(String name) {
					if (_pkgs == null) _pkgs = new LinkedList<String>();
					_pkgs.add(name);
				}
				public void loadDefaultImports() {
					 //to speed up the formance
				}
			}, s);
		}
	}
}
