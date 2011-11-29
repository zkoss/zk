/* SaveBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 26, 2011 3:45:02 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;

/**
 * Binding for saving.
 * @author henrichen
 *
 */
public interface SaveBinding extends Binding {
	/**
	 * Save data from the source attribute into the target property.
	 * @param ctx the binding runtime context
	 */
	public void save(BindContext ctx);
	
	
	/**
	 * do the validation by validator
	 * @param vctx
	 */
	public void validate(ValidationContext vctx);
	
	/**
	 * Returns {@link Property} to be validated.
	 * @param ctx the binding runtime context
	 * @return {@link Property} to be validated.
	 */
	public Property getValidate(BindContext ctx);
//	public Set<Property> getValidates(BindContext ctx);
	
	/**
	 * Returns whether to do validation. which means, if true, than getValidator should not return null
	 * @return whether to do validation.
	 */
	public boolean hasValidator();
	
	/**
	 * return {@link Validator} to do validation
	 * @return the validator if existed
	 */
	public Validator getValidator();
	
	/**
	 * Returns an argument <tags, object> pairs map for validator. 
	 * @return an argument <tags, object> pairs map for validator.
	 */
	public Map<String, Object> getValidatorArgs();	
}
