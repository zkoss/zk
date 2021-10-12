package org.zkoss.zktest.test2;

import org.zkoss.zul.Radio;
import org.zkoss.zul.RadioRenderer;

public class F60_ZK_669_RadioRenderer implements RadioRenderer<String> {
	public void render(Radio item, String data, int index) throws Exception {
		item.setLabel("renderer_"+data+"_"+index);
		item.setValue(data);
	}
}
