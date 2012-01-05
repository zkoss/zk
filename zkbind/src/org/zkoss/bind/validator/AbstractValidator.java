/* AbstractValidator.java

	Purpose:
		
	Description:
		
	History:
		2011/12/26 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.UiException;

/**
 * A abstract validator the handling validation message
 * 
 * @author dennis
 * @see ValidationMessages
 *
 */
public abstract class AbstractValidator implements Validator {

	private final static Log _log = Log.lookup(AbstractValidator.class); 
	
	/**
	 * add a message to validation context, when you call this method, it also set context invalid.
	 * @param ctx the validation context
	 * @param message the message of validation
	 */
	protected void addInvalidMessage(ValidationContext ctx,String message) {
		addInvalidMessages(ctx, new String[]{message});
	}
	
	/**
	 * add multiple messages to validation context, when you call this method, it also set context invalid.
	 * @param ctx the validation context
	 * @param messages messages of validation
	 */
	protected void addInvalidMessages(ValidationContext ctx,String[] messages) {
		ctx.setInvalid();
		ValidationMessages vmsgs = ((BinderCtrl)ctx.getBindContext().getBinder()).getValidationMessages();
		if(vmsgs!=null){
			Binding binding = ctx.getBindContext().getBinding();
			String attr = null;
			if(binding instanceof PropertyBinding){
				attr = ((PropertyBinding)binding).getFieldName();
			}else if(binding instanceof FormBinding){
				attr = ((FormBinding)binding).getFormId();
			}else{
				//ignore children binding;
			}
			if(attr!=null){
				vmsgs.setMessages(ctx.getBindContext().getComponent(),attr, messages);
			}
		}else{
			_log.warning("ValidationMessages not found on binder "+ctx.getBindContext().getBinder() + ", please init it");
		}
	}
}
