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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.BinderUtil.UtilContext;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.lang.Strings;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * Helper class to parse binding annotations and create bindings. 
 * @author henrichen
 * @author dennischen
 * @since 6.0.0
 */
public class AnnotateBinderHelper {
	final private Binder _binder;
	
	final static private String INIT_ANNO = "init";
	final static private String BIND_ANNO = "bind";
	final static private String LOAD_ANNO = "load";
	final static private String SAVE_ANNO = "save";
	final static private String REFERENCE_ANNO = "ref";
	final static private String ID_ANNO = "id";
	final static private String VALIDATOR_ANNO = "validator";
	final static private String CONVERTER_ANNO = "converter";
	final static private String TEMPLATE_ANNO = "template";
	final static private String COMMAND_ANNO = "command";
	final static private String GLOBAL_COMMAND_ANNO = "global-command";
	
	final static public String FORM_ATTR = "form";
	final static public String VIEW_MODEL_ATTR = "viewModel";
	final static public String BINDER_ATTR = "binder";
	final static public String VALIDATION_MESSAGES_ATTR = "validationMessages";
	final static public String CHILDREN_ATTR = "children";
	
	//control key
	final static public String CHILDREN_KEY = "$CHILDREN$";
	
	
	public AnnotateBinderHelper(Binder binder) {
		_binder = binder;
	}
	
	public void initComponentBindings(Component comp) {
		processAllComponentsBindings(comp);
	}
	
	
	private void processAllComponentsBindings(Component comp) {
		final Binder selfBinder = BinderUtil.getBinder(comp);
		//check if a component was binded already(by any binder)
		if (selfBinder != null) //this component already binded ! skip all of its children
			return;
		
		BindingAnnotationInfoChecker checker = ((BinderCtrl)_binder).getBindingAnnotationInfoChecker();
		if(checker!=null){
			checker.checkBinding(_binder, comp);
		}
		
		processComponentBindings0(comp);
		for(final Iterator<Component> it = comp.getChildren().iterator(); it.hasNext();) {
			final Component kid = it.next();
			processAllComponentsBindings(kid); //recursive to each child
		}

		// support shadow element
		if (comp instanceof ComponentCtrl) {
			for (ShadowElement se : ((ComponentCtrl) comp).getShadowRoots()) {
				processAllComponentsBindings((Component)se);
			}
		}
	}
	
	private void processComponentBindings0(Component comp) {
		final List<String> props = AnnotationUtil.getNonSystemProperties(comp);// look every property has annotation
		for (final Iterator<?> it = props.iterator(); it.hasNext(); ) {
			final String propName = (String) it.next();
			if (isEventProperty(propName)) {
				processCommandBinding(comp,propName);
				processGlobalCommandBinding(comp,propName);
			}else if(FORM_ATTR.equals(propName)){
				processFormBindings(comp);
			}else if(CHILDREN_ATTR.equals(propName)){
				processChildrenBindings(comp);
			}else if(VIEW_MODEL_ATTR.equals(propName)){
				//ignore
			}else if(BINDER_ATTR.equals(propName)){
				//ignore
			}else if(VALIDATION_MESSAGES_ATTR.equals(propName)){
				//ignore
			}else{
				processPropertyBindings(comp, propName);
			}
		}
		//don't mark the component is controlled, if we do this, it will always create a attribute map for a component.
		//and consume more memory, make performance worse.
		//if(!BinderUtil.isHandling(comp)){
		//	BinderUtil.markHandling(comp, _binder);
		//}
	}

	private boolean isEventProperty(String propName) {
		return propName.startsWith("on") && propName.length() >= 3 && Character.isUpperCase(propName.charAt(2));
	}
	
	private void processCommandBinding(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Collection<Annotation> anncol = compCtrl.getAnnotations(propName, COMMAND_ANNO);
		if(anncol.size()==0) return;
		if(anncol.size()>1) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one command binding for event "+propName+" of "+comp,comp));
		}
		final Annotation ann = anncol.iterator().next();
		
		final Map<String,String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
		Map<String, String[]> args = null;
		final List<String> cmdExprs = new ArrayList<String>();
		for (final Iterator<Entry<String,String[]>> it = attrs.entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				cmdExprs.add(AnnotationUtil.testString(tagExpr,ann));
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		
		final Map<String,Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			for(String cmd : cmdExprs) {
				_binder.addCommandBinding(comp, propName, cmd, parsedArgs);
			}
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void processGlobalCommandBinding(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final Collection<Annotation> anncol = compCtrl.getAnnotations(propName, GLOBAL_COMMAND_ANNO);
		if(anncol.size()==0) return;
		if(anncol.size()>1) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one global-command binding for event "+propName+" of "+comp,comp));
		}
		final Annotation ann = anncol.iterator().next();
		
		final Map<String,String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
		Map<String, String[]> args = null;
		final List<String> cmdExprs = new ArrayList<String>();
		for (final Iterator<Entry<String,String[]>> it = attrs.entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				cmdExprs.add(AnnotationUtil.testString(tagExpr,ann));
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		
		final Map<String,Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			for(String cmd : cmdExprs) {
				_binder.addGlobalCommandBinding(comp, propName, cmd, parsedArgs);
			}
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void processPropertyBindings(Component comp, String propName) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		
		//validator and converter information
		ExpressionAnnoInfo validatorInfo = parseValidator(comp,propName);
		ExpressionAnnoInfo converterInfo = parseConverter(comp,propName);

		//scan init
		Collection<Annotation> initannos = compCtrl.getAnnotations(propName, INIT_ANNO);
		if(initannos.size()>1){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one @init for "+propName+" of "+comp,initannos.iterator().next()));
		}else if(initannos.size()==1){
			processPropertyInit(comp,propName,initannos.iterator().next(),converterInfo);
		}
		
		Collection<Annotation> annos = compCtrl.getAnnotations(propName); //init in the annotation with the sequence
		
		for(Annotation anno:annos){
			if(anno.getName().equals(BIND_ANNO)){
				processPropertyPromptBindings(comp,propName,anno,converterInfo,validatorInfo);
			}else if(anno.getName().equals(LOAD_ANNO)){
				processPropertyLoadBindings(comp,propName,anno,converterInfo);
			}else if(anno.getName().equals(SAVE_ANNO)){
				processPropertySaveBindings(comp,propName,anno,converterInfo,validatorInfo);
			}else if(anno.getName().equals(REFERENCE_ANNO)){
				processReferenceBinding(comp,propName,anno);
			}
		}

		ExpressionAnnoInfo templateInfo = parseTemplate(comp,propName);
		if(templateInfo!=null){
			_binder.setTemplate(comp, propName, templateInfo.expr, templateInfo.args);
		}
	}
	
	private void processReferenceBinding(Component comp, String propName, Annotation ann) {
		String loadExpr = null;
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = AnnotationUtil.testString(tagExpr,ann);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String,Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addReferenceBinding(comp, propName, loadExpr, parsedArgs);
		}finally{
			BinderUtil.popContext();
		}
		
	}
	
	private void processPropertyInit(Component comp, String propName, Annotation ann,ExpressionAnnoInfo converterInfo) {
		String initExpr = null;
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				initExpr = AnnotationUtil.testString(tagExpr,ann);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String,Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addPropertyInitBinding(comp, propName, initExpr, parsedArgs, converterInfo == null ? null : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	//process @bind(expr) 
	private void processPropertyPromptBindings(Component comp, String propName, Annotation ann, ExpressionAnnoInfo converterInfo, ExpressionAnnoInfo validatorInfo) {
		String expr = null;
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				expr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("@bind is for prompt binding only, doesn't support before commands, check property "+propName+" of "+comp,ann));
			} else if ("after".equals(tag)) {
				throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("@bind is for prompt binding only, doesn't support after commands, check property "+propName+" of "+comp,ann));
			}  else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
			
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);

		try{
			UtilContext ctx = BinderUtil.pushContext();
			ctx.setIgnoreAccessCreationWarn(true);
			ctx.setCurrentLocation(ann.getLocation());
			
			_binder.addPropertyLoadBindings(comp, propName,
					expr, null, null, parsedArgs, 
					converterInfo == null ? null : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
			
			_binder.addPropertySaveBindings(comp, propName, expr,
					null, null, parsedArgs, 
					converterInfo == null ? null : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args, 
					validatorInfo == null ? null : validatorInfo.expr, 
					validatorInfo == null ? null : validatorInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void addCommand(Component comp, List<String> cmds, String[] cmdExprs){
		for(String cmdExpr:(String[])cmdExprs){
			addCommand(comp,cmds,cmdExpr);
		}
	}
	private void addCommand(Component comp, List<String> cmds, String cmdExpr){
		String cmd = BindEvaluatorXUtil.eval(_binder.getEvaluatorX(),comp,cmdExpr,String.class);
		if(Strings.isEmpty(cmd)){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("command of expression "+cmdExpr+" is empty",comp));
		}
		cmds.add(cmd);
	}
	
	private void processPropertyLoadBindings(Component comp, String propName, Annotation ann, ExpressionAnnoInfo converterInfo) {
		String loadExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
		
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addPropertyLoadBindings(comp, propName,
					loadExpr, 
					beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
					afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), parsedArgs, 
					converterInfo == null ? null : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}

	private void processPropertySaveBindings(Component comp, String propName, Annotation ann, ExpressionAnnoInfo converterInfo, ExpressionAnnoInfo validatorInfo) {
		String saveExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				saveExpr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addPropertySaveBindings(comp, propName,saveExpr, 
					beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
					afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), parsedArgs, 
					converterInfo == null ? null : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args,
					validatorInfo == null ? null : validatorInfo.expr, 
					validatorInfo == null ? null : validatorInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void processFormBindings(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final BindEvaluatorX eval = _binder.getEvaluatorX();
		//validator information
		ExpressionAnnoInfo validatorInfo = parseValidator(comp,FORM_ATTR);
		
		String formId = null;
		
		Collection<Annotation> idannos = compCtrl.getAnnotations(FORM_ATTR, ID_ANNO);
		if(idannos.size()==0){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("@id is not found for a form binding of "+comp,comp));
		}else if(idannos.size()>1){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one @id for a form binding of "+comp,idannos.iterator().next()));
		}
		
		final Annotation idanno = idannos.iterator().next();
		final String idExpr = idanno.getAttribute("value");
		
		if(idExpr!=null){
			formId = BindEvaluatorXUtil.eval(eval, comp, idExpr, String.class);
		}
		if(formId==null){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("value of @id is not found for a form binding of "+compCtrl+", exprssion is "+idExpr,idanno));
		}
		
		//scan init first
		Collection<Annotation> initannos = compCtrl.getAnnotations(FORM_ATTR, INIT_ANNO);
		if(initannos.size()>1){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one @init for "+FORM_ATTR+" of "+comp,initannos.iterator().next()));
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
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				initExpr = AnnotationUtil.testString(tagExpr,ann);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addFormInitBinding(comp, formId,initExpr, parsedArgs);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void processFormLoadBindings(Component comp, String formId,Annotation ann) {
		String loadExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds, tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds, tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addFormLoadBindings(comp, formId,
					loadExpr, 
					beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
					afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), parsedArgs);
		}finally{
			BinderUtil.popContext();
		}		
	}
	
	private void processFormSaveBindings(Component comp, String formId, Annotation ann, ExpressionAnnoInfo validatorInfo) {
		String saveExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				saveExpr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addFormSaveBindings(comp, formId, saveExpr, 
					beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
					afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), parsedArgs, 
					validatorInfo == null ? null : validatorInfo.expr, 
					validatorInfo == null ? null : validatorInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	
	private void processChildrenBindings(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		ExpressionAnnoInfo converterInfo = parseConverter(comp,CHILDREN_ATTR);
		//scan init first
		Collection<Annotation> initannos = compCtrl.getAnnotations(CHILDREN_ATTR, INIT_ANNO);
		if(initannos.size()>1){
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one @init for "+CHILDREN_ATTR+" of "+comp,initannos.iterator().next()));
		}else if(initannos.size()==1){
			processChildrenInit(comp,initannos.iterator().next(),converterInfo);
		}
		
		Collection<Annotation> annos = compCtrl.getAnnotations(CHILDREN_ATTR); //get all annotation in the children with the order.

		for(Annotation anno:annos){
			if(anno.getName().equals(BIND_ANNO)){
				processChildrenPromptBindings(comp,anno,converterInfo);
			}else if(anno.getName().equals(LOAD_ANNO)){
				processChildrenLoadBindings(comp,anno,converterInfo);
			}
		}

		ExpressionAnnoInfo templateInfo = parseTemplate(comp,CHILDREN_ATTR);
		if(templateInfo!=null){
			//use special CHILDREN_KEY to avoid conflict 
			_binder.setTemplate(comp, CHILDREN_KEY, templateInfo.expr, templateInfo.args);
		}
	}
	
	private void processChildrenInit(Component comp, Annotation ann,ExpressionAnnoInfo converterInfo) {
		String initExpr = null;
			
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				initExpr = AnnotationUtil.testString(tagExpr,ann);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String,Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addChildrenInitBinding(comp, initExpr, parsedArgs,
					converterInfo == null ? getDefaultChildBindingConverter() : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private String getDefaultChildBindingConverter(){
		if(SystemConverters.get("childrenBinding")!=null){
			return "'childrenBinding'";
		}
		return null;
	}
	
	private void processChildrenPromptBindings(Component comp, Annotation ann,ExpressionAnnoInfo converterInfo) {
		String expr = null;
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				expr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("@bind is for prompt binding only, doesn't support before commands, check property "+CHILDREN_ATTR+" of "+comp,comp));
			} else if ("after".equals(tag)) {
				throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("@bind is for prompt binding only, doesn't support after commands, check property "+CHILDREN_ATTR+" of "+comp,comp));
			}  else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
			
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addChildrenLoadBindings(comp, expr, null, null, parsedArgs,
					converterInfo == null ? getDefaultChildBindingConverter() : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	private void processChildrenLoadBindings(Component comp, Annotation ann, ExpressionAnnoInfo converterInfo){
		String loadExpr = null;
		final List<String> beforeCmds = new ArrayList<String>();
		final List<String> afterCmds = new ArrayList<String>();
		
		Map<String, String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				loadExpr = AnnotationUtil.testString(tagExpr,ann);
			} else if ("before".equals(tag)) {
				addCommand(comp,beforeCmds,tagExpr);
			} else if ("after".equals(tag)) {
				addCommand(comp,afterCmds,tagExpr);
			} else { //other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		final Map<String, Object> parsedArgs = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		try{
			BinderUtil.pushContext().setCurrentLocation(ann.getLocation());
			_binder.addChildrenLoadBindings(comp, loadExpr, 
					beforeCmds.size()==0?null:beforeCmds.toArray(new String[beforeCmds.size()]),
					afterCmds.size()==0?null:afterCmds.toArray(new String[afterCmds.size()]), parsedArgs,
					converterInfo == null ? getDefaultChildBindingConverter() : converterInfo.expr, 
					converterInfo == null ? null : converterInfo.args);
		}finally{
			BinderUtil.popContext();
		}
	}
	
	

	private ExpressionAnnoInfo parseConverter(Component comp, String propName) {
		final Collection<Annotation> annos = ((ComponentCtrl)comp).getAnnotations(propName, CONVERTER_ANNO);
		if(annos.size()==0) return null;
		if(annos.size()>1) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one converter for "+propName+" of "+comp,comp));
		}
		final Annotation ann = annos.iterator().next();
		
		ExpressionAnnoInfo info = new ExpressionAnnoInfo();
		Map<String,String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it
				.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = AnnotationUtil.testString(tagExpr,ann);
			} else { // other unknown tag, keep as arguments
				if (args== null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("value of converter is empty, check "+propName+" of "+comp,comp));
		}
		info.args = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		return info;
	}

	private ExpressionAnnoInfo parseValidator(Component comp, String propName) {
		final Collection<Annotation> annos = ((ComponentCtrl)comp).getAnnotations(propName, VALIDATOR_ANNO);
		if(annos.size()==0) return null;
		if(annos.size()>1) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one validator for "+propName+" of "+comp,comp));
		}
		final Annotation ann = annos.iterator().next();
		ExpressionAnnoInfo info = new ExpressionAnnoInfo();
		Map<String,String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = AnnotationUtil.testString(tagExpr,ann);
			} else { // other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("value of validator is empty, check "+propName+" of "+comp,comp));
		}
		info.args = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		return info;
	}
	
	private ExpressionAnnoInfo parseTemplate(Component comp, String propName) {
		final Collection<Annotation> annos = ((ComponentCtrl)comp).getAnnotations(propName, TEMPLATE_ANNO);
		if(annos.size()==0) return null;
		if(annos.size()>1) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Allow only one template for "+propName+" of "+comp,comp));
		}
		final Annotation ann = annos.iterator().next();
		ExpressionAnnoInfo info = new ExpressionAnnoInfo();
		Map<String,String[]> args = null;
		for (final Iterator<Entry<String,String[]>> it = ann.getAttributes().entrySet().iterator(); it.hasNext();) {
			final Entry<String,String[]> entry = it.next();
			final String tag = entry.getKey();
			final String[] tagExpr = entry.getValue();
			if ("value".equals(tag)) {
				info.expr = AnnotationUtil.testString(tagExpr,ann);
			} else { // other unknown tag, keep as arguments
				if (args == null) {
					args = new HashMap<String, String[]>();
				}
				args.put(tag, tagExpr);
			}
		}
		if (Strings.isBlank(info.expr)) {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage("Must specify a template for "+propName+" of "+comp,comp));
		}
		info.args = args == null ? null : BindEvaluatorXUtil.parseArgs(_binder.getEvaluatorX(),args);
		return info;
	}
	
	private static class ExpressionAnnoInfo{
		Map<String, Object> args;
		String expr;
	}
}
