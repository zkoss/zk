/* F104_ZK_6097_AvatarVM.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6097_AvatarVM {

	private String avatarLabel = "AB";
	private String avatarSrc = "";
	private String shape = "circle";
	private String size = "medium";

	@Init
	public void init() {
	}

	public String getAvatarLabel() { return avatarLabel; }
	public String getAvatarSrc() { return avatarSrc; }
	public String getShape() { return shape; }
	public String getSize() { return size; }

	@Command
	@NotifyChange({"avatarLabel", "avatarSrc"})
	public void showImage() {
		avatarSrc = "/img/ZK-Logo.gif";
		avatarLabel = "";
	}

	@Command
	@NotifyChange({"avatarLabel", "avatarSrc"})
	public void showLabel() {
		avatarSrc = "";
		avatarLabel = "JD";
	}

	@Command
	@NotifyChange("shape")
	public void setSquare() {
		shape = "square";
	}

	@Command
	@NotifyChange("size")
	public void setLarge() {
		size = "large";
	}
}
