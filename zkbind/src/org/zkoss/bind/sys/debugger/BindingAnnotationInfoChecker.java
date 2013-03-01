/* BindingAnnotationInfoChecker.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.sys.debugger;

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;

/**
 * the checker to check runtime binding annotation and show warn by {@link BindingExecutionInfoCollector}
 * 
 * @author dennis
 * @since 6.5.2
 */
public interface BindingAnnotationInfoChecker {
	
	/**
	 * check binding annotation
	 */
	void checkBinding(Binder binder,Component comp);

	/**
	 * check view model annotation 
	 */
	void checkViewModel(Component comp);

	/**
	 * check binder annotation
	 */
	void checkBinder(Component comp);

	/**
	 * check validation message annotation
	 */
	void checkValidationMessages(Component comp);
}
