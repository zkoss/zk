/* SystemValidators.java

	Purpose:
		
	Description:
		
	History:
		

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.Validator;
import org.zkoss.bind.validator.DeferredValidator;
import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.UiException;

/**
 * To keep system level validators, the built-in validators are initialized when first accessing.
 * @author dennis
 * @since 6.0.1
 */
public class SystemValidators {
	private static final Log _log = Log.lookup(SystemValidators.class);
	private static final Map<String, Validator> _validators = new HashMap<String, Validator>();
	private static boolean _init = false;
	
	static void initBuiltinValidators() {
		//built in
		synchronized(_validators){
			//built in
			set0("beanValidator", new DeferredValidator("org.zkoss.bind.validator.BeanValidator"));//defer the init of validator.(user might not use this validator)
			_init = true;
		}
	}
	
	static private void init(){
		if(_init) return;
		synchronized(_validators){
			if(_init) return;
			initBuiltinValidators();
			_init = true;
		}
	}
	
	static public void set(String name,Validator validator){
		init();
		set0(name,validator);
	}
	
	static private void set0(String name,Validator validator){
		if(_log.debugable()){
			_log.debug("set validator [%s]=[%s]",name,validator);
		}
		synchronized(_validators){
			_validators.put(name, validator);
		}
	}
	
	public static Validator get(String name) {
		init();
		Validator v = _validators.get(name);
		if (v == null && name.indexOf('.') > 0) { //might be a class path
			try {
				v = (Validator) Classes.newInstanceByThread(name);
				set(name, v); //assume state-less
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
		}
		return v;
	}
}
