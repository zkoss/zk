/* AuSyncEbox.java

        Purpose:
        
        Description:
        
        History:
                Wed May 23 5:12 PM:55 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;

/**
 * A response to ask the client to sync errorboxes in the desktop
 *
 *  @author klyve
 * @since 8.5.2
 */
public class AuSyncErrorbox extends AuResponse {
	/** A constructor for asking the client to sync the position all the errorboxes to its widget.
	 * @since 8.5.2
	 */
	public AuSyncErrorbox() {
		super("syncAllErrorbox");
	}
	/** A constructor for asking the client to sync position of a widget and its the errorbox.
	 * @param comp a component which is errorbox position would be sync.
	 * @since 8.5.2
	 */
	public AuSyncErrorbox(Component comp) {
		super("syncErrorbox", comp);
	}
	
}
