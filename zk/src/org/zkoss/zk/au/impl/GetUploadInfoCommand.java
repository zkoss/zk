/* GetUploadInfoCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 21:40:53     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import com.potix.lang.Objects;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.impl.Attributes;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.AuScript;
import com.potix.zk.au.Command;

/**
 * Used with {@link AuRequest} to return the ratio (aka., the progress)
 * of the current uploading.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class GetUploadInfoCommand extends Command {
	public GetUploadInfoCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final String[] data = request.getData();
		if (data != null && data.length != 0)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});

		final Desktop dt = request.getDesktop();
		final Integer p = (Integer)dt.getAttribute(Attributes.UPLOAD_PERCENT);
		final Long cb = (Long)dt.getAttribute(Attributes.UPLOAD_SIZE);
		final AuScript response = new AuScript(null,
			"zkau.updateUploadInfo("
				+(p != null ? p.intValue(): 0)+','
				+(cb != null ? cb.longValue(): 0)+')');
		Executions.getCurrent()
			.addAuResponse(response.getCommand(), response);
	}
}
