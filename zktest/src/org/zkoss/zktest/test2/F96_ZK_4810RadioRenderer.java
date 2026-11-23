/* F96_ZK_4810RadioRenderer.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 15 10:31:39 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.Radio;
import org.zkoss.zul.RadioRenderer;

public class F96_ZK_4810RadioRenderer implements RadioRenderer<String> {
	public void render(Radio radio, String data, int index) throws Exception {
		radio.setLabel("renderer[" + data + "]");
		radio.setValue(data);
	}
}
