/* ForEach.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:11     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.StringWriter;
import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;

/**
 * Iterators thru a collection/array of items.
 * 
 * @author tomyeh
 */
public class ForEach extends AbstractAction {
	private String _var, _varStatus;
	private Object _items;
	private int _beg = 0, _end = Integer.MAX_VALUE;
	private boolean _trim = true;
	private boolean _endSpecified, _itemsSpecified;

	/** Returns the variable name used to iterate thru items. */
	public String getVar() {
		return _var;
	}
	/** Sets the variable name used to iterate thru items. */
	public void setVar(String var) {
		_var = var;
	}
	/** Returns the variable name used to hold the current iteration
	 * status, an instance of {@link LoopStatus}.
	 */
	public String getVarStatus() {
		return _varStatus;
	}
	/** Sets the variable name used to hold the current iteration status.
	 */
	public void setVarStatus(String varStatus) {
		_varStatus = varStatus;
	}
	/** Returns the attribute items. */
	public Object getItems() {
		return _items;
	}
	/** Sets the attribute items. */
	public void setItems(Object items) {
		_items = items;
		_itemsSpecified = true;
	}

	/** Returns the index of the item at which the iteration begins.
	 */
	public int getBegin() {
		return _beg;
	}
	/** Sets the index of the item at which the iteration begins.
	 * <p>Default: 0.
	 */
	public void setBegin(int beg) {
		if (beg < 0)
			throw new IllegalArgumentException("Non-negative only");
		_beg = beg;
	}
	/** Returns the index of the item at which the iteration ends (inclusive).
	 */
	public int getEnd() {
		return _end;
	}
	/** Sets the index of the item at which the iteration ends (inclusive).
	 * <p>Default: Integer.MAX_VALUE.
	 */
	public void setEnd(int end) {
		_end = end;
		_endSpecified = true;
	}

	/** Returns whether to trim the result. */
	public boolean isTrim() {
		return _trim;
	}
	/** Sets whether to trim the result.
	 * <p>Default: true.
	 */
	public void setTrim(boolean trim) {
		_trim = trim;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		//at least items or end must be specified
		if (!nested || (_itemsSpecified && _items == null)
		|| (_endSpecified && _end < _beg)
		|| (!_itemsSpecified && !_endSpecified) || !isEffective())
			return;

		final Object old1 =
			_var != null ? ac.getAttribute(_var, ac.PAGE_SCOPE): null;
		final Object old2;
		final Status st;
		if (_varStatus != null) {
			old2 = ac.getAttribute(_varStatus, ac.PAGE_SCOPE);
			ac.setAttribute(_varStatus, st = new Status(), ac.PAGE_SCOPE);
		} else {
			old2 = null;
			st = null;
		}

		if (_items == null) { //use begin and end only
			renderWith(ac, st);
		} else if (_items.getClass().isArray()) {
			if (_items instanceof Object[])
				renderWith(ac, st, (Object[])_items);
			else if (_items instanceof int[])
				renderWith(ac, st, (int[])_items);
			else if (_items instanceof short[])
				renderWith(ac, st, (short[])_items);
			else if (_items instanceof long[])
				renderWith(ac, st, (long[])_items);
			else if (_items instanceof byte[])
				renderWith(ac, st, (byte[])_items);
			else if (_items instanceof char[])
				renderWith(ac, st, (char[])_items);
			else if (_items instanceof double[])
				renderWith(ac, st, (double[])_items);
			else if (_items instanceof float[])
				renderWith(ac, st, (float[])_items);
			else
				throw new InternalError("Unknown "+_items.getClass());
		} else if (_beg > 0 && (_items instanceof List)) {
			final List l = (List)_items;
			final int size = l.size();
			renderWith(ac, st, l.listIterator(_beg > size ? size: _beg));
		} else if (_items instanceof Collection) {
			renderWith(ac, st, ((Collection)_items).iterator());
		} else if (_items instanceof Map) {
			renderWith(ac, st, ((Map)_items).entrySet().iterator());
		} else if (_items instanceof Iterator) {
			renderWith(ac, st, (Iterator)_items);
		} else if (_items instanceof Enumeration) {
			renderWith(ac, st, (Enumeration)_items);
		} else if (_items instanceof String) {
			renderWith(ac, st, (String)_items);
		} else {
			throw new DspException(MWeb.DSP_UNKNOWN_ATTRIBUTE_VALUE,
				new Object[] {this, "items", new Integer(ac.getLineNumber())});
		}

		if (_var != null) ac.setAttribute(_var, old1, ac.PAGE_SCOPE);
		if (_varStatus != null) ac.setAttribute(_varStatus, old2, ac.PAGE_SCOPE);
	}

	private void renderWith(ActionContext ac, Status st)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j <= _end; ++j) {
			final Object val = new Integer(j);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, ListIterator it)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = 0, cnt = _end - _beg + 1; it.hasNext() && --cnt >= 0; ++j) {
			final Object val = it.next();
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, Iterator it)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);

		for (int j = 0; ++j <= _beg && it.hasNext();) //skip
			it.next();

		for (int j = 0, cnt = _end - _beg + 1; it.hasNext() && --cnt >= 0; ++j) {
			final Object val = it.next();
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, Enumeration enm)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);

		for (int j = 0; ++j <= _beg && enm.hasMoreElements();) //skip
			enm.nextElement();

		for (int j = 0, cnt = _end - _beg + 1; enm.hasMoreElements() && --cnt >= 0; ++j) {
			final Object val = enm.nextElement();
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, Object[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = ary[j];
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, int[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Integer(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, short[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Short(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, long[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Long(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, char[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Character(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, byte[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Byte(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, float[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Float(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, double[] ary)
	throws DspException, IOException {
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg; j < ary.length && j <= _end; ++j) {
			final Object val = new Double(ary[j]);
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(j, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}
	private void renderWith(ActionContext ac, Status st, String txt)
	throws DspException, IOException {
		final StringBuffer sb = new StringBuffer();
		int idx = 0;
		final StringWriter out = getFragmentOut(ac, _trim);
		for (int j = _beg, len = txt.length(); j < len && j <= _end; ++j) {
			char cc = txt.charAt(j);
			if (cc == ',') {
				final Object val = sb.toString();
				if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
				if (st != null) st.update(idx++, val);
				renderFragment(ac, out, _trim);
				sb.setLength(0);
			} else if (cc == '\\' && j + 1 < len) {
				cc = txt.charAt(j + 1);
				switch (cc) {
				case 'n': cc = '\n'; break;
				case 'r': cc = '\r'; break;
				case 't': cc = '\t'; break;
				case 'b': cc = '\b'; break;
				}
			}
			sb.append(cc);
		}
		if (sb.length() > 0) {
			final Object val = sb.toString();
			if (_var != null) ac.setAttribute(_var, val, ac.PAGE_SCOPE);
			if (st != null) st.update(idx++, val);
			renderFragment(ac, out, _trim);
		}
		if (out != null)
			ac.getOut().write(out.toString());
	}

	//-- Object --//
	public String toString() {
		return "forEach";
	}
	private static class Status implements LoopStatus {
		private int _j;
		private Object _cur;
		public int getIndex() {
			return _j;
		}
		public Object getCurrent() {
			return _cur;
		}
		private void update(int j, Object cur) {
			_j = j;
			_cur = cur;
		}
	}
}
