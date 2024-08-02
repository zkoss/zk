/* B101_ZK_5764VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 02 12:50:22 CST 2024, Created by jameschu

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.reflect.Method;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.init.ViewModelAnnotationResolvers;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class B101_ZK_5764VM {
	@Command
	public void update() {
		try {
			Method update = ViewModelAnnotationResolvers.getOriginalMethod(this, this.getClass().getMethod("update"));
			Clients.log(update.getDeclaringClass().getName());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}