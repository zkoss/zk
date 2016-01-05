/* LoadFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 6:25:44 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.LoadInfo;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ValueReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link LoadFormBinding}
 * @author henrichen
 * @author jumperchen
 * @since 6.0.0
 */
public class LoadFormBindingImpl extends FormBindingImpl implements	LoadFormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private int _len;
	private Set<String> _doneDependsOn;
	
	public LoadFormBindingImpl(Binder binder, Component comp, String formId, String loadExpr, 
			ConditionType conditionType,String command, Map<String, Object> bindingArgs) {
		super(binder, comp, formId, loadExpr, conditionType, command, bindingArgs);
	}

	public void load(BindContext ctx) {
		final Binder binder = getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Component comp = getComponent();
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		
		final Object bean = eval.getValue(ctx, comp, _accessInfo.getProperty());
		//ZK-1016 Nested form binding doesn't work.
		final ValueReference valref = eval.getValueReference(ctx, comp,  _accessInfo.getProperty());
		//value-reference is null if it is a simple node, ex ${vm}
		if ((valref != null && valref.getBase() instanceof Form)
				|| bean instanceof Form) {
			throw new UiException(MiscUtil.formatLocationMessage("doesn't support to load a nested form , formId "+getFormId(),comp));
		}
		
		final Form form = initFormBean(bean, (Class<Object>) (bean != null ? bean.getClass() : eval.getType(ctx, comp, _accessInfo.getProperty())), ctx);
		final boolean activating = ((BinderCtrl)getBinder()).isActivating();

			//ZK-1005 ZK 6.0.1 validation fails on nested bean
			//sets the last loaded bean express of the form
			comp.setAttribute(BinderCtrl.LOAD_FORM_EXPRESSION, getPropertyString());
			
			if (activating)
				return;// don't notify change if activating

			// don't do resetDirty when in activating. Test case is in bind/form/FormWith*
			FormStatus formStatus = form.getFormStatus();
			formStatus.reset(); //initial loading, mark form as clean
						
			binder.notifyChange(form, "."); // notify change of fx and fx.*

				// notify change of fxStatus and fxStatus.*
			binder.notifyChange(formStatus, ".");

			if (collector != null) {
				collector.addInfo(new LoadInfo(LoadInfo.FORM_LOAD, comp,
						getConditionString(ctx), getPropertyString(),
						getFormId(), bean, getArgs(), null));
			}

	}
	
	private String getConditionString(BindContext ctx){
		StringBuilder condition = new StringBuilder();
		if(getConditionType()==ConditionType.BEFORE_COMMAND){
			condition.append("before = '").append(getCommandName()).append("'");
		}else if(getConditionType()==ConditionType.AFTER_COMMAND){
			condition.append("after = '").append(getCommandName()).append("'");
		}else{
			condition.append(ctx.getTriggerEvent()==null?"":"event = "+ctx.getTriggerEvent().getName()); 
		}
		return condition.length()==0?null:condition.toString();
	}
	
	public void setSeriesLength(int len) {
		_len = len;
	}
	
	public int getSeriesLength() {
		return _len;
	}

	/**
	 * Internal Use Only.
	 */
	public void addDependsOnTrackings(List<String> srcpath, String basepath, String[] props) {
		if (srcpath != null) {
			final String src = BindELContext.pathToString(srcpath);
			if (_doneDependsOn != null && _doneDependsOn.contains(src)) { //this method has already done @DependsOn in this binding
				return;
			}
			_doneDependsOn = AllocUtil.inst.addSet(_doneDependsOn, src); //mark method as done @DependsOn; ZK-2289
		}
		for(String prop : props) {
			BindELContext.addDependsOnTracking(this, srcpath, basepath, prop);
		}
	}
}
