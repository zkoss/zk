/* SystemConverters.java

	Purpose:
		
	Description:
		
	History:
		

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.Converter;
import org.zkoss.bind.converter.FormatedDateConverter;
import org.zkoss.bind.converter.FormatedNumberConverter;
import org.zkoss.bind.converter.FormattedTimeConverter;
import org.zkoss.bind.converter.ObjectBooleanConverter;
import org.zkoss.bind.converter.UriConverter;
import org.zkoss.bind.converter.sys.ChildrenBindingConverter;
import org.zkoss.bind.converter.sys.DefaultJSONBindingParamConverter;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.UiException;
/**
 * To keep system level converters, the built-in converters are initialized when first accessing.
 * @author dennis
 * @since 6.0.1
 */
public class SystemConverters {
	private static final Logger _log = LoggerFactory.getLogger(SystemConverters.class);
	private static final Map<String, Converter> _converters = new HashMap<String, Converter>();
	private static boolean _init = false;
	
	static void initBuiltinConverters() {
		//built in formattedDate and formattedNumber
		synchronized(_converters){
			set0("objectBoolean", new ObjectBooleanConverter());
			
			set0("formatedDate", new FormatedDateConverter());
			//Ian Tsai, bug-1157, correct the spelling of "formated" to "formatted"
			set0("formattedDate", new FormatedDateConverter());
			
			set0("formatedNumber", new FormatedNumberConverter());
			//Ian Tsai, bug-1157, correct the spelling of "formated" to "formatted"
			set0("formattedNumber", new FormatedNumberConverter());
			
			set0("uri", new UriConverter());
			
			//zk 1548
			set0("childrenBinding", new ChildrenBindingConverter());

			// ZK-2650 new feature.
			set0("jsonBindingParam", new DefaultJSONBindingParamConverter());
			
			//F80-ZK-2668: New Build-in Converter - FormattedTimeConverter
			set0("formattedTime", new FormattedTimeConverter());
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
		if(_log.isDebugEnabled()){
			_log.debug("set converter [%s]=[%s]",name,converter);
		}
		synchronized(_converters){
			_converters.put(name, converter);
		}
	}
	
	public static Converter get(String name) {
		init();
		Converter c;
		synchronized(_converters){
			c = _converters.get(name);
		}
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
