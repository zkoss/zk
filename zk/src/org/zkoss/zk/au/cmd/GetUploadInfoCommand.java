/* GetUploadInfoCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 21:40:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.out.AuScript;

/**
 * Used with {@link AuRequest} to return the ratio (aka., the progress)
 * of the current uploading.
 *
 * @author tomyeh
 * @since 3.0.0
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
