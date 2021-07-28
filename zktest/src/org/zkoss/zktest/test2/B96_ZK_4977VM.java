/* B96_ZK_4977VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 28 16:46:48 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B96_ZK_4977VM {
	private String profile;

	public String getProfile() {
		return profile;
	}

	@MatchMedia("all and (min-width: 769px)")
	@NotifyChange("profile")
	public void desktop() {
		profile = "desktop";
	}

	@MatchMedia("all and (max-width: 768px) and (min-width: 415px)")
	@NotifyChange("profile")
	public void tablet() {
		profile = "tablet";
	}

	@MatchMedia("all and (max-width: 414px)")
	@NotifyChange("profile")
	public void mobile() {
		profile = "mobile";
	}
}
