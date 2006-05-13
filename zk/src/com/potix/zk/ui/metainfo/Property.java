/* Property.java

{{IS_NOTE
	$Id: Property.java,v 1.4 2006/05/08 05:58:44 tomyeh Exp $
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

import com.potix.lang.Classes;
import com.potix.lang.Exceptions;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.Evaluator;
import com.potix.zk.ui.ext.DynamicPropertied;

/**
 * A property of a definition.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/08 05:58:44 $
 */
public class Property implements Condition {
	private static final Log log = Log.lookup(Property.class);

	/** The method, or null if more than two methods are found
	 * (and use {@link #_mtds} in this case).
	 */
	private final Method _mtd;
	/** Used more than two methods are found, or null if only one method
	 * (and use {@link #_mtd} in this case).
	 */
	private final Method[] _mtds;
	private final String _name;
	private final Object _value;
	private final Condition _cond;
	/** Whether expression is specified. */
	private final boolean _bExpr;

	/** A constructor
	 */
	public Property(Class cls, String name, String value, Condition cond) {
		_name = name;
		_cond = cond;
		_bExpr = value != null && value.indexOf("${") >= 0;

		final String mtdnm = Classes.toMethodName(name, "set");
		if (_bExpr) {
			final Method[] mtds =
				Classes.getCloseMethods(cls, mtdnm, new Class[] {null});
			if (mtds.length == 0) {
				if (!DynamicPropertied.class.isAssignableFrom(cls))
					throw new UiException("Method "+mtdnm+" not found for "+cls); 
				_mtd = null; _mtds = null;
			} else {
				if (mtds.length == 1) {
					_mtd = mtds[0];
					_mtds = null;
				} else {
					_mtd = null;
					_mtds = mtds;
				}
			}
			_value = value;
		} else {
		//Note: String has higher priority
			Object val;
			Method mtd;
			try {
				mtd = Classes.getCloseMethod(
					cls, mtdnm, new Class[] {String.class});
				val = value;
			} catch (NoSuchMethodException ex) {
				try {
					mtd = Classes.getCloseMethod(
						cls, mtdnm, new Class[] {null});
					val = Classes.coerce(mtd.getParameterTypes()[0], value);
				} catch (NoSuchMethodException e2) {
					if (!DynamicPropertied.class.isAssignableFrom(cls))
						throw new UiException("Method not found: "+mtdnm);
					mtd = null;
					val = value;
				}
			}
			_mtd = mtd;
			_mtds = null;
			_value = val;
		}
	}

	/** Assigns the value of this memeber to the specified component.
	 */
	public void assign(Component comp, Evaluator eval) {
		try {
			//Note: if _mtd and _mtds are both null, it must be dyna-attr
			//However, if dyna-attr, _mtd or _mtds might not be null
			final Class type =
				_mtd != null ? _mtd.getParameterTypes()[0]: Object.class;

			Object val;
			if (_bExpr) {
				val = eval.evaluate(comp, (String)_value, type);
			} else {
				val = _value;
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
	public boolean isEffective(PageDefinition pagedef, Page page) {
		return _cond == null || _cond.isEffective(pagedef, page);
	}
	public String toString() {
		return "["+_name+"="+_value+']';
	}
}
