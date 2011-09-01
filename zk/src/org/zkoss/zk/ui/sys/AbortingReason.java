/* AbortingReason.java

	Purpose:
		
	Description:
		
	History:
		Sun Apr 30 18:11:01     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.au.AuResponse;

/**
 * The aborting reason used to abort the current execution.
 *
 * @author tomyeh
 */
public interface AbortingReason {
	/** Returns whether to abort the execution.
	 */
	public boolean isAborting();
	/** Called to process this aborting reason.
	 * It is called after {@link UiEngine} processes all events,
	 * before generating the responses to the client.
	 *
	 * <p>Note: it is always called even if {@link #isAborting} returns false.
	 *
	 * <p>Note: {@link #execute} is called before generating
	 * the responses to the client, while {@link #getResponse} is called
	 * when generating the responses.
	 *
	 * <p>The calling sequence: {@link #execute}, {@link #getResponse},
	 * and then {@link #finish}.
	 *
	 * @since 3.0.0
	 * @see #getResponse
	 */
	public void execute();
	/** Returns the response representing this aborting reason.
	 * It is called when {@link UiEngine} generates the responses to
	 * client. Actually, it is called as the last step after
	 * generating all other responses. In other words, the response
	 * returned by this method, if any, will become the last response.
	 *
	 * <p>Note: don't modify the components in {@link #getResponse}, since
	 * it is called after all other responses are generated.
	 *
	 * <p>Note: it is always called even if {@link #isAborting} returns false.
	 *
	 * <p>Note: {@link #execute} is called before generating
	 * the responses to the client, while {@link #getResponse} is called
	 * when generating the responses.
	 *
	 * @return the response to generate, or null if no response to generate.
	 * @see #execute
	 */
	public AuResponse getResponse();
	/** Called after all processing is done, and just before deactivating
	 * the execution.
	 *
	 * <p>Note: it is always called even if {@link #isAborting} returns false.
	 *
	 * <p>The calling sequence: {@link #execute}, {@link #getResponse},
	 * and then {@link #finish}.
	 *
	 * @since 3.0.2
	 */
	public void finish();
}
