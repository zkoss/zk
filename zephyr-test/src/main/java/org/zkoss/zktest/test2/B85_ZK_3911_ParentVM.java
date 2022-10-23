/* B85_ZK_3911_ParentVM.java

        Purpose:
                
        Description:
                
        History:
                Mon May 28 17:28:06 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyChange;

public class B85_ZK_3911_ParentVM {
	private String label;
	
	@MatchMedia("all and (min-width: 769px)")
	@NotifyChange("label")
	public void desktop() {
		label = "desktop";
	}
	
	@MatchMedia("all and (max-width: 768px) and (min-width: 415px)")
	@NotifyChange("label")
	public void tablet() {
		label = "tablet";
	}
	
	@MatchMedia("all and (max-width: 414px)")
	@NotifyChange("label")
	public void mobile() {
		label = "mobile";
	}
	
	public String getLabel() {
		return label;
	}
}
