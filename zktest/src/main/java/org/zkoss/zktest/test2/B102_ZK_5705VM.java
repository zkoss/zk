/* B102_ZK_5705VM.java

	Purpose:
		
	Description:
		
	History:
		10:19 AM 2025/4/22, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.LocalDateTime;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * @author jumperchen
 */
public class B102_ZK_5705VM {
	private MyBean myBean = new MyBean();

	@Init
	public void init() {
		myBean.text = "Initial";
	}

	@Command("refresh")
	public void refresh() {
		myBean.text = "Updated" + LocalDateTime.now();
		BindUtils.postNotifyChange(this, "myBean");
	}

	public MyBean getMyBean() {
		return myBean;
	}


	public static class MyBean {
		public String text;
		public String getText() {
			return text;
		}
	}

}