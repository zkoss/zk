/* F104_ZK_6086VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:04:09 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;

public class F104_ZK_6086VM {
	private String code = "line 1\nline 2";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		// echo the saved value back so the bound label reloads, proving the
		// onChange two-way save round-tripped through the view model.
		BindUtils.postNotifyChange(null, null, this, "code");
	}
}
