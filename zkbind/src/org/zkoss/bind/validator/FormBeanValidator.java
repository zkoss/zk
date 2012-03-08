/* FormBeanValidator.java

	Purpose:
		
	Description:
		
	History:
		2012/3/5 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.zkoss.bind.Binder;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.UiException;
/**
 * A <a href="http://jcp.org/en/jsr/detail?id=303"/>JSR 303</a> compatible validator for a form-binding.<p/>  
 * Notice : Before use this validator, you have to configure your environment (depends on the implementation you chosen). 
 * Here is a article <a href="http://books.zkoss.org/wiki/Small_Talks/2011/May/Integrate_ZK_with_JSR_303:_Bean_Validation#How_to_use_Bean_Validation_in_your_ZK_application">Integrate ZK with JSR 303: Bean Validation</a> 
 * talks about how to set up JSR 303 in ZK with Hibernate implementation.
 * <p/> 
 * It validates all the saving properties of a bean and sets the invalid message by 
 * {@link AbstractValidator#addInvalidMessage(ValidationContext, String, String)}. <p/>
 * 
 * To use this class, you have to add <code>@validator('formBeanValidator',prefix='p_')</code> or <code>@validator('org.zkoss.bind.validator.FormBeanValidator',prefix='p_')</code> to the form-binding,
 * where <code>prefix</code> is an argument of the message name(<code>prefix+property</code>) for a property.
 * Because of the message name is shared between same {@link ValidationMessages}, that is same {@link Binder}, 
 * you have to provide a unique prefix for every FormBeanValidator in same binder.  
 * <p/>
 * <b>Example</b><p>
 * <pre>{@code
 * <grid width="600px" form="@id('fx') @load(vm.user) @save(vm.user,after='save') @validator('formBeanValidator',prefix='p_')">
 *   <textbox value="@bind(fx.firstName)"/>
 *   <label value="@load(vmsgs['p_firstName'])"/> 
 *</grid>
 * }</pre>
 *  
 * <br/>
 * 
 * @author dennis
 * @since 6.0.1
 */
public class FormBeanValidator extends AbstractValidator {

	
	
	protected Validator getValidator(){
		return BeanValidations.getValidator();
	}
	
	/**
	 * Validate the value 
	 * @param clz the class of bean
	 * @param property the property of bean
	 * @param value the value to be validated.
	 * @return the ConstraintViolation set.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Set<ConstraintViolation<?>> validate(Class clz, String property, Object value){
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
	
	/**
	 * Handle hibernate ConstraintViolation. By default, it adds to invalid messages.
	 * @param ctx
	 * @param key the key of message
	 * @param violations
	 * @since 6.0.1
	 */
	protected void handleConstraintViolation(ValidationContext ctx, String key, Set<ConstraintViolation<?>> violations){
		final int s = violations.size();
		
		if (s == 1) {
			addInvalidMessages(ctx, key, new String[] { violations.iterator().next().getMessage() });
		} else if (s > 0) {
			String[] msgs = new String[violations.size()];
			// it is a Set, I tested in hibernate 4 , it doesn't guarantee the
			// order, so I sort it by annottion.toString
			List<ConstraintViolation<?>> l = new ArrayList<ConstraintViolation<?>>(violations);
			sort(l);
			
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = l.get(i).getMessage();
			}
			addInvalidMessages(ctx, key, msgs);
		}
	}
	

	/**
	 * Get the bean class of the base object. <br/>
	 * @param ctx the validation context
	 * @param base the base object
	 * @return the bean class of base object, never null
	 */
	protected Class getBeanClass(ValidationContext ctx, Object base){
		return base.getClass();
	}
	
	
	@Override
	public void validate(ValidationContext ctx) {
		final Property p = ctx.getProperty();
		final Object base = p.getBase();

		//check if it is the form binding case.
		if(ctx.getBindContext().getBinding() instanceof SaveFormBinding){
			final String prefix = (String)ctx.getValidatorArg("prefix");
			if(Strings.isEmpty(prefix)){
				throw new NullPointerException("prefix of message key is empty, did you set prefix argument in @validator?");
			}
			
			final Class<?> clz = getBeanClass(ctx, base);
			final Map<String,Property> beanProps = ctx.getProperties(base);//get related properties of base object
			String pname = null;
			for(Property prop:beanProps.values()){
				pname = prop.getProperty();
				if(".".equals(pname)) continue;//skip the case of property for form
				Set<ConstraintViolation<?>> s = validate(clz, pname, prop.getValue());
				if(s.size()>0){
					handleConstraintViolation(ctx,new StringBuilder().append(prefix).append(pname).toString(),s);
				}
			}
		}else{
			throw new UiException("Can be used in form binding only");
		}
	}
}
