/* DefaultAnnotationInfoChecker.java

	Purpose:
		
	Description:
		
	History:
		2013/1/21 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * @author dennis
 *
 */
public class DefaultAnnotationInfoChecker implements BindingAnnotationInfoChecker {

	private static final String ZKBIND1_ANNO = "default-bind";
	private static final String ZKBIND2_ANNO = "ZKBIND";
	
	private static final String ID_ANNO = "id";
	private static final String INIT_ANNO = "init";
	
	private static final String VIEW_MODEL_ATTR = "viewModel";
	private static final String BINDER_ATTR = "binder";
	private static final String VALIDATION_MESSAGES_ATTR = "validationMessages";
	
	
	final static private String BIND_ANNO = "bind";
	final static private String LOAD_ANNO = "load";
	final static private String SAVE_ANNO = "save";
	final static private String REFERENCE_ANNO = "ref";
	final static private String VALIDATOR_ANNO = "validator";
	final static private String CONVERTER_ANNO = "converter";
	final static private String TEMPLATE_ANNO = "template";
	final static private String COMMAND_ANNO = "command";
	final static private String GLOBAL_COMMAND_ANNO = "global-command";
	
	final static public String FORM_ATTR = "form";
	final static public String CHILDREN_ATTR = "children";
	
	private static final String QUEUE_NAME_ANNO_ATTR = "queueName";
	private static final String QUEUE_SCOPE_ANNO_ATTR = "queueScope";
	
	BindingExecutionInfoCollector _collector;
	
	DefaultAnnotationInfoChecker(BindingExecutionInfoCollector collector){
		_collector = collector;
	}
	
	@Override
	public void checkBinding(Binder binder, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for(String p:compCtrl.getAnnotatedProperties()){
			if(BINDER_ATTR.equals(p) || VIEW_MODEL_ATTR.equals(p) || VALIDATION_MESSAGES_ATTR.equals(p)){
				continue;
			}
			for(Annotation anno:compCtrl.getAnnotations(p)){
				String nm = anno.getName();
				if(p.startsWith("on")){
					if(COMMAND_ANNO.equals(nm) || GLOBAL_COMMAND_ANNO.equals(nm)){
						continue;
					}
					_collector.addInfo(new WarnInfo(comp, "unknow-command-annotation", p, nm, ""));
				}else if(p.equals("form")){
					if(ZKBIND1_ANNO.equals(nm) || ZKBIND2_ANNO.equals(nm) || 
							BIND_ANNO.equals(nm) || LOAD_ANNO.equals(nm) || SAVE_ANNO.equals(nm) || VALIDATOR_ANNO.equals(nm)
							|| CONVERTER_ANNO.equals(nm) || ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)){
						continue;
					}
					_collector.addInfo(new WarnInfo(comp, "unknow-binding-annotation", p, nm, ""));
				}else{
					if(ZKBIND1_ANNO.equals(nm) || ZKBIND2_ANNO.equals(nm) || 
							BIND_ANNO.equals(nm) || LOAD_ANNO.equals(nm) || SAVE_ANNO.equals(nm) || VALIDATOR_ANNO.equals(nm)
							|| REFERENCE_ANNO.equals(nm) || CONVERTER_ANNO.equals(nm) || TEMPLATE_ANNO.equals(nm) || INIT_ANNO.equals(nm)){
						continue;
					}
					_collector.addInfo(new WarnInfo(comp, "unknow-binding-annotation", p, nm, ""));
				}
				
			}
		}
	}

	@Override
	public void checkViewModel(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		
		for(Annotation anno:compCtrl.getAnnotations(VIEW_MODEL_ATTR)){
			String nm = anno.getName();
			if(ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)){
				continue;
			}
			_collector.addInfo(new WarnInfo(comp, "unknow-viewmodel-annotation", VIEW_MODEL_ATTR, nm, ""));
		}
		
	}

	@Override
	public void checkBinder(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for(Annotation anno:compCtrl.getAnnotations(BINDER_ATTR)){
			String nm = anno.getName();
			if(ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)){
				continue;
			}
			_collector.addInfo(new WarnInfo(comp, "unknow-binder-annotation", BINDER_ATTR, nm, ""));
		}
	}

	@Override
	public void checkValidationMessages(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for(Annotation anno:compCtrl.getAnnotations(VALIDATION_MESSAGES_ATTR)){
			String nm = anno.getName();
			if(ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)){
				continue;
			}
			_collector.addInfo(new WarnInfo(comp, "unknow-vmsgs-annotation", VALIDATION_MESSAGES_ATTR, nm, ""));
		}
	}

}
