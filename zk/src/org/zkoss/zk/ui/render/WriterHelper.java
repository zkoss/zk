/* WriterHelper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/9/5 19:49:42     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;

/**
 * A helper class that is used to simplify the generation
 * of the output when {@link ComponentRenderer} is implemented.
 *
 * @author Dennis.Chen
 * @since 3.0.0
 */
public final class WriterHelper {
	private final Writer _w;
	
	public WriterHelper(Writer writer){
		_w = writer;
	}

	/** Write a component.
	 * It works even if the component is null.
	 */
	public WriterHelper write(Component comp) throws IOException {
		if (comp != null)
			comp.redraw(_w);
		return this;
	}
	/** Write a component.
	 * It works even if the component is null.
	 */
	public WriterHelper writeln(Component comp)
	throws IOException {
		return write(comp).write("\n");
	}

	/**
	 * Write a string. 
	 * If str is null, nothing will be written.
	 *
	 * @param str a string to be written
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public WriterHelper write(String str) throws IOException{
		if (str != null) _w.write(str);
		return this;
	}
	/**
	 * Write new line.
	 * @return this object
	 * @throws IOException if failed to write
	 */
	public WriterHelper writeln() throws IOException{
		_w.write("\n");
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
	public WriterHelper writeln(String str) throws IOException{
		if (str != null) {
			_w.write(str);
			_w.write("\n");
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
	public WriterHelper write(String str, boolean trim) throws IOException {
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
	public WriterHelper writeln(String str, boolean trim) throws IOException {
		if (str != null) {
			_w.write(trim ? str.trim(): str);
			_w.write("\n");
		}
		return this;
	}

	/** Writes a boolean.
	 */
	public WriterHelper write(boolean b) throws IOException {
		_w.write(Boolean.toString(b));
		return this;
	}
	/** Writes a char.
	 */
	public WriterHelper write(char c) throws IOException {
		_w.write(Character.toString(c));
		return this;
	}
	/** Writes a byte.
	 */
	public WriterHelper write(byte v) throws IOException {
		_w.write(Byte.toString(v));
		return this;
	}
	/** Writes a short.
	 */
	public WriterHelper write(short v) throws IOException {
		_w.write(Short.toString(v));
		return this;
	}
	/** Writes an integer.
	 */
	public WriterHelper write(int v) throws IOException {
		_w.write(Integer.toString(v));
		return this;
	}
	/** Writes a float.
	 */
	public WriterHelper write(float v) throws IOException {
		_w.write(Float.toString(v));
		return this;
	}
	/** Writes a double.
	 */
	public WriterHelper write(double v) throws IOException {
		_w.write(Double.toString(v));
		return this;
	}

	/** Writes an attribute.
	 * The output is generated only if val is not null (and not empty).
	 */
	public WriterHelper writeAttr(String name, Object val)
	throws IOException {
		if (val != null
		&& (!(val instanceof String) || ((String)val).length() != 0))
			return write(" ").write(name).write("=\"")
				.write(val.toString()).write("\"");
		return this;
	}
}
