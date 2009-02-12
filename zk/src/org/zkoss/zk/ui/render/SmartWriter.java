/* SmartWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/9/5 19:49:42     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.render;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;

/**
 * A writer that is used to simplify the output generation
 * of compoennts.
 * It is mainly used to implement {@link ComponentRenderer}.
 *
 * <p>Spec Note: we cannot extend it from {@link Writer},
 * since we want {@link #write(String)} to return this object,
 * such that the user can write<br>
 * <code>sw.write("<div id=\"").write(comp.getUuid()).write('"')</code>
 *
 * @author Dennis.Chen
 * @author tomyeh
 * @since 3.0.0
 */
public final class SmartWriter /*extends Writer*/ {
	/** For better performance we don't get the system default
	 * (from the line.separator property).
	 */
	private static final String LF = "\n";

	private final Writer _w;
	
	public SmartWriter(Writer writer){
		_w = writer;
	}

	//Writer//
	public void close() throws IOException {
		_w.close();
	}
	public void flush() throws IOException {
		_w.flush();
	}

	//extra//
	/** Write a component.
	 * It works even if the component is null.
	 */
	public SmartWriter write(Component comp) throws IOException {
		if (comp != null)
			comp.redraw(_w);
		return this;
	}
	/** Write a component.
	 * It works even if the component is null.
	 */
	public SmartWriter writeln(Component comp) throws IOException {
		return write(comp).write(LF);
	}
	/** Writes all children.
	 */
	public SmartWriter writeChildren(Component comp) throws IOException {
		return writeComponents(comp.getChildren());
	}
	/** Writes children in the specified range.
	 *
	 * @param from the first index (included).
	 * @param to the last index (included).
	 */
	public SmartWriter writeChildren(Component comp, int from, int to)
	throws IOException {
		return writeComponents(comp.getChildren(), from, to);
	}
	/** Writes a list of components.
	 */
	public SmartWriter writeComponents(Collection comps) throws IOException {
		for (Iterator it = comps.iterator(); it.hasNext();)
			((Component)it.next()).redraw(_w);
		return this;
	}
	/** Writes a list of component in the specified range.
	 *
	 * @param from the first index (included).
	 * @param to the last index (included).
	 */
	public SmartWriter writeComponents(List comps, final int from, final int to)
	throws IOException {
		if (from < comps.size()) {
			ListIterator it = comps.listIterator(from);
			for (int cnt = to - from + 1; it.hasNext() && --cnt >= 0;)
				((Component)it.next()).redraw(_w);
		}
		return this;
	}

	/**
	 * Write a string. 
	 * If str is null, nothing will be written.
	 *
	 * @param str a string to be written
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public SmartWriter write(String str) throws IOException{
		if (str != null) _w.write(str);
		return this;
	}
	/**
	 * Write new line.
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public SmartWriter writeln() throws IOException{
		_w.write(LF);
		return this;
	}
	
	/**
	 * Write a string, and then write a new line.
	 * If str is null, nothing will be written.
	 *
	 * @param str a string to be written
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public SmartWriter writeln(String str) throws IOException{
		if (str != null) {
			_w.write(str);
			_w.write(LF);
		}
		return this;
	}
	
	/**
	 * Write a string.
	 * If trim is true, then a trimed string will be written. 
	 * If str is null, nothing will be written.
	 *
	 * @param str a string to be written
	 * @param trim trim str when write.
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public SmartWriter write(String str, boolean trim) throws IOException {
		if (str != null)
			_w.write(trim ? str.trim(): str);
		return this;
	}
	/**
	 * Write a string, and then write a new line.
	 * If trim is true, then a trimed string will be written. 
	 * If str is null, nothing will be written.
	 *
	 * @param str a string to be written
	 * @param trim trim str when write.
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public SmartWriter writeln(String str, boolean trim) throws IOException {
		if (str != null) {
			_w.write(trim ? str.trim(): str);
			_w.write(LF);
		}
		return this;
	}

	/** Writes a boolean.
	 */
	public SmartWriter write(boolean b) throws IOException {
		_w.write(Boolean.toString(b));
		return this;
	}
	/** Writes a char.
	 */
	public SmartWriter write(char c) throws IOException {
		_w.write(Character.toString(c));
		return this;
	}
	/** Writes a char with a line feed.
	 */
	public SmartWriter writeln(char c) throws IOException {
		_w.write(Character.toString(c));
		_w.write(LF);
		return this;
	}
	/** Writes a byte.
	 */
	public SmartWriter write(byte v) throws IOException {
		_w.write(Byte.toString(v));
		return this;
	}
	/** Writes a short.
	 */
	public SmartWriter write(short v) throws IOException {
		_w.write(Short.toString(v));
		return this;
	}
	/** Writes an integer.
	 */
	public SmartWriter write(int v) throws IOException {
		_w.write(Integer.toString(v));
		return this;
	}
	/** Writes a float.
	 */
	public SmartWriter write(float v) throws IOException {
		_w.write(Float.toString(v));
		return this;
	}
	/** Writes a double.
	 */
	public SmartWriter write(double v) throws IOException {
		_w.write(Double.toString(v));
		return this;
	}

	/** Writes an attribute.
	 * The output is generated only if val is not null (and not empty).
	 */
	public SmartWriter writeAttr(String name, Object val)
	throws IOException {
		if (val != null
		&& (!(val instanceof String) || ((String)val).length() != 0))
			return write(" ").write(name).write("=\"")
				.write(val.toString()).write("\"");
		return this;
	}
}
