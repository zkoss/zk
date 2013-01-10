/* ValidationHelper.java

	Purpose:
		
	Description:
		
	History:
		2011/10/1 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
/**
 * a internal stateless helper to helps BindImpl to the validation.
 * 
 * @author dennis
 * @since 6.0.0
 */
/*public*/ class ValidationHelper {
	
	private final Binder _binder;
	private final InfoProvider _infoProvider;
	
//	private Map<Binding,Set<Property>> _collectedPropertyCache;
	private Map<Binding,Property> _mainPropertyCache;
	
	public ValidationHelper(Binder binder,InfoProvider infoProvider){
		this._binder = binder;
		this._infoProvider = infoProvider;
		this._mainPropertyCache = new HashMap<Binding,Property>(2);
//		this._collectedPropertyCache = new HashMap<Binding,Set<Property>>(2);
	}
	
	// a binder validation information provider, it is related to implementation of BindImpl 
	interface InfoProvider {
		Map<String, List<SavePropertyBinding>> getSaveBeforeBindings();
		Map<String, List<SavePropertyBinding>> getSaveAfterBindings();
		Map<String, List<SaveFormBinding>> getSaveFormBeforeBindings();
		Map<String, List<SaveFormBinding>> getSaveFormAfterBindings();
		BindingKey getBindingKey(Component comp, String attr);
	}
	
	//doCommand -> doValidate ->
	public void collectSaveBefore(Component comp, String command, Event evt, Set<Property> validates){
		collectSavePropertyBefore(comp, command, evt, validates);
		collectSaveFormBefore(comp, command, evt, validates);
	}
	
	//doCommand -> doValidate -> collectSaveBefore ->
	private void collectSavePropertyBefore(Component comp, String command, Event evt, Set<Property> validates) {
		final List<SavePropertyBinding> bindings = _infoProvider.getSaveBeforeBindings().get(command);//_saveBeforeBindings.get(command);
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				collectSavePropertyBinding(comp, binding, command, evt, validates);
			}
		}
	}
	
	//doCommand -> doValidate -> collectSaveBefore ->
	private void collectSaveFormBefore(Component comp, String command, Event evt, Set<Property> validates) {
		final List<SaveFormBinding> bindings = _infoProvider.getSaveFormBeforeBindings().get(command);//_saveFormBeforeBindings.get(command);
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				collectSaveFormBinding(comp, binding, command, evt, validates);
			}
		}
	}
	
	
	//doValidate -> 
	public void collectSaveAfter(Component comp, String command, Event evt, Set<Property> validates) {
		collectSavePropertyAfter(comp, command, evt, validates);
		collectSaveFormAfter(comp, command, evt, validates);
	}
	
	
	//doValidate -> collectSaveAfter ->
	private void collectSavePropertyAfter(Component comp, String command, Event evt, Set<Property> validates) {
		final List<SavePropertyBinding> bindings = _infoProvider.getSaveAfterBindings().get(command);//_saveAfterBindings.get(command);
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				collectSavePropertyBinding(comp, binding, command, evt, validates);
			}
		}
	}
	
	//doValidate -> collectSaveAfter ->
	private void collectSaveFormAfter(Component comp, String command, Event evt, Set<Property> validates) {
		final List<SaveFormBinding> bindings = _infoProvider.getSaveFormAfterBindings().get(command);//_saveFormAfterBindings.get(command);
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				collectSaveFormBinding(comp, binding, command, evt, validates);
			}
		}
	}	

	//validations

	public boolean validateSaveBefore(Component comp,String command, Map<String,Property[]> validates,boolean valid,Set<Property> notifys) {
		boolean r = valid;
		r &= validateSavePropertyBefore(comp, command, validates,r,notifys);
		r &= validateSaveFormBefore(comp, command, validates,r,notifys);
		return r;
	}
	
	//doCommand -> doValidate -> validateSaveBefore ->
	private boolean validateSavePropertyBefore(Component comp,String command, Map<String,Property[]> validates,boolean valid, Set<Property> notifys) {
		final List<SavePropertyBinding> bindings = _infoProvider.getSaveBeforeBindings().get(command);//_saveBeforeBindings.get(command);
		boolean r = valid;
		if (bindings != null && bindings.size()>0) {
			for (SavePropertyBinding binding : bindings) {
				r &= validateSavePropertyBinding(comp, binding, command, validates, r, notifys);
			}
		}
		return r;
	}
	
	//doCommand -> doValidate -> validateSaveBefore ->
	private boolean validateSaveFormBefore(Component comp,String command, Map<String,Property[]> validates,boolean valid,Set<Property> notifys) {
		final List<SaveFormBinding> bindings = _infoProvider.getSaveFormBeforeBindings().get(command);//_saveFormBeforeBindings.get(command);
		boolean r = valid;
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				r &= validateSaveFormBinding(comp, binding, command, validates, r, notifys);
			}
		}
		return r;
	}	
	
	public boolean validateSaveAfter(Component comp,String command, Map<String,Property[]> validates, boolean valid, Set<Property> notifys) {
		boolean r = valid;
		r &= validateSavePropertyAfter(comp, command, validates,r,notifys);
		r &= validateSaveFormAfter(comp, command, validates,r,notifys);
		return r;
	}
	
	//doCommand -> doValidate -> validateSaveBefore ->
	private boolean validateSavePropertyAfter(Component comp,String command, Map<String,Property[]> validates, boolean valid, Set<Property> notifys) {
		final List<SavePropertyBinding> bindings = _infoProvider.getSaveAfterBindings().get(command);//_saveBeforeBindings.get(command);
		boolean r = true;
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				r &= validateSavePropertyBinding(comp, binding, command, validates, valid, notifys);
			}
		}
		return r;
	}
	
	//doCommand -> doValidate -> validateSaveBefore ->
	private boolean validateSaveFormAfter(Component comp,String command, Map<String,Property[]> validates, boolean valid, Set<Property> notifys) {
		final List<SaveFormBinding> bindings = _infoProvider.getSaveFormAfterBindings().get(command);//_saveFormBeforeBindings.get(command);
		boolean r = valid;
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				r &= validateSaveFormBinding(comp, binding, command, validates, r, notifys);
			}
		}
		return r;
	}	
	
	//collect properties from a save-binding
	private void collectSavePropertyBinding(Component comp, SavePropertyBinding binding, String command, Event evt, Set<Property> validates) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), evt);
		BindContextUtil.setConverterArgs(_binder, binding.getComponent(), ctx, binding);
		Set<Property> cp = new HashSet<Property>();
		Property p = binding.getValidate(ctx);
		_mainPropertyCache.put(binding, p);
		cp.add(p);//main property
		validates.add(p); //collect properties to be validated
	}
	
	//collect properties form a save-form-binding
	private void collectSaveFormBinding(Component comp, SaveFormBinding binding, String command, Event evt, Set<Property> validates) {
		Set<SaveBinding> savebindings = ((BinderCtrl)binding.getBinder()).getFormAssociatedSaveBindings(binding.getComponent());
		for(SaveBinding sbinding:savebindings){
			if(sbinding instanceof SavePropertyBinding){
				collectSavePropertyBinding(comp,((SavePropertyBinding)sbinding),command,evt,validates);
			}else{
				// any other possible to go here?
			}
		}
		
		
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), evt);
		
		Set<Property> cp = new HashSet<Property>();
		Property p = binding.getValidate(ctx);
		_mainPropertyCache.put(binding, p);
		
		cp.add(p);// the main property
		cp.addAll(binding.getValidates(ctx));// the field properties in form
		validates.addAll(cp); //collect properties to be validated
	}
	
	
	//validate a save-binding
	private boolean validateSavePropertyBinding(Component comp,SavePropertyBinding binding,String command, Map<String,Property[]> validates, boolean valid, Set<Property> notifys) {
		if(!binding.hasValidator()) return true;
		
		//clear previous message before balidation
		if(((BinderImpl)binding.getBinder()).hasValidator(binding.getComponent(), binding.getFieldName())){
			clearValidationMessage(binding.getBinder(),binding.getComponent(),binding.getFieldName());
		}
		
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), null);
		BindContextUtil.setValidatorArgs(binding.getBinder(), binding.getComponent(), ctx, binding);

		Property p = _mainPropertyCache.get(binding);
		ValidationContextImpl vContext = new ValidationContextImpl(command,p,validates,ctx,valid);
		binding.validate(vContext);
		final Set<Property> xnotifys = getNotifys(ctx);
		if (xnotifys != null) {
			notifys.addAll(xnotifys);
		}
		return vContext.isValid();
	}
	
	private void clearValidationMessage(Binder binder, Component component,String attr){
		ValidationMessages vmsgs = ((BinderCtrl)binder).getValidationMessages();
		if(vmsgs!=null){
			vmsgs.clearMessages(component,attr);
		}
	}
	
	//validate a save-form-binding
	private boolean validateSaveFormBinding(Component comp, SaveFormBinding binding, String command, Map<String,Property[]> validates, boolean valid, Set<Property> notifys) {
		//validate tracked savebinding
		Set<SaveBinding> savebindings = ((BinderCtrl)binding.getBinder()).getFormAssociatedSaveBindings(binding.getComponent());
		boolean svalid = true;
		for(SaveBinding sbinding:savebindings){
			if(sbinding instanceof SavePropertyBinding){
				svalid &= validateSavePropertyBinding(comp,((SavePropertyBinding)sbinding),command,validates,svalid & valid,notifys);
			}else{
				// any other possible to go here?
			}
		}
		if(!binding.hasValidator()) return svalid;
		
		//clear previous message before validation
		if(((BinderImpl)binding.getBinder()).hasValidator(binding.getComponent(), binding.getFormId())){
			clearValidationMessage(binding.getBinder(),binding.getComponent(),binding.getFormId());
		}
		
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), null);
		BindContextUtil.setValidatorArgs(binding.getBinder(), binding.getComponent(), ctx, binding);

		Property p = _mainPropertyCache.get(binding); 
		ValidationContextImpl vContext = new ValidationContextImpl(command,p,validates,ctx,svalid & valid);
		binding.validate(vContext);
		final Set<Property> xnotifys = getNotifys(ctx);
		if (xnotifys != null) {
			notifys.addAll(xnotifys);
		}
		return svalid && vContext.isValid();
		
	}
	
	@SuppressWarnings("unchecked")
	private static Set<Property> getNotifys(BindContext ctx){
		return (Set<Property>)ctx.getAttribute(BinderImpl.NOTIFYS);
	}
	
}
