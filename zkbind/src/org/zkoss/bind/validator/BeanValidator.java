/* BeanValidator.java

	Purpose:
		
	Description:
		
	History:
		2011/12/22 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;

/**
 * A <a href="http://jcp.org/en/jsr/detail?id=303"/>JSR 303</a> compatible validator. <br/>
 * Before use this validator, you have to configure your environment (depends on the implementation you chosen)<br/>
 * Here is a article <a href="http://books.zkoss.org/wiki/Small_Talks/2011/May/Integrate_ZK_with_JSR_303:_Bean_Validation#How_to_use_Bean_Validation_in_your_ZK_application">Integrate ZK with JSR 303: Bean Validation</a> 
 * talks about how to set up JSR 303 in ZK with Hibernate implementation.
 * 
 * @author dennis
 *
 */
public class BeanValidator extends AbstractValidator {

	
	
	protected Validator getValidator(){
		return BeanValidations.getValidator();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<ConstraintViolation<?>> validate(Class clz, String property, Object value){
		return getValidator().validateValue(clz, property, value);
	}
	
	
	/**
	 * Sort the viloations, make multiple violation order more predictable.
	 * By default, sort it by the constraint name. 
	 * @param viloations
	 */
	protected void sort(List<ConstraintViolation<?>> viloations){
		Collections.sort(viloations, new Comparator<ConstraintViolation<?>>() {
			@Override
			public int compare(ConstraintViolation<?> o1, ConstraintViolation<?> o2) {
				String s1 = o1.getConstraintDescriptor().getAnnotation().toString();
				String s2 = o2.getConstraintDescriptor().getAnnotation().toString();
				return s1.compareTo(s2);
			}
		});
	}
	
	@Override
	public void validate(ValidationContext ctx) {
		final Property p = ctx.getProperty();
		final Object base = p.getBase();
		final Class<?> clz = base.getClass();
		final String name = p.getProperty();
		final Object value = p.getValue();
		
		//TODO have to handle the Form case. or(use another validator?)  
		 
		Set<ConstraintViolation<?>> violations = validate(clz, name, value);
		final int s = violations.size();
		
		if (s == 1) {
			addInvalidMessages(ctx, new String[] { violations.iterator().next().getMessage() });
		} else if (s > 0) {
			String[] msgs = new String[violations.size()];
			// it is a Set, I tested in hibernate 4 , it doesn't guarantee the
			// order, so I sort it by annottion.toString
			List<ConstraintViolation<?>> l = new ArrayList<ConstraintViolation<?>>(violations);
			sort(l);
			
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = l.get(i).getMessage();
			}
			addInvalidMessages(ctx, msgs);
		}

	}
}
