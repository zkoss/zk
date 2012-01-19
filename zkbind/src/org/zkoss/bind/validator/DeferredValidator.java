/* DeferredValidator.java

	Purpose:
		
	Description:
		
	History:
		2011/12/28 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.validator;

import java.io.Serializable;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.UiException;


/**
 * A deferred validator that defers the initialization of the real validator,
 * a validator needs 3-party jar files could still be installed if user doesn't provide it (ex, BeanValidator of JSR 303).   
 * @author dennis
 * @since 6.0.0
 */
public class DeferredValidator implements Validator,Serializable{
	private static final long serialVersionUID = 6545009126528775045L;
	private String _clzName;
	private Class<Validator> _clz;
	private volatile Validator _target;
	
	public DeferredValidator(String clzName){
		_clzName = clzName;
	}
	
	public DeferredValidator(Class<Validator> clz){
		_clz = clz;
	}
	
	@SuppressWarnings("unchecked")
	private Validator getValidator() throws Exception{
		if(_target==null){
			synchronized(this){
				if(_target==null){
					if(_clz==null){
						_clz = (Class<Validator>)Classes.forNameByThread(_clzName);
					}
					_target = (Validator)_clz.newInstance();
				}
			}
		}
		return _target;
	}
	
	@Override
	public void validate(ValidationContext ctx) {
		try {
			getValidator().validate(ctx);
		} catch (Exception e) {
			throw new UiException(e.getMessage(),e);
		}
	}

	

}
