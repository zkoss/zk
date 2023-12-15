/* DefaultAnnotationInfoChecker.java

	Purpose:
		
	Description:
		
	History:
		2013/1/21 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.AnnoWarnInfo;
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

	private static final String BIND_ANNO = "bind";
	private static final String LOAD_ANNO = "load";
	private static final String SAVE_ANNO = "save";
	private static final String REFERENCE_ANNO = "ref";
	private static final String VALIDATOR_ANNO = "validator";
	private static final String CONVERTER_ANNO = "converter";
	private static final String TEMPLATE_ANNO = "template";
	private static final String COMMAND_ANNO = "command";
	private static final String GLOBAL_COMMAND_ANNO = "global-command";

	public static final String FORM_ATTR = "form";
	public static final String CHILDREN_ATTR = "children";

	//	private static final String QUEUE_NAME_ANNO_ATTR = "queueName";
	//	private static final String QUEUE_SCOPE_ANNO_ATTR = "queueScope";

	BindingExecutionInfoCollector _collector;
	private Class<?> _viewModelClass;

	DefaultAnnotationInfoChecker(BindingExecutionInfoCollector collector) {
		_collector = collector;
		if (_collector instanceof DefaultExecutionInfoCollector)
			((DefaultExecutionInfoCollector) _collector).setViewModelClass(getViewModelClass());
	}

	protected Class<?> getViewModelClass() {
		return _viewModelClass;
	}

	public void setViewModelClass(Class<?> viewModelClass) {
		_viewModelClass = viewModelClass;
	}

	public void checkBinding(Binder binder, Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for (String p : compCtrl.getAnnotatedProperties()) {
			if (BINDER_ATTR.equals(p) || VIEW_MODEL_ATTR.equals(p) || VALIDATION_MESSAGES_ATTR.equals(p)) {
				continue;
			}
			for (Annotation anno : compCtrl.getAnnotations(p)) {
				String nm = anno.getName();
				try {
					BinderUtil.pushContext().setCurrentLocation(anno.getLocation());
					if (p.startsWith("on")) {
						if (COMMAND_ANNO.equals(nm) || GLOBAL_COMMAND_ANNO.equals(nm)) {
							continue;
						}
						_collector.addInfo(new AnnoWarnInfo(comp, p, nm, "Unknow command annotation"));
					} else if (p.equals("form")) {
						if (ZKBIND1_ANNO.equals(nm) || ZKBIND2_ANNO.equals(nm) || BIND_ANNO.equals(nm)
								|| LOAD_ANNO.equals(nm) || SAVE_ANNO.equals(nm) || VALIDATOR_ANNO.equals(nm)
								|| CONVERTER_ANNO.equals(nm) || ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)) {
							continue;
						}
						_collector.addInfo(new AnnoWarnInfo(comp, p, nm, "Unknow form binding annotation"));
					} else {
						if (ZKBIND1_ANNO.equals(nm) || ZKBIND2_ANNO.equals(nm) || BIND_ANNO.equals(nm)
								|| LOAD_ANNO.equals(nm) || SAVE_ANNO.equals(nm) || VALIDATOR_ANNO.equals(nm)
								|| REFERENCE_ANNO.equals(nm) || CONVERTER_ANNO.equals(nm) || TEMPLATE_ANNO.equals(nm)
								|| INIT_ANNO.equals(nm)) {
							continue;
						}
						_collector.addInfo(new AnnoWarnInfo(comp, p, nm, "Unknow binding annotation"));
					}
				} finally {
					BinderUtil.popContext();
				}
			}
		}
	}

	public void checkViewModel(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;

		for (Annotation anno : compCtrl.getAnnotations(VIEW_MODEL_ATTR)) {
			String nm = anno.getName();
			if (ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)) {
				continue;
			}
			try {
				BinderUtil.pushContext().setCurrentLocation(anno.getLocation());
				_collector.addInfo(new AnnoWarnInfo(comp, VIEW_MODEL_ATTR, nm, "Unknow viewmodel annotation"));
			} finally {
				BinderUtil.popContext();
			}
		}

	}

	public void checkBinder(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for (Annotation anno : compCtrl.getAnnotations(BINDER_ATTR)) {
			String nm = anno.getName();
			if (ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)) {
				continue;
			}
			try {
				BinderUtil.pushContext().setCurrentLocation(anno.getLocation());
				_collector.addInfo(new AnnoWarnInfo(comp, BINDER_ATTR, nm, "Unknow binder annotation"));
			} finally {
				BinderUtil.popContext();
			}
		}
	}

	public void checkValidationMessages(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		for (Annotation anno : compCtrl.getAnnotations(VALIDATION_MESSAGES_ATTR)) {
			String nm = anno.getName();
			if (ID_ANNO.equals(nm) || INIT_ANNO.equals(nm)) {
				continue;
			}
			try {
				BinderUtil.pushContext().setCurrentLocation(anno.getLocation());
				_collector.addInfo(
						new AnnoWarnInfo(comp, VALIDATION_MESSAGES_ATTR, nm, "Unknow validation messages annotation"));
			} finally {
				BinderUtil.popContext();
			}
		}
	}

}
