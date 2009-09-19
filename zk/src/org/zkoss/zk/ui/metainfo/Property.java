/* Property.java

	Purpose:
		
	Description:
		
	History:
		Sun Apr 16 14:55:35     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.lang.reflect.Method;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Information about how to initialize a property (aka., a field of a component).
 * There are two kind of properties: one is a String instance (either
 * a string vaue or an expression), and the other is
 * a {@link NativeInfo} instance. The later is also called the native content.
 *
 * @author tomyeh
 */
public class Property extends EvalRefStub
implements Condition, java.io.Serializable {
	private static final Log log = Log.lookup(Property.class);
    private static final long serialVersionUID = 20060622L;

	private final String _name;
	/** The value if it is not the native content.
	 * Exactly one of _value and _navval is non-null.
	 */
	private final ExValue _value;
	/** The value if it is the native content.
	 * Exactly one of _value and _navval is non-null.
	 */
	private final NativeInfo _navval;
	private final ConditionImpl _cond;
	/** Used to optimize {@link #resolve}. */
	private transient Class _lastcls;
	/** The method, or null if more than two methods are found
	 * (and use {@link #_mtds} in this case).
	 */
	private transient Method _mtd;
	/** Used more than two methods are found, or null if only one method
	 * (and use {@link #_mtd} in this case).
	 */
	private transient Method[] _mtds;

	/** Constructs a property with a class that is known in advance.
	 * @exception IllegalArgumentException if evalr or name is null
	 */
	public Property(EvaluatorRef evalr, String name, String value,
	ConditionImpl cond) {
		this(evalr, name, value, null, cond);
	}
	/** Constructs a property with the native content.
	 * The native content is represented by {@link NativeInfo},
	 * i.e., a XML fragment (aka., a tree of {@link ComponentInfo}.
	 * @since 3.5.0
	 */
	public Property(EvaluatorRef evalr, String name, NativeInfo value,
	ConditionImpl cond) {
		this(evalr, name, null, value, cond);
	}
	private Property(EvaluatorRef evalr, String name, String value,
	NativeInfo navval, ConditionImpl cond) {
		if (name == null || evalr == null)
			throw new IllegalArgumentException();

		_evalr = evalr;
		_name = name;

		_cond = cond;
		_navval = navval;
		_value = navval != null ? null: new ExValue(value, Object.class);
			//type will be fixed when mapped to a method
	}

	/** Returns the name of the property.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the raw value of the property.
	 * Note: it is the original value without evaluation.
	 * In other words, it may contain EL expressions.
	 * @exception UnsupportedOperationException if value is the native content,
	 * i.e., it is constructed by use of {@link #Property(EvaluatorRef, String, NativeInfo, ConditionImpl)}.
	 * @since 3.0.0
	 */
	public String getRawValue() {
		if (_value == null)
			throw new UnsupportedOperationException("native content");
		return _value.getRawValue();
	}
	/** Sets the raw value of the property.
	 * @exception UnsupportedOperationException if value is the native content,
	 * i.e., it is constructed by use of {@link #Property(EvaluatorRef, String, NativeInfo, ConditionImpl)}.
	 * @since 3.0.0
	 */
	public void setRawValue(String value) {
		if (_value == null)
			throw new UnsupportedOperationException("native content");
		_value.setRawValue(value);
	}

	/** Evaluates the value to an Object.
	 * Note: it does NOT call {@link #isEffective} and it doesn't coerce
	 * the result (i.e., Object.class is assumed).
	 */
	public Object getValue(Component comp) {
		if (_value != null)
			return _value.getValue(_evalr, comp);

		Desktop desktop = comp.getDesktop();
		Page page;
		if (desktop == null) {
			Execution exec = Executions.getCurrent();
			if (exec == null)
				throw new IllegalStateException("Not attached, nor execution");
			desktop = exec.getDesktop();
			page = ((ExecutionCtrl)exec).getCurrentPage();
		} else {
			page = comp.getPage();
		}
		return ((WebAppCtrl)desktop.getWebApp()).getUiEngine()
			.getNativeContent(comp, _navval.getChildren(),
			((Native)_navval.newInstance(page, comp)).getHelper());
	}
	/** Evaluates the value to an Object.
	 * Note: it does NOT call {@link #isEffective} and it doesn't coerce
	 * the result (i.e., Object.class is assumed).
	 * @exception UnsupportedOperationException if value is the native content,
	 * i.e., it is constructed by use of {@link #Property(EvaluatorRef, String, NativeInfo, ConditionImpl)}.
	 */
	public Object getValue(Page page) {
		if (_value == null)
			throw new UnsupportedOperationException("native content");
		return _value.getValue(_evalr, page);
	}
	/** Assigns the value of this memeber to the specified component.
	 *
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void assign(Component comp) {
		if (isEffective(comp)) {
			try {
				assign0(comp);
			} catch (Exception ex) {
				log.error("Failed to assign "+this+" to "+comp+"\n"+Exceptions.getMessage(ex));
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	private Object[] resolve(Class cls) {
		Method mtd = null;
		Method[] mtds = null;
		final String mtdnm = Classes.toMethodName(_name, "set");
		if (_value == null) {
			mtd = resolveMethod0(cls, mtdnm);
		} else {
			mtds = Classes.getCloseMethods(cls, mtdnm, new Class[] {null});
			if (mtds.length == 0) {
				if (!DynamicPropertied.class.isAssignableFrom(cls))
					throw new PropertyNotFoundException("Method "+mtdnm+" not found for "+cls); 
				mtds = null;
			} else if (mtds.length == 1) {
				mtd = mtds[0];
				mtds = null;
			}
		}
		return new Object[] {mtd, mtds};
	}
	private void assign0(Component comp) throws Exception {
		//Note: we have to synchronize since metainfo is shared
		//(unless it is initialized at the constructor)
		final Class cls = comp.getClass();
		if (_lastcls == null) { //first tiime
			synchronized (this) {
				if (_lastcls == null) { //not being initialized
					final Object[] mi = resolve(cls);
					_mtd = (Method)mi[0];
					_mtds = (Method[])mi[1];
					_lastcls = cls;
				}
			}
		}

		Method mtd;
		Method[] mtds;
		if (_lastcls == cls) {
			mtd = _mtd;
			mtds = _mtds;
		} else { //two or more diff comp classes (use="${x?a:b}")
			//We don't cache methods for 2nd class (only cache 1st)
			final Object[] mi = resolve(cls);
			mtd = (Method)mi[0];
			mtds = (Method[])mi[1];
		}

		//Note: if mtd and mtds are both null, it must be dyna-attr
		//However, if dyna-attr, mtd or mtds might not be null
		Class type =
			mtd != null ? mtd.getParameterTypes()[0]: Object.class;
		if (_value != null) _value.setExpectedType(type);
		Object val = getValue(comp);

		Method m = null;
		if (mtd != null) {
			m = mtd;
		} else if (mtds == null) {
			//it must be dynamic attribute
			((DynamicPropertied)comp).setDynamicProperty(_name, val);
			return; //done
		} else { //mtds != null && val != null
			if ((m = findExact(mtds, val)) == null
			&& (m = findAssignable(mtds, val)) == null
			&& (m = findNullable(mtds, val)) == null) {
				//primitive
				if (val != null)
					for (int j = 0; j < mtds.length; ++j) {
						type = mtds[j].getParameterTypes()[0];
						if (type.isPrimitive()) {
							try {
								val = Classes.coerce(type, val);
								m = mtds[j];
								break;
							} catch (Throwable ex) {
							}
						}
					}

				if (m == null) {
					//non primitive, non string
					Method strmtd = null;
					for (int j = 0; j < mtds.length; ++j) {
						type = mtds[j].getParameterTypes()[0];
						if (String.class.equals(type)) {
							strmtd = mtds[j];
						} else if (!type.isPrimitive()) {
							try {
								val = Classes.coerce(type, val);
								m = mtds[j];
								break;
							} catch (Throwable ex) {
							}
						}
					}
					if (m == null) {
						if (strmtd != null) {
							try {
								val = Classes.coerce(String.class, val);
								m = strmtd;
							} catch (Throwable ex) {
							}
						}
						if (m == null)
							throw new ClassCastException("Unable to find a setter named "+_name+" that supports "+val);
					}
				}
			}
		}
		m.invoke(comp, new Object[] {val});
	}
	private static Method findExact(final Method[] mtds, final Object val) {
		if (val != null) {
			final Class vcls = val.getClass();
			for (int j = 0; j < mtds.length; ++j)
				if (vcls.equals(mtds[j].getParameterTypes()[0]))
					return mtds[j]; //found
		}
		return null;
	}
	private static Method findAssignable(final Method[] mtds, final Object val) {
		if (val != null) {
			//Look for the most 'extended' and isInstance class
			Method m = null;
			Class t = null;
			for (int j = 0; j < mtds.length; ++j) {
				final Class type = mtds[j].getParameterTypes()[0];
				if (type.isInstance(val)
				&& (t == null || t.isAssignableFrom(type))) {
					t = type;
					m = mtds[j];
				}
			}
			return m;
		}
		return null;
	}
	private static Method findNullable(final Method[] mtds, final Object val) {
		if (val == null) {
			//Look for the most 'extended' class
			Method m = null;
			Class t = null;
			for (int j = 0; j < mtds.length; ++j) {
				final Class type = mtds[j].getParameterTypes()[0];
				if (!type.isPrimitive()
				&& (t == null || t.isAssignableFrom(type))) {
					t = type;
					m = mtds[j];
				}
			}
			return m;
		}
		return null;
	}

	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}
	public String toString() {
		return "["+_name+(_navval != null ? "": "="+_value)+']';
	}

	//static utilities//
	/** Resloves the method for the specified property, or null
	 * if {@link DynamicPropertied#setDynamicProperty} shall be used instead.
	 *
	 * <p>Use this method to retrieve the method when you want to assign a value
	 * to a component's property.
	 *
	 * <p>Don't use the reflection directly since this method searches
	 * more signatures.
	 * @param name the property name, such as "title".
	 * @exception PropertyNotFoundException if the property is not found,
	 * i.e., no corresponding method and {@link DynamicPropertied} not
	 * implmented.
	 * @since 3.5.0
	 */
	public static final Method resolveMethod(Class cls, String name)
	throws PropertyNotFoundException {
		return resolveMethod0(cls, Classes.toMethodName(name, "set"));
	}
	private static final Method resolveMethod0(Class cls, String mtdnm)
	throws PropertyNotFoundException {
		try {
			return Classes.getCloseMethod(
				cls, mtdnm, new Class[] {String.class});
		} catch (NoSuchMethodException ex) {
			try {
				return Classes.getCloseMethod(
					cls, mtdnm, new Class[] {null});
			} catch (NoSuchMethodException e2) {
				if (!DynamicPropertied.class.isAssignableFrom(cls))
					throw new PropertyNotFoundException("Method, "+mtdnm+", not found for "+cls);
				return null;
			}
		}
	}
	/** Assigns a property.
	 *
	 * <p>Don't use the refelction directly since this method searches
	 * more signatures.
	 *
	 * @exception PropertyNotFoundException if the property is not found,
	 * i.e., no corresponding method and {@link DynamicPropertied} not
	 * implmented.
	 * @exception UiException if fail to assign
	 * @since 3.5.0
	 */
	public static final void assign(Component comp, String name, String value) {
		final Method mtd = resolveMethod(comp.getClass(), name);
		if (mtd != null) {
			try {
				Object val = Classes.coerce(mtd.getParameterTypes()[0], value);
				mtd.invoke(comp, new Object[] {val});
			} catch (Exception ex) {
				log.error("Failed to assign "+value+" to "+comp+"\n"+Exceptions.getMessage(ex));
				throw UiException.Aide.wrap(ex);
			}
		} else {
			((DynamicPropertied)comp).setDynamicProperty(name, value);
		}
	}
}
