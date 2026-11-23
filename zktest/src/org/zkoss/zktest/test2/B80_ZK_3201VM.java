/* B80_ZK_3201VM.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep  8, 2016 12:37:12 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
/**
 * 
 * @author Sefi
 */
public class B80_ZK_3201VM extends B80_ZK_3201BaseVM<String>{
	@Command
	@Override
	public void editRecord(@BindingParam("record") String record) {
		super.editRecord(record);
	}
}
