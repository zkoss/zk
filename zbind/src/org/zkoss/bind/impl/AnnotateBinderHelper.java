/* AnnotateBinderHelper.java

	Purpose:
		
	Description:
		
	History:
		Sep 9, 2011 6:06:10 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.lang.Strings;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * Helper class to parse binding annotations and create bindings. 
 * @author henrichen
 *
 */
public class AnnotateBinderHelper {
	final private Binder _binder;
	
	final static private String BIND_ANNO = "bind";
	final static private String FORM_ANNO = "form";
	final static private String VALIDATOR_ANNO = "validator";
	final static private String CONVERTER_ANNO = "converter";
	
	public AnnotateBinderHelper(Binder binder) {
		_binder = binder;
	}
	
	public void initComponentBindings(Component comp) {
		initAllComponentsBindings(comp);
	}
	
	
	private void initAllComponentsBindings(Component comp) {
		final Binder selfBinder = (Binder) comp.getAttribute(BinderImpl.BINDER);
		//check if a component was binded already(by any binder)
		if (selfBinder != null) //already binded !
			return;
		initFormBindings(comp);//initial form binding on comp, ex self="form(...)"
		initComponentPropertiesBindings(comp); //initial property binding on comp, ex value="bind(...)"
		for(final Iterator<Component> it = comp.getChildren().iterator(); it.hasNext();) {
			final Component kid = (Component) it.next();
			initAllComponentsBindings(kid); //recursive to each child
		}
	}
	
	private void initFormBindings(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation formAnno = compCtrl.getAnnotation(FORM_ANNO);
		if (formAnno != null) {
			final List<String> saveExprs = new ArrayList<String>();
			final List<String> loadExprs = new ArrayList<String>();
			String id = null;
			Object value = null;
			String initExpr = null;
			Map<String, Object> args = null;
			ValidatorInfo validatorInfo = null;
			for (final Iterator<?> it = formAnno.getAttributes().entrySet().iterator(); it.hasNext();) {
				final Map.Entry entry = (Map.Entry) it.next();
				final String tag = (String) entry.getKey();
				final Object tagExpr = entry.getValue();
				if ("id".equals(tag)) {
					id = (String) tagExpr;
				} else if ("init".equals(tag)) {
					initExpr = (String) tagExpr;
				} else if ("save".equals(tag)) {
					addTagExpr(saveExprs, tagExpr);
				} else if ("load".equals(tag)) {
					addTagExpr(loadExprs, tagExpr);
				} else if ("value".equals(tag)) {
					value = tagExpr;
				} else { //other unknown tag, keep as arguments
					if (args == null) {
						args = new HashMap<String, Object>();
					}
					args.put(tag, tagExpr);
				}
			}
			if (Strings.isBlank(id)) {
				throw new UiException("Must specify a form id!");
			}
			if (value != null) {
				//default value will apply to load or save if they were empty
				if (loadExprs.isEmpty()) { //might be both
//					loadExprs.add(value);
					addTagExpr(loadExprs, value);
				}
				if (saveExprs.isEmpty()) { //might be both
//					saveExprs.add(value);
					addTagExpr(saveExprs, value);
				}
			}
			args = args == null ? null : parsedArgs(args);
			
			//parse validator
			final Annotation validtorAnno = compCtrl.getAnnotation(VALIDATOR_ANNO);
			if(validtorAnno!=null){
				validatorInfo = parseValidator(compCtrl,validtorAnno);
			}
			
			_binder.addFormBindings(comp, id, initExpr,
					loadExprs.toArray(new String[loadExprs.size()]), 
					saveExprs.toArray(new String[saveExprs.size()]),  
					validatorInfo==null?null:validatorInfo.expr, 
					args,
					validatorInfo==null?null:validatorInfo.args);
		}
	}
	
	private void initComponentPropertiesBindings(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final List<?> props = compCtrl.getAnnotatedPropertiesBy(BIND_ANNO);
		for (final Iterator<?> it = props.iterator(); it.hasNext(); ) {
			final String propName = (String) it.next();
			if (isEventProperty(propName)) {
				initCommandBindings(comp, propName);
			} else {
				initPropertyBindings(comp, propName);
			}
			
		}
	}
	
	private boolean isEventProperty(String propName) {
		return propName.startsWith("on") && propName.length() >= 3 && Character.isUpperCase(propName.charAt(2));
	}
	
	private void initCommandBindings(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(propName, BIND_ANNO);
		if (ann != null) {
			final Map<?, ?> attrs = ann.getAttributes(); //(tag, tagExpr)
			Map<String, Object> args = null;
			final List<String> cmdExprs = new ArrayList<String>();
			for (final Iterator<?> it = attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry entry = (Map.Entry) it.next();
				final String tag = (String) entry.getKey();
				final Object tagExpr = entry.getValue();
				if ("value".equals(tag)) {
					if (tagExpr instanceof String[]) {
						throw new UiException("Allow only one Command for an event!");
					}
					cmdExprs.add((String)tagExpr);
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
	}
	
	private void initPropertyBindings(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Annotation ann = compCtrl.getAnnotation(propName, BIND_ANNO);
		if (ann != null) {
			final List<String> saveExprs = new ArrayList<String>();
			final List<String> loadExprs = new ArrayList<String>();
			Object value = null;
			String initExpr = null;
			Map<String, Object> args = null;
			ValidatorInfo validatorInfo = null;
			ConverterInfo converterInfo = null;
			for (final Iterator<?> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
				final Map.Entry entry = (Map.Entry) it.next();
				final String tag = (String) entry.getKey();
				final Object tagExpr = entry.getValue();
				if ("init".equals(tag)) {
					initExpr = (String) tagExpr;
				} else if ("save".equals(tag)) {
					addTagExpr(saveExprs, tagExpr);
				} else if ("load".equals(tag)) {
					addTagExpr(loadExprs, tagExpr);
				} else if ("value".equals(tag)) {
					value = tagExpr;
				} else { //other unknown tag, keep as arguments
					if (args == null) {
						args = new HashMap<String, Object>();
					}
					args.put(tag, tagExpr);
				}
			}
			if (value != null) {
				//default value will apply to load or save if they were empty
				if (loadExprs.isEmpty()) { //might be both
//					loadExprs.add(value);
					addTagExpr(loadExprs, value);
				}
				if (saveExprs.isEmpty()) { //might be both
//					saveExprs.add(value);
					addTagExpr(saveExprs, value);
				}
			}
			
			args = args==null?null:parsedArgs(args);
			
			//parse validator
			final Annotation validtorAnno = compCtrl.getAnnotation(propName, VALIDATOR_ANNO);
			if(validtorAnno!=null){
				validatorInfo = parseValidator(compCtrl,validtorAnno);
			}
			//parse converter
			final Annotation converterAnno = compCtrl.getAnnotation(propName, CONVERTER_ANNO);
			if(converterAnno!=null){
				converterInfo = parseConverter(compCtrl,converterAnno);
			}
			
			
			_binder.addPropertyBinding(comp, propName, initExpr,
					loadExprs.toArray(new String[loadExprs.size()]), 
					saveExprs.toArray(new String[saveExprs.size()]), 
					converterInfo==null?null:converterInfo.expr,
					validatorInfo==null?null:validatorInfo.expr,
					args,
					converterInfo==null?null:converterInfo.args, 
					validatorInfo==null?null:validatorInfo.args);
		}
	}

	private ConverterInfo parseConverter(ComponentCtrl compCtrl, Annotation anno) {
		ConverterInfo info = new ConverterInfo();
		for (final Iterator<?> it = anno.getAttributes().entrySet().iterator(); it
				.hasNext();) {
			final Map.Entry entry = (Map.Entry) it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = (String) tagExpr;
			} else { // other unknown tag, keep as arguments
				if (info.args == null) {
					info.args = new HashMap<String, Object>();
				}
				info.args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new UiException("Must specify a converter!");
		}
		info.args = info.args == null ? null : parsedArgs(info.args);
		return info;
	}

	private ValidatorInfo parseValidator(ComponentCtrl compCtrl, Annotation anno) {
		ValidatorInfo info = new ValidatorInfo();
		for (final Iterator<?> it = anno.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Map.Entry entry = (Map.Entry) it.next();
			final String tag = (String) entry.getKey();
			final Object tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = (String) tagExpr;
			} else { // other unknown tag, keep as arguments
				if (info.args == null) {
					info.args = new HashMap<String, Object>();
				}
				info.args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new UiException("Must specify a validator!");
		}
		info.args = info.args == null ? null : parsedArgs(info.args);
		return info;
	}

	private void addTagExpr(List<String> exprs, Object tagExpr) {
		if (tagExpr instanceof String[]) {
			for (String expr : (String[])tagExpr) {
				exprs.add(expr);
			}
		} else {
			exprs.add((String)tagExpr);
		}
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
