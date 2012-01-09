/* CompositeValidator.java

	Purpose:
		
	Description:
		
	History:
		2011/12/28 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;

/**
 * A composite validator that contains mulitple validator and forward the validation to them.   
 * @author dennis
 * @since 6.0.0
 */
public class CompositeValidator implements Validator,Serializable{
	private static final long serialVersionUID = 6545009126528775045L;
	private List<Validator> _validators;
	
	//I don't provide addValidator method since this class might be share between binders, 
	//if we provide addValidator, then we have to consider the thread-safe issue in validate();
	
	public CompositeValidator(List<Validator> validator){
		_validators = new ArrayList<Validator>(validator);
	}
	public CompositeValidator(Validator... validators){
		_validators = new ArrayList<Validator>();
		for(Validator v:validators){
			_validators.add(v);
		}
	}
	@Override
	public void validate(ValidationContext ctx) {
		for(Validator v:_validators){
			v.validate(ctx);
		}
	}
}
