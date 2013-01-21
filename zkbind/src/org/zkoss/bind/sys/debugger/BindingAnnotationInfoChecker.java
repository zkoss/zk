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
 * the collector to collect runtime binding execution information
 * 
 * @author dennis
 * @since 6.5.2
 */
public interface BindingAnnotationInfoChecker {
	
	void checkBinding(Binder binder,Component comp);

	void checkViewModel(Component comp);

	void checkBinder(Component comp);

	void checkValidationMessages(Component comp);
}
