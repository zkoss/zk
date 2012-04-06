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

import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.zk.ui.Component;

/**
 * A <a href="http://jcp.org/en/jsr/detail?id=303"/>JSR 303</a> compatible validator for a property-binding.<p/>  
 * Notice : Before use this validator, you have to configure your environment (depends on the implementation you chosen). 
 * Here is a article <a href="http://books.zkoss.org/wiki/Small_Talks/2011/May/Integrate_ZK_with_JSR_303:_Bean_Validation#How_to_use_Bean_Validation_in_your_ZK_application">Integrate ZK with JSR 303: Bean Validation</a> 
 * talks about how to set up JSR 303 in ZK with Hibernate implementation.
 * <p/> 
 * It validates a single propertie of a bean and sets the invalid message by 
 * {@link AbstractValidator#addInvalidMessage(ValidationContext, String)}. <p/>
 * 
 * To use this class, you have to add <code>@validator('beanValidator')</code> or <code>@validator('org.zkoss.bind.validator.BeanValidator')</code> to the property-binding
 * <p/> 
 * <b>Example</b><p/>
 * <pre>{@code
 * <grid width="600px">
 *   <textbox id="tb" value="@bind(vm.person.firstName) @validator('beanValidator')"/>
 *   <label value="@load(vmsgs[tb])"/> 
 *</grid>
 * }</pre>
 * 
 * [Since zk 6.0.1] <br/>
 * It also supports to validate a property of a form which properties are load from a bean,
 * It uses the class of last loaded bean of the form to perform the validation, which means it doesn't support to validate a form that didn't loaded a bean yet.
 * <p/>
 * <b>Example</b><p/>
 * <pre>{@code
 * <grid width="600px" form="@id('fx') @load(vm.user) @save(vm.user,after='save')">
 *   <textbox id="tb" value="@bind(fx.firstName) @validator('beanValidator')"/>
 *   <label value="@load(vmsgs[tb])"/> 
 *</grid>
 * }</pre>
 *  
 * <p/>
 * 
 * @author dennis
 * @since 6.0.0
 */
public class BeanValidator extends AbstractValidator {

	
	
	protected Validator getValidator(){
		return BeanValidations.getValidator();
	}
	
	/**
	 * Validate the value 
	 * @param clz the class of bean
	 * @param property the property of bean
	 * @param value the value to be validated.
	 * @return the ConstraintViolation set.
	 *  @since 6.0.1
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
	 * Get the bean class of the base object and property to validate. <br/>
	 * By default, if the base object is a form(implements FormExt), it returns the last loaded bean class of this form.<br/>
	 * If the object is not a form, it returns the class of base object directly.
	 * @param ctx the validation context
	 * @param base the base object
	 * @param property the property to validate
	 * @return a object array, the first item is the bean class of base object, 2nd item is the tuned property regards to the bean class
	 * @since 6.0.1
	 */
	
	protected Object[] getValidationInfo(ValidationContext ctx, Object base, String property){
		Class<?> clz = null;
		if(base instanceof Form){
			//ZK-1005 ZK 6.0.1 validation fails on nested bean
			Component formComp = (Component)ctx.getBindContext().getAttribute(BinderImpl.LOAD_FORM_COMPONENT);//see SavePropertyBinding
			if(formComp==null){
				throw new NullPointerException("form component not found");
			}
			String loadexpr = (String) formComp.getAttribute(BinderImpl.LOAD_FORM_EXPRESSION);//see InitForm and LoadForm
			if(loadexpr==null){
				throw new NullPointerException("load expression not found on the Form "+base+", a bean validator doesn't support to validate a form that doesn't loads a bean yet");
			}
			
			final String beanexpr = BindELContext.appendFields(loadexpr, property);
			Binder binder = ctx.getBindContext().getBinder();
			ExpressionX expr = binder.getEvaluatorX().parseExpressionX(null, beanexpr, Object.class);
			
			ValueReference vr = binder.getEvaluatorX().getValueReference(ctx.getBindContext(), formComp, expr);
			
			if(vr==null){
				throw new NullPointerException("loaded instance not found on the Form "+base+", a bean validator doesn't support to validate a form that doesn't loads a bean yet");
			}
			
			clz = vr.getBase().getClass();
			property = vr.getProperty().toString();

		}else{
			clz = base.getClass();
		}
		
		return new Object[]{clz,property};
	}
	
	/**
	 * Handle hibernate ConstraintViolation. by default, it add to invalid messages.
	 * @param ctx
	 * @param violations
	 * @since 6.0.1
	 */
	protected void handleConstraintViolation(ValidationContext ctx, Set<ConstraintViolation<?>> violations){
		final int s = violations.size();
		
		if (s == 1) {
			addInvalidMessage(ctx, violations.iterator().next().getMessage());
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
	
	@Override
	public void validate(ValidationContext ctx) {
		final Property p = ctx.getProperty();
		final Object base = p.getBase();
		String property = p.getProperty();
		final Object value = p.getValue();
		
		Object[] info = getValidationInfo(ctx, base, property);
		Class<?> clz = (Class<?>)info[0];
		property = (String)info[1];
		 
		Set<ConstraintViolation<?>> violations = validate(clz, property, value);
		
		handleConstraintViolation(ctx, violations);
	}
}
