/* TimeZoneCommand.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 28 17:57:11 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.au.in;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * Used only by {@link AuRequest} to implement the update TimeZone command.
 * 
 * @author sam
 * @since 3.6.3
 */
public class TimeZoneCommand extends Command{
	public TimeZoneCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	//-- super --//
	protected void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		String result = data[0];
		((Updatable)((ComponentCtrl)comp).getExtraCtrl()).setResult(result);
	}	
}