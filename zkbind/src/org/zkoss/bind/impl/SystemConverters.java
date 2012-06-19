/* SystemConverters.java

	Purpose:
		
	Description:
		
	History:
		

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.Converter;
import org.zkoss.bind.converter.FormatedDateConverter;
import org.zkoss.bind.converter.FormatedNumberConverter;
import org.zkoss.bind.converter.ObjectBooleanConverter;
import org.zkoss.bind.converter.UriConverter;
import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.UiException;
/**
 * To keep system level converters, the built-in converters are initialized when first accessing.
 * @author dennis
 * @since 6.0.1
 */
public class SystemConverters {
	private static final Log _log = Log.lookup(SystemConverters.class);
	private static final Map<String, Converter> _converters = new HashMap<String, Converter>();
	private static boolean _init = false;
	
	static void initBuiltinConverters() {
		//built in formattedDate and formattedNumber
		synchronized(_converters){
			set0("objectBoolean", new ObjectBooleanConverter());
			
			set0("formatedDate", new FormatedDateConverter());
			//bug-1157, correct the spelling of "formated" to "formatted"
			set0("formattedDate", new FormatedDateConverter());
			
			set0("formatedNumber", new FormatedNumberConverter());
			//bug-1157, correct the spelling of "formated" to "formatted"
			set0("formattedNumber", new FormatedNumberConverter());
			
			set0("uri", new UriConverter());
			_init = true;
		}
	}
	
	static private void init(){
		if(_init) return;
		synchronized(_converters){
			if(_init) return;
			initBuiltinConverters();
			_init = true;
		}
	}
	static public void set(String name,Converter converter){
		init();
		set0(name,converter);
	}
	static private void set0(String name,Converter converter){
		if(_log.debugable()){
			_log.debug("set converter [%s]=[%s]",name,converter);
		}
		synchronized(_converters){
			_converters.put(name, converter);
		}
	}
	
	public static Converter get(String name) {
		init();
		Converter c = _converters.get(name);
		if (c == null && name.indexOf('.') > 0) { //might be a class path
			try {
				c = (Converter) Classes.newInstanceByThread(name);
				set(name, c); //assume state-less
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
		}
		return c;
	}
}
