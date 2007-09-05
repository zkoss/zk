/* WriterHelper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/9/5 下午 7:49:42     2007, Created by Dennis.Chen
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
 *
 */
/**package**/ class WriterHelper {
	Writer _w;
	
	public WriterHelper(Writer writer){
		_w = writer;
	}
	
	/**
	 * Write str. 
	 * If str is null or str is empty after trim() then nothing will be written.
	 * @param str a string to be written
	 * @return
	 * @throws IOException
	 */
	public WriterHelper write(String str) throws IOException{
		return write(str,true,true);
	}
	
	/**
	 * Write new line.
	 * @return
	 * @throws IOException
	 */
	public WriterHelper writeln() throws IOException{
		_w.write("\n");
		return this;
	}
	
	/**
	 * Write str , and then write a new line.
	 * @param str a string to be written
	 * @return
	 * @throws IOException
	 */
	public WriterHelper writeln(String str) throws IOException{
		write(str,true,true);
		_w.write("\n");
		return this;
	}
	
	/**
	 * Write str. If skipEmpty is true, then nothing will be written when str is null or empty.
	 * This method doesn't trim str when writting.
	 * @param str
	 * @param skipEmpty
	 * @return
	 * @throws IOException
	 */
	public WriterHelper write(String str,boolean skipEmpty) throws IOException{
		write(str,skipEmpty,false);
		return this;
	}
	
	/**
	 * Write str. If skipEmpty is true, then nothing will be written when str is null or empty.
	 * If trim is true, then a trimed string will be written. 
	 * @param str a string to be written
	 * @param skipEmpty skip write if str is null or empty string.
	 * @param trim trim str when write.
	 * @return
	 * @throws IOException
	 */
	public WriterHelper write(String str,boolean skipEmpty,boolean trim) throws IOException{
		if(skipEmpty && (str==null||str.trim().length()==0)){
			return this;
		}
		if(trim && str!=null){
			str = str.trim();
		}
		_w.write(str);
		
		return this;
	}
	
	
}
