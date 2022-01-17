/* B85_ZK_3949VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 14 12:52:50 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class B85_ZK_3949VM {
	private String image = "src/main/webapp/img/ZK-Logo.gif";

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Command
	public void showImage()
	{
		this.image = "src/main/webapp/img/ZK-Logo.gif";
		BindUtils.postNotifyChange(null, null, this, "image");
	}

	@Command
	public void hideImage()
	{
		this.image = null;
		BindUtils.postNotifyChange(null, null, this, "image");
	}
}
