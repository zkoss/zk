/* AnnotateDataBinderInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 30 17:41:15     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;

/**
 * <p>This initiator class do following things:</p>
 * <ol>
 * <li>new an {@link AnnotateDataBinder} instance.</li>
 * <li>set the AnnotateDataBinder instance as a variable "binder" in the ID Space of the root 
 *  component as specified in arg0 "component-path".(if arg0 is not specified, use Page instead.)</li>
 * <li>call {@link AnnotateDataBinder#loadAll()} to initiate all UI components from the associated data bean.</li>
 * </ol>
 * <p>Put the init PI as follows:</p>
 * <pre>
 * &lt;?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit" [arg0="component|component-path"] [arg1="true|false"]?>
 * </pre>
 * <p>Where the arg0 is the component itself (via EL expression) or the component path that specifies the component the AnnotateDataBinder covers. If the 
 * arg0 is not specified or set to string "page", the AnnotateDataBinder will default to cover the whole page.</p>
 * <p>Where the arg1 is used to decide whether to load default binding configuration defined in lang-addon.xml. 
 * If the arg1 is not specified it is default to true. Note that you have to specify arg0 if you want to specify arg1.</p>
 *
 * @author Henri Chen
 * @see AnnotateDataBinder
 */
public class AnnotateDataBinderInit implements Initiator {
	private Component _comp;
	private String _compPath;
	private String _defaultConfig;
	
	/** The AnnotateDataBinder created in doAfterCompose() */
	protected AnnotateDataBinder _binder; 
	
	//-- Initator --//
	public void doAfterCompose(Page page) {
		boolean b = _defaultConfig != null ? Boolean.valueOf(_defaultConfig).booleanValue() : true;
		if (_comp instanceof Component) {
			_binder = new AnnotateDataBinder(_comp, b);
			_comp.setVariable("binder", _binder, true);
		} else if (_compPath == null || "page".equals(_compPath)) {
			_binder = new AnnotateDataBinder(page, b);
			page.setVariable("binder", _binder);
		} else if (_compPath.startsWith("/")) {
			final Component comp = Path.getComponent(_compPath);
			_binder = new AnnotateDataBinder(comp, b);
			comp.setVariable("binder", _binder, true);
		} else {
			final Component comp = page.getFellow(_compPath);
			_binder = new AnnotateDataBinder(comp, b);
			comp.setVariable("binder", _binder, true);
		}
		_binder.loadAll(); //load data bean properties into UI components
	}
 	public boolean doCatch(java.lang.Throwable ex) {
 		return false; // do nothing
 	}
	public void doFinally() {
		// do nothing
	}
	public void doInit(Page page, java.lang.Object[] args) {
		if (args.length > 0) {
			Object arg0 = args[0];
			if (arg0 instanceof String) {
				_compPath = (String) arg0;
			} else if (arg0 instanceof Component) {
				_comp = (Component) arg0;
			} else if (!(arg0 instanceof Page)) {
				throw new UiException("arg0 has to be String, Component, or Page: "+arg0);
			}
		}
		
		if (args.length > 1) {
			_defaultConfig = (String)args[1];
		}
	}
}
