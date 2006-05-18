/* ForEachImpl.java

{{IS_NOTE
	$Id: ForEachImpl.java,v 1.1.1.1 2006/04/18 02:42:34 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 14:21:08     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;

import com.potix.util.CollectionsX;

import com.potix.zk.ui.Executions;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.util.ForEach;
import com.potix.zk.ui.util.ForEachStatus;

/**
 * An implementation of {@link ForEach}.
 *
 * <p>Note: the use of {@link ForEachImpl} is different from
 * {@link ConditionImpl}. While you could use the same instance of
 * {@link ConditionImpl} for all evaluation, each instance of
 * {@link ForEachImpl} can be used only once (drop it after {@link #next}
 * returns false).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1.1.1 $ $Date: 2006/04/18 02:42:34 $
 */
public class ForEachImpl implements ForEach {
	private final PageDefinition _pagedef;
	private final Page _page;
	private final Component _comp;
	private final String _expr;
	private Status _status;
	private Iterator _it;
	private Object _oldEach;
	private boolean _done;

	/** Returns an instance that represents the iterator for the
	 * specified collection, or null if expr is null or empty.
	 *
	 * @param expr an EL expression that shall return a collection of objects.
	 */
	public static ForEach getInstance(Component comp, String expr) {
		if (expr == null || expr.length() == 0)
			return null;
		return new ForEachImpl(comp, expr);
	}
	/** Returns an instance that represents the iterator for the
	 * specified collection, or null if expr is null or empty.
	 *
	 * @param expr an EL expression that shall return a collection of objects.
	 */
	public static ForEach getInstance(
	PageDefinition pagedef, Page page, String expr) {
		if (expr == null || expr.length() == 0)
			return null;
		return new ForEachImpl(pagedef, page, expr);
	}

	/** Constructor.
	 * In most cases, use {@link #getInstance(Component, String)}
	 * instead of this constructor.
	 */
	public ForEachImpl(Component comp, String expr) {
		if (comp == null)
			throw new IllegalArgumentException("comp");

		_pagedef = null;
		_page = null;
		_comp = comp;
		_expr = expr;
	}
	/** Constructor.
	 * In most cases, use {@link #getInstance(Component, String)}
	 * instead of this constructor.
	 */
	public ForEachImpl(PageDefinition pagedef, Page page, String expr) {
		if (page == null)
			throw new IllegalArgumentException("page");

		_pagedef = pagedef;
		_page = page;
		_comp = null;
		_expr = expr;
	}

	//-- ForEach --//
	public boolean next() {
		if (_done)
			throw new IllegalStateException("Iterate twice not allowed");

		if (_status == null) {
			final Object o = _comp != null ?
				Executions.evaluate(_comp, _expr, Object.class):
				Executions.evaluate(_pagedef, _page, _expr, Object.class);
			if (o == null) {
				_done = true;
				return false;
			}

			prepare(o); //prepare iterator

			//preserve
			if (_comp != null) {
				_oldEach = _comp.getVariable("each", true);
				_status = new Status(_comp.getVariable("forEachStatus", true));
				_comp.setVariable("forEachStatus", _status, true);
			} else {
				_oldEach = _page.getVariable("each");
				_status = new Status(_page.getVariable("forEachStatus"));
				_page.setVariable("forEachStatus", _status);
			}
		}

		if (_it.hasNext()) {
			_status.each = _it.next();
			++_status.index;
			if (_comp != null) _comp.setVariable("each", _status.each, true);
			else _page.setVariable("each", _status.each);
			return true;
		}

		//restore
		_done = true;
		if (_comp != null) {
			if (_status.previous != null)
				_comp.setVariable("forEachStatus", _status.previous, true);
			else
				_comp.unsetVariable("forEachStatus");
			if (_oldEach != null)
				_comp.setVariable("each", _oldEach, true);
			else
				_comp.unsetVariable("each");
		} else {
			if (_status.previous != null)
				_page.setVariable("forEachStatus", _status.previous);
			else
				_page.unsetVariable("forEachStatus");
			if (_oldEach != null)
				_page.setVariable("each", _oldEach);
			else
				_page.unsetVariable("each");
		}
		_it = null; _status = null; //recycle (just in case)
		return false;
	}

	private void prepare(Object o) {
		if (o instanceof Collection) {
			_it = ((Collection)o).iterator();
		} else if (o instanceof Map) {
			_it = ((Map)o).entrySet().iterator();
		} else if (o instanceof Iterator) {
			_it = (Iterator)o;
		} else if (o instanceof Enumeration) {
			_it = new CollectionsX.EnumerationIterator((Enumeration)o);
		} else if (o instanceof Object[]) {
			_it = new CollectionsX.ArrayIterator((Object[])o);
		} else if (o instanceof int[]) {
			final int[] ary = (int[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Integer(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof long[]) {
			final long[] ary = (long[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Long(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof short[]) {
			final short[] ary = (short[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Short(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof byte[]) {
			final byte[] ary = (byte[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Byte(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof float[]) {
			final float[] ary = (float[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Float(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof double[]) {
			final double[] ary = (double[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Double(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof char[]) {
			final char[] ary = (char[])o;
			_it = new Iterator() {
				private int _j = 0;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Character(ary[_j]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else {
			_it = new CollectionsX.OneIterator(o);
		}
	}
	private static class Status implements ForEachStatus {
		private final Object previous;
		private Object each;
		private int index = -1;

		private Status(Object previous) {
			this.previous = previous;
		}

		public ForEachStatus getPrevious() {
			return this.previous instanceof ForEachStatus ?
				(ForEachStatus)this.previous: null;
		}
		public Object getEach() {
			return this.each;
		}
		public int getIndex() {
			return this.index;
		}
	}
}
