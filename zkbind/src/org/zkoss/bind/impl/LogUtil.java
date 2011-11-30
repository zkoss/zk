/* LogUtil.java

Purpose:
	
Description:
	
History:
	Aug 1, 2011 2:43:33 PM, Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provide better log format for simplify the logging
 * @author dennis
 *
 */
public class LogUtil {
	
	private Logger log;
	private boolean debug;
	
	@SuppressWarnings("unchecked")
	public LogUtil(Class clz){
		this(clz.getName());
	}
	public LogUtil(String name){
		log = Logger.getLogger(name);
		debug = log.isLoggable(Level.FINE);
	}
	
	public String format(String format,Object... args){
		String d = null;
		try{
			d = String.format(format, args);
		}catch(Exception x){
			d = x.getMessage()+":"+format;
		}
		return d;
	}
	
	public void debug(String format,Object... args){
		if(debug){
			String msg =format(format,args);
			log.fine(msg);
//			System.out.println(">>"+d);//more easy to read in console
		}
	}
	
	public void warn(String format,Object... args){
		String msg = format(format,args);
		log.warning(msg);
	}
}
