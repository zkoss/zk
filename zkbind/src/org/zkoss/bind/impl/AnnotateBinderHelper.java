/* AnnotateBinderHelper.java

	Purpose:
		
	Description:
		
	History:
		Sep 9, 2011 6:06:10 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.lang.Strings;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * Helper class to parse binding annotations and create bindings. 
 * @author henrichen
 * @author dennischen
 *
 */
public class AnnotateBinderHelper {
	final private Binder _binder;
	
	final static private String INIT_ANNO = "init";
	final static private String BIND_ANNO = "bind";
	final static private String LOAD_ANNO = "load";
	final static private String SAVE_ANNO = "save";
	final static private String ID_ANNO = "id";
	final static private String VALIDATOR_ANNO = "validator";
	final static private String CONVERTER_ANNO = "converter";
	final static private String COMMAND_ANNO = "command";
	
	final static private String FORM_ATTR = "form";
	final static private String VIEW_MODEL_ATTR = "viewModel";
	final static private String BINDER_ATTR = "binder";
	
	
	public AnnotateBinderHelper(Binder binder) {
		_binder = binder;
	}
	
	public void initComponentBindings(Component comp) {
		processAllComponentsBindings(comp);
	}
	
	
	private void processAllComponentsBindings(Component comp) {
		final Binder selfBinder = (Binder) comp.getAttribute(BinderImpl.BINDER);
		//check if a component was binded already(by any binder)
		if (selfBinder != null) //this component already binded ! skip all of its children
			return;
		
		processComponentBindings0(comp);
		for(final Iterator<Component> it = comp.getChildren().iterator(); it.hasNext();) {
			final Component kid = it.next();
			processAllComponentsBindings(kid); //recursive to each child
		}
	}
	
	private void processComponentBindings0(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		
		final List<String> props = compCtrl.getAnnotatedProperties();// look every property has annotation	
		for (final Iterator<?> it = props.iterator(); it.hasNext(); ) {
			final String propName = (String) it.next();
			if (isEventProperty(propName)) {
				processCommandBinding(comp,propName);
			}else if(FORM_ATTR.equals(propName)){
				processFormBindings(comp);
			}else if(VIEW_MODEL_ATTR.equals(propName)){
				//ignore
			}else if(BINDER_ATTR.equals(propName)){
				//ignore
			}else{
				processPropertyBindings(comp, propName);
			}
		}
	}
	
	private boolean isEventProperty(String propName) {
		return propName.startsWith("on") && propName.length() >= 3 && Character.isUpperCase(propName.charAt(2));
	}
	
	private void processCommandBinding(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Collection<Annotation> anncol = compCtrl.getAnnotations(propName, COMMAND_ANNO);
		if(anncol.size()==0) return;
		if(anncol.size()>1) {
			throw new IllegalSyntaxException("Allow only one command binding for event "+propName+" of "+compCtrl);
		}
		final Annotation ann = anncol.iterator().next();
		
		final Map<String,Object> attrs = ann.getAttributes(); //(tag, tagExpr)
		Map<String, Object> args = null;
		final List<String> cmdExprs = new ArrayList<String>();
		for (final Iterator<Entry<String,Object>> it = attrs.entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				cmdExprs.add(testString(comp,propName,tag,tagExpr));
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		
		args = args == null ? null : parsedArgs(args);
		for(String cmd : cmdExprs) {
			_binder.addCommandBinding(comp, propName, cmd, args);
		}
	}
	
	private void processPropertyBindings(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		
		//validator and converter information
		ValidatorInfo validatorInfo = parseValidator(compCtrl,propName);
		ConverterInfo converterInfo = parseConverter(compCtrl,propName);
		
		//scan init
		Collection<Annotation> initannos = compCtrl.getAnnotations(propName, INIT_ANNO);
		if(initannos.size()>1){
			throw new IllegalSyntaxException("Allow only one @init for "+propName+" of "+comp);
		}else if(initannos.size()==1){
			processPropertyInit(comp,propName,initannos.iterator().next(),converterInfo);
		}
		
		Collection<Annotation> annos = compCtrl.getAnnotations(propName); //init in the annotation with the sequence
		//
		boolean hasBind = false;
		boolean hasLS = false;
		for(Annotation anno:annos){
			if(anno.getName().equals(BIND_ANNO)){
				hasBind=true;
				processPropertyPromptBindings(comp,propName,anno,converterInfo,validatorInfo);
			}else if(anno.getName().equals(LOAD_ANNO)){
				hasLS=true;
				processPropertyLoadBindings(comp,propName,anno,converterInfo);
			}else if(anno.getName().equals(SAVE_ANNO)){
				hasLS=true;
				processPropertySaveBindings(comp,propName,anno,converterInfo,validatorInfo);
			}
		}
		if(hasBind && hasLS){
			throw new IllegalSyntaxException("@bind is for prompt binding, you should not use it with @load and @save, check "+propName+" of "+comp);
		}
	}
	
	private void processPropertyInit(Component comp, String propName, Annotation anno,ConverterInfo converterInfo) {
		String initExpr = null;
			
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = anno.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				initExpr = testString(comp, propName, tag, tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.setPropertyInitBinding(comp, propName, initExpr, args,converterInfo == null ? null : converterInfo.expr, 
				converterInfo == null ? null : converterInfo.args);
	}
	
	//process @bind(expr) 
	private void processPropertyPromptBindings(Component comp, String propName, Annotation ann, ConverterInfo converterInfo, ValidatorInfo validatorInfo) {
		String expr = null;
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				expr = testString(comp,propName,tag, tagExpr);
			} else if ("before".equals(tag)) {
				throw new IllegalSyntaxException("@bind is for prompt binding only, doesn't support before commands, check property "+propName+" of "+comp);
			} else if ("after".equals(tag)) {
				throw new IllegalSyntaxException("@bind is for prompt binding only, doesn't support after commands, check property "+propName+" of "+comp);
			}  else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
			
		args = args==null?null:parsedArgs(args);

		_binder.addPropertyLoadBindings(comp, propName,
				expr, null, null, args, 
				converterInfo == null ? null : converterInfo.expr, 
				converterInfo == null ? null : converterInfo.args);
		
		_binder.addPropertySaveBindings(comp, propName, expr,
				null, null, args, 
				converterInfo == null ? null : converterInfo.expr, 
				converterInfo == null ? null : converterInfo.args, 
				validatorInfo == null ? null : validatorInfo.expr, 
				validatorInfo == null ? null : validatorInfo.args);
	}
	
	private String testString(Object comp,String propName, String tag, Object tagValue){
		if(tagValue instanceof String){
			return (String)tagValue;
		}else{
			throw new IllegalSyntaxException("Allow only String in "+tag +":"+propName+":"+comp+", but get is "+tagValue);
		}
	}
	
	private void addCommand(Component comp, List<String> cmds, Object cmdExprs){
		if(cmdExprs instanceof String[]){
			for(String cmdExpr:(String[])cmdExprs){
				addCommand(comp,cmds,cmdExpr);
			}
		}else{
			addCommand(comp,cmds,cmdExprs.toString());
		}
		
	}
	private void addCommand(Component comp, List<String> cmds, String cmdExpr){
		cmds.add(eval(_binder.getEvaluatorX(),comp,cmdExpr,String.class));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T eval(BindEvaluatorX evalx, Component comp, String expression, Class<T> expectedType){
		ExpressionX expr = evalx.parseExpressionX(null, expression, expectedType);
		Object obj = evalx.getValue(null, comp, expr);
		return (T)obj;
	}
	
	private void processPropertyLoadBindings(Component comp, String propName, Annotation ann, ConverterInfo converterInfo) {
		String loadExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
		
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = testString(comp,propName,tag, tagExpr);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.addPropertyLoadBindings(comp, propName,
			loadExpr, 
			beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
			afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), args, 
			converterInfo == null ? null : converterInfo.expr, 
			converterInfo == null ? null : converterInfo.args);
	}

	private void processPropertySaveBindings(Component comp, String propName, Annotation ann, ConverterInfo converterInfo, ValidatorInfo validatorInfo) {
		String saveExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				saveExpr = testString(comp,propName,tag, tagExpr);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.addPropertySaveBindings(comp, propName,saveExpr, 
			beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
			afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), args, 
			converterInfo == null ? null : converterInfo.expr, 
			converterInfo == null ? null : converterInfo.args,
			validatorInfo == null ? null : validatorInfo.expr, 
			validatorInfo == null ? null : validatorInfo.args);
	}
	
	private void processFormBindings(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final BindEvaluatorX eval = _binder.getEvaluatorX();
		//validator information
		ValidatorInfo validatorInfo = parseValidator(compCtrl,FORM_ATTR);
		
		String formId = null;
		
		Collection<Annotation> idannos = compCtrl.getAnnotations(FORM_ATTR, ID_ANNO);
		if(idannos.size()==0){
			throw new IllegalSyntaxException("@id is not found for a form binding of "+compCtrl);
		}else if(idannos.size()>1){
			throw new IllegalSyntaxException("Allow only one @id for a form binding of "+compCtrl);
		}
		
		final Annotation idanno = idannos.iterator().next();
		final String idExpr = idanno.getAttribute("value");
		
		if(idExpr!=null){
			formId = eval(eval, comp, idExpr, String.class);
		}
		if(formId==null){
			throw new UiException("value of @id is not found for a form binding of "+compCtrl+", exprssion is "+idExpr);
		}
		
		//scan init first
		Collection<Annotation> initannos = compCtrl.getAnnotations(FORM_ATTR, INIT_ANNO);
		if(initannos.size()>1){
			throw new IllegalSyntaxException("Allow only one @init for "+FORM_ATTR+" of "+comp);
		}else if(initannos.size()==1){
			processFormInit(comp,formId,initannos.iterator().next());
		}
		
		Collection<Annotation> annos = compCtrl.getAnnotations(FORM_ATTR); //get all annotation in the form with the order.

		for(Annotation anno:annos){
			if(anno.getName().equals(LOAD_ANNO)){
				processFormLoadBindings(comp,formId,anno);
			}else if(anno.getName().equals(SAVE_ANNO)){
				processFormSaveBindings(comp,formId,anno,validatorInfo);
			}
		}
	}
	
	private void processFormInit(Component comp, String formId,Annotation ann) {
		String initExpr = null;
			
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				initExpr = testString(comp, formId, tag, tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.setFormInitBinding(comp, formId,initExpr, args);
	}
	
	private void processFormLoadBindings(Component comp, String formId,Annotation ann) {
		String loadExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = testString(comp,formId,tag, tagExpr);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds, tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds, tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.addFormLoadBindings(comp, formId,
			loadExpr, 
			beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
			afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), args);
	}
	
	private void processFormSaveBindings(Component comp, String formId, Annotation ann, ValidatorInfo validatorInfo) {
		String saveExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, Object> args = null;
		for (final Iterator<Entry<String,Object>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				saveExpr = testString(comp,formId,tag, tagExpr);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, Object>();
				}
				args.put(tag, tagExpr);
			}
		}
		args = args==null?null:parsedArgs(args);
		_binder.addFormSaveBindings(comp, formId, saveExpr, 
			beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
			afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), args, 
			validatorInfo == null ? null : validatorInfo.expr, 
			validatorInfo == null ? null : validatorInfo.args);
	}
	

	private ConverterInfo parseConverter(ComponentCtrl compCtrl, String propName) {
		final Collection<Annotation> annos = compCtrl.getAnnotations(propName, CONVERTER_ANNO);
		if(annos.size()==0) return null;
		if(annos.size()>1) {
			throw new IllegalSyntaxException("Allow only one converter for "+propName+" of "+compCtrl);
		}
		final Annotation anno = annos.iterator().next();
		
		ConverterInfo info = new ConverterInfo();
		for (final Iterator<Entry<String,Object>> it = anno.getAttributes().entrySet().iterator(); it
				.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = testString(compCtrl,propName,tag, tagExpr);
			} else { // other unknown tag, keep as arguments
				if (info.args == null) {
					info.args = new HashMap<String, Object>();
				}
				info.args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new IllegalSyntaxException("Must specify a converter for "+propName+" of "+compCtrl);
		}
		info.args = info.args == null ? null : parsedArgs(info.args);
		return info;
	}

	private ValidatorInfo parseValidator(ComponentCtrl compCtrl, String propName) {
		final Collection<Annotation> annos = compCtrl.getAnnotations(propName, VALIDATOR_ANNO);
		if(annos.size()==0) return null;
		if(annos.size()>1) {
			throw new IllegalSyntaxException("Allow only one validator for "+propName+" of "+compCtrl);
		}
		final Annotation anno = annos.iterator().next();
		ValidatorInfo info = new ValidatorInfo();
		for (final Iterator<Entry<String,Object>> it = anno.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,Object> entry = it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = testString(compCtrl,propName,tag, tagExpr);
			} else { // other unknown tag, keep as arguments
				if (info.args == null) {
					info.args = new HashMap<String, Object>();
				}
				info.args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new IllegalSyntaxException("Must specify a validator for "+propName+" of "+compCtrl);
		}
		info.args = info.args == null ? null : parsedArgs(info.args);
		return info;
	}
	
	// parse args , if it is a string, than parse it to ExpressionX
	private Map<String, Object> parsedArgs(Map<String,Object> args) {
		final BindEvaluatorX eval = _binder.getEvaluatorX();
		final Map<String, Object> result = new LinkedHashMap<String, Object>(args.size()); 
		for(final Iterator<Entry<String, Object>> it = args.entrySet().iterator(); it.hasNext();) {
			final Entry<String, Object> entry = it.next(); 
			final String key = entry.getKey();
			final Object value = entry.getValue();
			if (value instanceof String){
				addArg(eval, result, key, (String)value);
			}/*else if (value instanceof String[]) { 
				// TODO, consider if it is a string array/collection
			}*/else{
				result.put(key, value);
			}
		}
		return result;
	}

	
	private void addArg(BindEvaluatorX eval, Map<String,Object> result, String key, String valueScript) {
		final ExpressionX parsedValue = valueScript == null ? null : eval.parseExpressionX(null, valueScript, Object.class);
		result.put(key, parsedValue);
	}
	
	private static class ValidatorInfo{
		Map<String, Object> args;
		String expr;
	}
	
	private static class ConverterInfo{
		Map<String, Object> args;
		String expr;
	}
}
