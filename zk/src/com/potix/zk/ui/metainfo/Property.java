/* Property.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Apr 16 14:55:35     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.lang.reflect.Method;
import java.io.Serializable;

import com.potix.lang.Classes;
import com.potix.lang.Exceptions;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.Evaluator;
import com.potix.zk.ui.ext.DynamicPropertied;

/**
 * A property of a definition.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Property implements Condition, Serializable {
	private static final Log log = Log.lookup(Property.class);
    private static final long serialVersionUID = 20060622L;

	private final String _name;
	private final String _value;
	private final Condition _cond;
	/** Used to optimize {@link #resolve}. */
	private transient Class _lastcls;
	/** Value after coerced; used only if !_bExpr */
	private transient Object _coercedVal;
	/** The method, or null if more than two methods are found
	 * (and use {@link #_mtds} in this case).
	 */
	private transient Method _mtd;
	/** Used more than two methods are found, or null if only one method
	 * (and use {@link #_mtd} in this case).
	 */
	private transient Method[] _mtds;
	/** Whether expression is specified. */
	private final boolean _bExpr;

	/** Constructs a property with a class that is known in advance.
	 */
	public Property(String name, String value, Condition cond) {
		if (name == null)
			throw new IllegalArgumentException();
		_name = name;
		_cond = cond;
		_value = value;
		_bExpr = value != null && value.indexOf("${") >= 0;
	}

	private final void resolve(Class cls) {
		final String mtdnm = Classes.toMethodName(_name, "set");
		if (_bExpr) {
			_mtds = Classes.getCloseMethods(cls, mtdnm, new Class[] {null});
			if (_mtds.length == 0) {
				if (!DynamicPropertied.class.isAssignableFrom(cls))
					throw new UiException("Method "+mtdnm+" not found for "+cls); 
				_mtds = null;
			} else if (_mtds.length == 1) {
				_mtd = _mtds[0];
				_mtds = null;
			}
		} else {
		//Note: String has higher priority
			try {
				_mtd = Classes.getCloseMethod(
					cls, mtdnm, new Class[] {String.class});
				_coercedVal = _value;
			} catch (NoSuchMethodException ex) {
				try {
					_mtd = Classes.getCloseMethod(
						cls, mtdnm, new Class[] {null});
					_coercedVal =
						Classes.coerce(_mtd.getParameterTypes()[0], _value);
				} catch (NoSuchMethodException e2) {
					if (!DynamicPropertied.class.isAssignableFrom(cls))
						throw new UiException("Method not found: "+mtdnm);
					_mtd = null;
					_coercedVal = _value;
				}
			}
		}
	}

	/** Assigns the value of this memeber to the specified component.
	 */
	public void assign(Millieu mill, Component comp, Evaluator eval) {
		final Class cls = mill.resolveImplementationClass(comp.getPage());
		if (_lastcls != cls) {
			resolve(cls);
			_lastcls = cls;
		}

		try {
			//Note: if _mtd and _mtds are both null, it must be dyna-attr
			//However, if dyna-attr, _mtd or _mtds might not be null
			final Class type =
				_mtd != null ? _mtd.getParameterTypes()[0]: Object.class;

			Object val;
			if (_bExpr) {
				val = eval.evaluate(comp, _value, type);
			} else {
				val = _coercedVal;
			}

			final Method mtd;
			if (_mtd != null) {
				mtd = _mtd;
			} else if (_mtds == null) {
				//it must be dynamic attribute
				((DynamicPropertied)comp).setDynamicProperty(_name, val);
				return; //done
			} else if (val == null) { //_mtds != null but val == null
				mtd = _mtds[0];
				val = Classes.coerce(mtd.getParameterTypes()[0], val);
			} else { //_mtds != null && val != null
				for (int j = 0; ; ++j) {
					if (j == _mtds.length) {
						mtd = _mtds[0];
						val = Classes.coerce(mtd.getParameterTypes()[0], val);
						break; //pick randomly
					}
					if (_mtds[j].getParameterTypes()[0].isInstance(val)) {
						mtd = _mtds[j];
						break; //found
					}
				}
			}

			mtd.invoke(comp, new Object[] {val});
		} catch (Exception ex) {
			log.error("Failed to assign "+this+" to "+comp+"\n"+Exceptions.getMessage(ex));
			throw UiException.Aide.wrap(ex);
		}
	}

	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}
	public String toString() {
		return "["+_name+"="+_value+']';
	}
}
