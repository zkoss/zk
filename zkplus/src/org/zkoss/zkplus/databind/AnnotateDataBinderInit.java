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
import org.zkoss.zk.ui.util.Initiator;

/**
 * <p>This initiator class do following things:</p>
 * <ol>
 * <li>new an {@link AnnotateDataBinder} instance.</li>
 * <li>set the AnnotateDataBinder instance as a page variable "binder".</li>
 * <li>call {@link AnnotateDataBinder#loadAll()} to initiate all UI components from the associated data bean.</li>
 * </ol>
 * <p>Put the init PI as follows:</p>
 * <pre>
 * &lt;?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit" [arg0="component-path"] ?>
 * </pre>
 * <p>Where the arg0 is the component path that specifies the component the AnnotateDataBinder covers. If the 
 * arg0 is not specified, the AnnotateDataBinder will default to cover the whole page.</p>
 *
 * @author Henri Chen
 * @see AnnotateDataBinder
 */
public class AnnotateDataBinderInit implements Initiator {
	private String _compPath;
	
	//-- Initator --//
	public void doAfterCompose(Page page) {
		AnnotateDataBinder binder = _compPath == null ?
			new AnnotateDataBinder(page) :  _compPath.startsWith("/") ? 
			new AnnotateDataBinder(Path.getComponent(_compPath)) : 
			new AnnotateDataBinder(page.getFellow(_compPath));
		page.setVariable("binder", binder);
	    binder.loadAll(); //load data bean properties into UI components
	}
 	public void doCatch(java.lang.Throwable ex) {
 		// do nothing
 	}
	public void doFinally() {
		// do nothing
	}
	public void doInit(Page page, java.lang.Object[] args) {
		if (args.length > 0) {
			_compPath = (String)args[0];
		}
	}
}
