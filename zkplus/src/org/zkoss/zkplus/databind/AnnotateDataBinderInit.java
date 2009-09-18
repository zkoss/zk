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

import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.InitiatorExt;

/**
 * <p>This initiator class do following things:</p>
 * <ol>
 * <li>New an {@link AnnotateDataBinder} instance.</li>
 * <li>Set the AnnotateDataBinder instance as a custom attribute with the name as specified in 
 * arg2 (default to "binder") and store it in the component as specified in arg0 "component-path".(if arg0 is not 
 * specified, use Page instead.)</li>
 * <li>call {@link AnnotateDataBinder#loadAll()} in to {@link #doAfterCompose(Page)} and
 * initiate all UI components from the associated data bean.</li>
 * </ol>
 * <p>Put the init PI as follows:</p>
 * <pre>
 * &lt;?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit" 
 * 	[root="component|component-path"] 
 *  [loadDefault="true|false"]
 *  [name="binder's name"] ?>
 * </pre>
 * <p>Where the root attribute is the component itself (via EL expression) or the component path that 
 * specifies the component the AnnotateDataBinder covers. You can use absolute path that 
 * starts with "//" (search from Desktop) or "/" (search from Page); or you can use relative
 * path(supported since ZK 3.0.8) that starts with "./" or "../" (relative to the Id Space of 
 * the page's root components). If the root attribute is not specified or set to string "page", 
 * the AnnotateDataBinder will default to cover the whole page.</p>
 * <p>Where the loadDefault attribute is used to decide whether to load default binding configuration defined in lang-addon.xml. 
 * If the loadDefault attribute is not specified it is default to true.</p>
 * <p>(since 3.6.2) Where the name attribute is used to specify the created DataBinder's name (default to "binder")
 * which you can access it via EL or component.getVariable() later.
 *
 * <p>For application design to run ZK prior to 3.6.2, it can use arg0 instead
 * of root, and arg1 instead of loadDefault.
 *
 * @author Henri Chen
 * @see AnnotateDataBinder
 */
 public class AnnotateDataBinderInit implements Initiator, InitiatorExt {
	private Component _comp;
	private String _compPath;
	private String _defaultConfig;
	private String _name;
	
	/** The AnnotateDataBinder created in doAfterCompose() */
	protected AnnotateDataBinder _binder; 
	
	//-- Initator --//
	public void doAfterCompose(Page page) {
		//will not be called since we implements InitiatorExt
	}
 	public boolean doCatch(java.lang.Throwable ex) {
 		return false; // do nothing
 	}
	public void doFinally() {
		// do nothing
	}
	public void doInit(Page page, Map args) {
		Object arg0 = args.get("root");
		if (arg0 == null) {
			arg0 = args.get("arg0"); //backward compatible
		}
		if (arg0 != null) {
			if (arg0 instanceof String) {
				_compPath = (String) arg0;
			} else if (arg0 instanceof Component) {
				_comp = (Component) arg0;
			} else if (!(arg0 instanceof Page)) {
				throw new UiException("arg0/root has to be String, Component, or Page: "+arg0);
			}
		}
		
		_defaultConfig = (String)args.get("loadDefault");
		if (_defaultConfig == null) {
			_defaultConfig = (String)args.get("arg1"); //backward compatible
		}
		
		_name = (String) args.get("name");
		if (_name == null) {
			_name = "binder";
		}
	}
	
	//-- InitiatorExt --//
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		boolean b = _defaultConfig != null ? Boolean.valueOf(_defaultConfig).booleanValue() : true;
		if (_comp instanceof Component) { //a specified component instance
			_binder = new AnnotateDataBinder(_comp, b);
			_comp.setAttribute(_name, _binder);
		} else if (_compPath == null || "page".equals(_compPath)) { //page
			_binder = new AnnotateDataBinder(page, b);
			if (page.getAttribute(_name) != null) { //already a binder on the page
				throw new UiException("Page is already covered by another Data Binder. Cannot be covered by this Data Binder again. Page:"+page.getId());
			} else {
				page.setAttribute(_name, _binder);
			}
		} else if (_compPath.startsWith("/")) { //absolute path
			final Component comp = Path.getComponent(_compPath);
			if (comp == null) {
				throw new UiException("Cannot find the specified component. Absolute Path:"+_compPath);
			}
			_binder = new AnnotateDataBinder(comp, b);
			comp.setAttribute(_name, _binder);
		} else if (_compPath.startsWith("./") || _compPath.startsWith("../")) { //relative path
			for (int j = 0; j < comps.length; ++j) {
				final Component vroot = comps[j];
				final Component comp = Path.getComponent(vroot.getSpaceOwner(), _compPath);
				if (comp != null) { //found
					_binder = new AnnotateDataBinder(comp, b);
					comp.setAttribute(_name, _binder);
					break;
				}
			}
			if (_binder == null) {
				throw new UiException("Cannot find the specified component. Relative Path:"+_compPath);
			}
		} else {
			final Component comp = page.getFellow(_compPath);
			_binder = new AnnotateDataBinder(comp, b);
			comp.setAttribute(_name, _binder);
		}
		_binder.loadAll(); //load data bean properties into UI components
	}
}
