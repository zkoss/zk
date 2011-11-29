/* SavePropertyBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 1, 2011 2:45:43 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link SavePropertyBinding}.
 * @author henrichen
 */
public class SavePropertyBindingImpl extends PropertyBindingImpl implements SavePropertyBinding {
	private final ExpressionX _validator;
	private final Map<String, Object> _validatorArgs;
	
	public SavePropertyBindingImpl(Binder binder, Component comp, String attr, String saveExpr,
			ConditionType conditionType, String command, Map<String, Object> bindingArgs, 
			String converterExpr, Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {
		
		super(binder, comp, "self."+attr, saveExpr, conditionType, command, bindingArgs, converterExpr, converterArgs);
		final BindEvaluatorX eval = binder.getEvaluatorX();
		_validator = validatorExpr==null?null:parseValidator(eval,validatorExpr);
		_validatorArgs = validatorArgs;
	}
	
	public Map<String, Object> getValidatorArgs() {
		return _validatorArgs;
	}
	
	@Override
	protected boolean ignoreTracker(){
		return true;
	}
	
	private ExpressionX parseValidator(BindEvaluatorX eval, String validatorExpr) {
		
//		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
//		ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el, we don't need to trace validator
		//expression will/should not be tracked, (although, from the impl, tracker don't care savebinding)
		return eval.parseExpressionX(/*ctx*/null, validatorExpr, Object.class);
	}

	public Validator getValidator() {
		if(_validator==null) return null;

//		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
//		ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el, we don't need to trace validator
		final BindEvaluatorX eval = getBinder().getEvaluatorX();

		Object obj = eval.getValue(null, getComponent(), _validator);
		
		if(obj instanceof Validator){
			return (Validator)obj;
		}else if(obj instanceof String){
			return getBinder().getValidator((String)obj);//binder will throw exception if not found
		}else{
			throw new ClassCastException("result of expression '"+_validator.getExpressionString()+"' is not a Validator, is "+obj);
		}
	}
	

	private static final String $COMPVALUE$ = "$COMPVALUE$";
	private static final String $VALUEREF$ = "$VALUEREF$";
	private Object getComponentValue(BindContext ctx) {
		if (!containsAttribute(ctx, $COMPVALUE$)) {
			final Component comp = ctx.getComponent();
			final BindEvaluatorX eval = getBinder().getEvaluatorX();
			
			//get data from component attribute
			Object value = eval.getValue(null, comp, _fieldExpr);
			
			//use converter to convert type if any
			final Converter conv = getConverter();
			if (conv != null) {
				value = conv.coerceToBean(value, comp, ctx);
				ValueReference ref = getValueReference(ctx);
					//collect Property for @NotifyChange, kept in BindContext
					//see BinderImpl$CommandEventListener#onEvent()
				BindELContext.addNotifys(getConverterMethod(conv.getClass()), ref.getBase(), null, value, ctx);
			}
			setAttribute(ctx, $COMPVALUE$, value);
		}
		return getAttribute(ctx, $COMPVALUE$);
	}
	
	public void save(BindContext ctx) {
		//get data from component attribute
		Object value = getComponentValue(ctx);
		
		//set data into bean property
		final Component comp = ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		eval.setValue(ctx, comp, _accessInfo.getProperty(), value);
	}
	
	//get and cache value reference of this binding
	private ValueReference getValueReference(BindContext ctx){
		ValueReference ref = (ValueReference) getAttribute(ctx, $VALUEREF$);
		if (ref == null) {
			final Component comp = ctx.getComponent();
			final BindEvaluatorX eval = getBinder().getEvaluatorX();
			ref = eval.getValueReference(ctx, comp, _accessInfo.getProperty());
			setAttribute(ctx, $VALUEREF$, ref);
		}
		return ref;
	}

	//--SaveBinding--//
	public Property getValidate(BindContext ctx) {
		//we should not check this binding need to validate or not, 
		//maybe other validator want to know the value of this binding, so just provide it
		final Object value = getComponentValue(ctx);
		try {
			ValueReference ref = getValueReference(ctx);
			return new PropertyImpl(ref.getBase(), (String) ref.getProperty(), value);
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}
	
	public boolean hasValidator() {
		return _validator == null ? false : true;
	}
	
	public void validate(ValidationContext vctx) {
		Validator validator = getValidator();
		if(validator == null){
			throw new NullPointerException("cannot find validator for "+this);
		}
		validator.validate(vctx);
		//collect notify change
		collectNotifyChange(validator,vctx);
	}
	
	private void collectNotifyChange(Validator validator, ValidationContext vctx) {
		ValueReference ref = getValueReference(vctx.getBindContext());
		BindELContext.addNotifys(getValidatorMethod(validator.getClass()), ref.getBase(), null, null, vctx.getBindContext());
	}

	private Method getConverterMethod(Class<? extends Converter> cls) {
		try {
			return cls.getMethod("coerceToBean", new Class[] {Object.class, Component.class, BindContext.class});
		} catch (NoSuchMethodException e) {
			//ignore
		}
		return null; //shall never come here
	}
	
	private Method getValidatorMethod(Class<? extends Validator> cls) {
		try {
			return cls.getMethod("validate", new Class[] {ValidationContext.class});
		} catch (NoSuchMethodException e) {
			//ignore
		}
		return null; //shall never come here
	}
}
