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
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;

/**
 * A helper class for writting output.
 * @author Dennis.Chen
 */
/*package*/ final class WriterHelper {
	Writer _w;
	
	public WriterHelper(Writer writer){
		_w = writer;
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
}
