/* Singleton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 19 08:55:55     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.sys;

import java.lang.reflect.InvocationTargetException;

import com.potix.lang.D;
import com.potix.lang.Classes;

/**
 * Solves the Double-Check idiom.
 * <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">Double-Checked Locking is Broken</a>
 *
 * <p>Use:
 * <pre><code>class X { //implements AutoStart if init will call the()
	private static final Singleton _the = new Singleton();
	public static X the() {
		final X one = (X)_the.get();
		if (one != null)
			return one;
		try {
			return (X)_the.newInstance(X.class);
		} catch(Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
}</code></pre>
 *
 * <p>Note: if the real class implements {@link AutoStart}, then its
 * {@link AutoStart#start} will be invoked when the singleton is instantiated.
 * It is required if your init codes call the() again. See {@link AutoStart}
 * for why.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:25 $
 * @see PerDomainManager
 * @see AutoStart
 */
public class Singleton {
	/** the singleton. */
	private Object _the;
		//NOTE: In "_the = new X()", _the might be assigned first, and then
		//be constructed. So, use volatile to avoid any reorder.
	/** the singleton who is executing AutoStart.start(). */
	private Object _theInConstruct;
	/** whether the singleton is constructing. It is used to detect
	 * any callback to the() in constructore, because it causes the dead loop. 
	 */
	private boolean _constructing;

	/** Returns the singleton, or null if not initialized yet.
	 * To initialize it, use {@link #newInstance(Class)}.
	 *
	 * <p>Implementation: to be most conserative, we might always return null.
	 * And, it degenerates to the simplest form: all accesses inside sync.
	 */
	public final Object get() {
		return _the;
	}
	/** Creates the singleton, never null, by specifying a class.
	 * @see #get
	 * @see #newInstance(String)
	 */
	synchronized public final Object newInstance(Class klass)
	throws InstantiationException, IllegalAccessException {
		if (_the != null)
			return _the;
		if (_theInConstruct != null)
			return _theInConstruct; //reentrant from the same thread thru start()

		if (_constructing)
			throw new IllegalStateException(
				"Calls the() in "+klass.getName()+"()\nIt will cause a dead loop.\n"
				+"Please fix your codes by implementing AutoStart.start()");

		try {
			_constructing = true;
			final Object o = klass.newInstance();
			_constructing = false;

			if (o instanceof AutoStart) {
				_theInConstruct = o; //let reentry from the same thread work
				((AutoStart)o).start();
			}

			return _the = o;
		} finally { //clean it up
			_theInConstruct = null;
			_constructing = false;
		}
	}
	/** Creates the singleton, never null, by specifying a class name.
	 * @see #get
	 * @see #newInstance(Class)
	 */
	public final Object newInstance(String className)
	throws InstantiationException, IllegalAccessException,
	NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
		try {
			return newInstance(Classes.forNameByThread(className));
		} catch(Error ex) {
			System.err.println("Failed to load "+className);
			throw ex;
		} catch(InstantiationException ex) {
			System.err.println("Failed to load "+className);
			throw ex;
		}
	}
	/** Enforce the singleton to be the specified one.
	 */
	synchronized public final void set(Object o) {
		if (_theInConstruct != null)
			throw new IllegalStateException("Cannot be called in start()");
			//reason: _the will be assigned later and override the following
		_the = o;
	}
	/** Resets the singleton, so next call to {@link #get} returns null.
	 */
	synchronized public final void reset() {
		if (_theInConstruct != null)
			throw new IllegalStateException("Cannot be called in start()");
		_the = null;
	}
}
