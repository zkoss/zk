/* ForEachImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 14:21:08     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;

import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * An implementation of {@link ForEach}.
 *
 * <p>Note: the use of {@link ForEachImpl} is different from
 * {@link ConditionImpl}. While you could use the same instance of
 * {@link ConditionImpl} for all evaluation, each instance of
 * {@link ForEachImpl} can be used only once (drop it after {@link #next}
 * returns false).
 *
 * @author tomyeh
 */
public class ForEachImpl implements ForEach {
	private final EvaluatorRef _evalr;
	private final Page _page;
	private final Component _comp;
	private final ExValue[] _expr;
	private final ExValue _begin, _end;
	private Status _status;
	private Iterator _it;
	private Object _oldEach;
	private boolean _done;

	/** Returns an instance that represents the iterator for the
	 * specified collection.
	 *
	 * @param expr an array of expressions. There are two formats.
	 * <ol>
	 * <li>length == 1: it iterates thru the content of expr[0].
	 * For example, if expr[0] is an array, all items in this array will
	 * be iterated.</li>
	 * <li>length > 1, it will iterate thru expr[0]</li>
	 * <li>length == 0 or expr is null, null is returned</li>
	 * </ol>
	 * @since 3.0.6
	 */
	public static
	ForEach getInstance(EvaluatorRef evalr, Component comp,
	ExValue[] expr, ExValue begin, ExValue end) {
		if (expr == null || expr.length == 0)
			return null;
		return new ForEachImpl(evalr, comp, expr, begin, end);
	}
	/** Returns an instance that represents the iterator for the
	 * specified collection, or null if expr is null or empty.
	 *
	 * @param expr an array of expressions. There are two formats.
	 * <ol>
	 * <li>length == 1: it iterates thru the content of expr[0].
	 * For example, if expr[0] is an array, all items in this array will
	 * be iterated.</li>
	 * <li>length > 1, it will iterate thru expr[0]</li>
	 * <li>length == 0 or expr is null, null is returned</li>
	 * </ol>
	 * @since 3.0.6
	 */
	public static
	ForEach getInstance(EvaluatorRef evalr, Page page,
	ExValue[] expr, ExValue begin, ExValue end) {
		if (expr == null || expr.length == 0)
			return null;
		return new ForEachImpl(evalr, page, expr, begin, end);
	}

	/** Constructor.
	 * In most cases, use {@link #getInstance(EvaluatorRef, Component, ExValue[], ExValue, ExValue)}
	 * instead of this constructor.
	 * @exception IllegalArgumentException if comp or evalr is null
	 * @since 3.0.6
	 */
	public ForEachImpl(EvaluatorRef evalr, Component comp,
	ExValue[] expr, ExValue begin, ExValue end) {
		if (comp == null || evalr == null)
			throw new IllegalArgumentException();

		_evalr = evalr;
		_page = null;
		_comp = comp;
		_expr = expr;
		_begin = begin;
		_end = end;
	}
	/** Constructor.
	 * In most cases, use {@link #getInstance(EvaluatorRef, Component, ExValue[], ExValue, ExValue)}
	 * instead of this constructor.
	 * @exception IllegalArgumentException if page or evalr is null
	 * @since 3.0.6
	 */
	public ForEachImpl(EvaluatorRef evalr, Page page, ExValue[] expr, ExValue begin, ExValue end) {
		if (page == null || evalr == null)
			throw new IllegalArgumentException();

		_evalr = evalr;
		_page = page;
		_comp = null;
		_expr = expr;
		_begin = begin;
		_end = end;
	}

	/** Returns an instance that represents the iterator for the
	 * specified collection, or null if expr is null or empty.
	 *
	 * @param expr an EL expression that shall return a collection of objects.
	 * @see #getInstance(EvaluatorRef, Component, ExValue[], ExValue, ExValue)
	 */
	public static
	ForEach getInstance(EvaluatorRef evalr, Component comp, String expr, String begin, String end) {
		if (expr == null || expr.length() == 0)
			return null;
		return new ForEachImpl(evalr, comp, expr, begin, end);
	}
	/** Returns an instance that represents the iterator for the
	 * specified collection, or null if expr is null or empty.
	 *
	 * @param expr an EL expression that shall return a collection of objects.
	 * @since 3.0.0
	 * @see #getInstance(EvaluatorRef, Page, ExValue[], ExValue, ExValue)
	 */
	public static
	ForEach getInstance(EvaluatorRef evalr, Page page, String expr, String begin, String end) {
		if (expr == null || expr.length() == 0)
			return null;
		return new ForEachImpl(evalr, page, expr, begin, end);
	}

	/** Constructor.
	 * In most cases, use {@link #getInstance(EvaluatorRef, Component, String, String, String)}
	 * instead of this constructor.
	 * @exception IllegalArgumentException if comp or evalr is null
	 * @since 3.0.0
	 * @see #ForEachImpl(EvaluatorRef, Component, ExValue[], ExValue, ExValue)
	 */
	public ForEachImpl(EvaluatorRef evalr, Component comp, String expr, String begin, String end) {
		if (comp == null || evalr == null)
			throw new IllegalArgumentException();

		_evalr = evalr;
		_page = null;
		_comp = comp;
		_expr = expr != null ? new ExValue[] {new ExValue(expr, Object.class)}: null;
		_begin = begin != null && begin.length() > 0 ? new ExValue(begin, Integer.class): null;
		_end = end != null && end.length() > 0 ? new ExValue(end, Integer.class): null;
	}
	/** Constructor.
	 * In most cases, use {@link #getInstance(EvaluatorRef, Component, String, String, String)}
	 * instead of this constructor.
	 * @exception IllegalArgumentException if page or evalr is null
	 * @since 3.0.0
	 * @see #ForEachImpl(EvaluatorRef, Page, ExValue[], ExValue, ExValue)
	 */
	public ForEachImpl(EvaluatorRef evalr, Page page, String expr, String begin, String end) {
		if (page == null || evalr == null)
			throw new IllegalArgumentException();

		_evalr = evalr;
		_page = page;
		_comp = null;
		_expr = expr != null ? new ExValue[] {new ExValue(expr, Object.class)}: null;
		_begin = begin != null && begin.length() > 0 ? new ExValue(begin, Integer.class): null;
		_end = end != null && end.length() > 0 ? new ExValue(end, Integer.class): null;
	}
	private Object eval(ExValue value) {
		return value == null ? null:
			_comp != null ?
				value.getValue(_evalr, _comp): value.getValue(_evalr, _page);
	}

	//-- ForEach --//
	public boolean next() {
		if (_done)
			throw new IllegalStateException("Iterate twice not allowed");

		if (_status == null) {
			//Bug 2188572: we have to evaluate _expr before setupStatus
			final Object o;
			if (_expr == null || _expr.length == 0) {
				o = null;
			} else if (_expr.length == 1) {
				o = eval(_expr[0]);
			} else {
				Object[] ary = new Object[_expr.length];
				for (int j = 0; j < _expr.length; ++j)
					ary[j] = eval(_expr[j]);
				o = ary;
			}

			if (o == null) {
				_done = true;
				return false;
			}

			//Bug 1786154: we have to prepare _status first since _expr,
			//_begin or _end might depend on it
			setupStatus();

			Integer ibeg = (Integer)eval(_begin);
			int vbeg = ibeg != null ? ibeg.intValue(): 0;
			if (vbeg < 0) ibeg = new Integer(vbeg = 0);
			_status.setBegin(ibeg);
			_status.setEnd((Integer)eval(_end));

			prepare(o, vbeg); //prepare iterator
		}

		if ((_status.end == null || _status.index < _status.end.intValue())
		&& _it.hasNext()) {
			++_status.index;
			_status.each = _it.next();
			if (_comp != null) _comp.setAttribute("each", _status.each);
			else _page.setAttribute("each", _status.each);
			return true;
		}

		//restore
		_done = true;
		restoreStatus();
		return false;
	}
	private void setupStatus() {
		final Scope scope = _comp != null ? (Scope)_comp: _page;
		_oldEach = scope.getAttribute("each", true);
		_status = new Status(scope.getAttribute("forEachStatus", true));
		scope.setAttribute("forEachStatus", _status);
	}
	private void restoreStatus() {
		final Scope scope = _comp != null ? (Scope)_comp: _page;
		if (_status.previous != null)
			scope.setAttribute("forEachStatus", _status.previous);
		else
			scope.removeAttribute("forEachStatus");
		if (_oldEach != null)
			scope.setAttribute("each", _oldEach);
		else
			scope.removeAttribute("each");
		_it = null; _status = null; //recycle (just in case)
	}

	private void prepare(Object o, final int begin) {
		if (begin > 0 && (o instanceof List)) {
			final List l = (List)o;
			final int size = l.size();
			_it = l.listIterator(begin > size ? size: begin);
		} else if (o instanceof Collection) {
			_it = ((Collection)o).iterator();
			forward(begin);
		} else if (o instanceof Map) {
			_it = ((Map)o).entrySet().iterator();
			forward(begin);
		} else if (o instanceof Iterator) {
			_it = (Iterator)o;
			forward(begin);
		} else if (o instanceof Enumeration) {
			_it = new CollectionsX.EnumerationIterator((Enumeration)o);
			forward(begin);
		} else if (o instanceof Object[]) {
			final Object[] ary = (Object[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return ary[_j++];
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof int[]) {
			final int[] ary = (int[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Integer(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof long[]) {
			final long[] ary = (long[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Long(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof short[]) {
			final short[] ary = (short[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Short(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof byte[]) {
			final byte[] ary = (byte[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Byte(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof double[]) {
			final double[] ary = (double[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Double(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof float[]) {
			final float[] ary = (float[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Float(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof char[]) {
			final char[] ary = (char[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return new Character(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else if (o instanceof boolean[]) {
			final boolean[] ary = (boolean[])o;
			_it = new Iterator() {
				private int _j = begin;
				public boolean hasNext() {
					return _j < ary.length;
				}
				public Object next() {
					return Boolean.valueOf(ary[_j++]);
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} else {
			_it = new CollectionsX.OneIterator(o);
			forward(begin);
		}
	}
	private void forward(int begin) {
		while (--begin >= 0 && _it.hasNext())
			_it.next();
	}
	private static class Status implements ForEachStatus {
		private final Object previous;
		private Object each;
		private int index;
		private Integer begin, end;

		private Status(Object previous) {
			this.previous = previous;
			this.index = -1;
		}
		private void setBegin(Integer begin) {
			this.begin = begin;
			this.index = begin != null ? begin.intValue() - 1: -1;
		}
		private void setEnd(Integer end) {
			this.end = end;
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
		public Integer getBegin() {
			return this.begin;
		}
		public Integer getEnd() {
			return this.end;
		}
	}
}
